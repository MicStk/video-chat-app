package at.ac.tuwien.sepm.groupphase.backend.transcription;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.AudioChunk;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.entity.Meeting;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.AudioChunkRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Component
public class Transcriptor extends Thread {

    private final Object commonObject = new Object();
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final ContainerTranscriptRepository containerTranscriptRepository;
    private final MessageRepository messageRepository;
    private final AudioChunkRepository audioChunkRepository;

    @Value("${ffmpeg-paths.ffmpeg}")
    private String ffmpegPath;

    @Value("${ffmpeg-paths.ffprobe}")
    private String ffprobePath;

    @PostConstruct
    public void init() {
        start();
    }

    public Transcriptor(MeetingRepository meetingRepository, UserRepository userRepository, ContainerTranscriptRepository containerTranscriptRepository, MessageRepository messageRepository, AudioChunkRepository audioChunkRepository) {
        this.meetingRepository = meetingRepository;
        this.userRepository = userRepository;
        this.containerTranscriptRepository = containerTranscriptRepository;
        this.messageRepository = messageRepository;
        this.audioChunkRepository = audioChunkRepository;
    }

    @Override
    public void run() {
        LOGGER.info("TRANSCRIPTOR: started");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            LOGGER.warn("TRANSCRIPTOR: was interrupted", e);
        }

        Thread transcriptorWorkerThread;

        while (true) {
            transcriptorWorkerThread = new TranscriptorWorker(this.audioChunkRepository, this.userRepository, this.meetingRepository, this.messageRepository, this.containerTranscriptRepository, this.ffmpegPath, this.ffprobePath);
            transcriptorWorkerThread.start();
            while (transcriptorWorkerThread.isAlive()) {
                try {
                    synchronized (commonObject) {
                        commonObject.wait();
                    }
                } catch (InterruptedException e) {
                    LOGGER.warn("TRANSCRIPTOR: was interrupted", e);
                    transcriptorWorkerThread.interrupt();
                }
            }
        }
    }

    @Scope("prototype")
    public class TranscriptorWorker extends Thread {

        private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

        private final AudioChunkRepository audioChunkRepository;
        private final MeetingRepository meetingRepository;
        private final MessageRepository messageRepository;
        private final UserRepository userRepository;

        private final ContainerTranscriptRepository containerTranscriptRepository;

        private final String ffmpegPath;
        private final String ffprobePath;

        @PostConstruct
        public void init() {
            start();
        }

        public TranscriptorWorker(AudioChunkRepository audioChunkRepository,
                                  UserRepository userRepository,
                                  MeetingRepository meetingRepository,
                                  MessageRepository messageRepository,
                                  ContainerTranscriptRepository containerTranscriptRepository,
                                  String ffmpegPath,
                                  String ffprobePath) {
            this.audioChunkRepository = audioChunkRepository;
            this.meetingRepository = meetingRepository;
            this.messageRepository = messageRepository;
            this.userRepository = userRepository;
            this.containerTranscriptRepository = containerTranscriptRepository;
            this.ffmpegPath = ffmpegPath;
            this.ffprobePath = ffprobePath;
        }

        public void housekeeping() {
            LOGGER.trace("TranscriptorWorker.housekeeping");
            List<File> folderListForCleanup = new ArrayList<>();
            folderListForCleanup.add(new File("./chunks/"));

            for (File folder : folderListForCleanup) {
                folder.mkdir();
                cleanupFolder(folder);
            }
        }

        private void cleanupFolder(File folder) {
            LOGGER.trace("TranscriptorWorker.cleanupFolder " + folder.toString());
            File[] files = folder.listFiles();

            if (files != null) {
                for (final File file : files) {
                    String filename = file.getName();
                    if (filename.endsWith(".ogg") | filename.endsWith(".txt")) {
                        LOGGER.info("MOVE FILE " + file.getName() + " to " + folder.getName());
                        moveAudioFileToFolder(file, "cleanup");
                    }
                }
            }
        }

        @Override
        public void run() {
            LOGGER.trace("TranscriptorWorker.run");
            LOGGER.info("TRANSCRIPTOR_WORKER: Started.");

            while (true) {
                try {
                    LOGGER.info("TRANSCRIPTOR_WORKER: Starting next iteration.");
                    startTranscriptorWorkerIteration();
                    synchronized (commonObject) {
                        commonObject.notifyAll();
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.warn("TRANSCRIPTOR_WORKER: was interrupted.", e);
                }
            }
        }

        private void startTranscriptorWorkerIteration() {
            LOGGER.trace("TranscriptorWorker.startTranscriptorWorkerIteration");
            processOggFilesFromFolder();

            LOGGER.info("TRANSCRIPTOR_WORKER: Looking for finished meetings ...");
            List<Meeting> finishedMeetings = this.meetingRepository.findFinishedMeetings();

            if (!finishedMeetings.isEmpty()) {
                for (Meeting meeting : finishedMeetings) {
                    processFinishedMeeting(meeting);
                    processOggFilesFromFolder();
                }
            } else {
                LOGGER.info("TRANSCRIPTOR_WORKER: ... found none.");
            }

            processOggFilesFromFolder();

            housekeeping();
        }

        private void processFinishedMeeting(Meeting meeting) {
            LOGGER.trace("TranscriptorWorker.processFinishedMeeting " + meeting.toString());
            LOGGER.info("TRANSCRIPTOR_WORKER: Found finished meetings!");

            Long numberOfDbChunksInThisMeeting = 0L;
            int attempt = 1;

            while (
                !(
                    this.audioChunkRepository.findAllByTimestampBetweenAndSavedToFileAtIsNullOrderByTimestampAsc(
                        meeting.getStartTime(),
                        meeting.getEndTime()
                    ).isEmpty()
                    )
                    && attempt <= 3
            ) {
                LOGGER.info("TRANSCRIPTOR_WORKER: Attempt no. " + attempt + " of converting raw db chunks to files ...");
                numberOfDbChunksInThisMeeting += processAudioChunksFromDb(meeting);
                attempt++;
            }

            convertMeetingToTranscript(meeting, numberOfDbChunksInThisMeeting);
        }

        private void processOggFilesFromFolder() {
            LOGGER.trace("TranscriptorWorker.processOggFilesFromFolder");

            final File folder = new File("./chunks/");
            folder.mkdir();

            File[] files = folder.listFiles();

            if (files != null) {
                for (final File file : files) {
                    if (file.getName().endsWith(".ogg")) {
                        try {
                            AudioChunk chunk = getDbChunkFromChunkFile(file).get();
                            ContainerTranscript transcript = this.containerTranscriptRepository.findAllByTimestampBeforeAndEndTimeAfter(chunk.getTimestamp(), chunk.getTimestamp()).get(0);
                            transcribeAudioFileToTranscript(file, transcript);
                            LOGGER.info("TRANSCRIPTOR_WORKER: Finished transcription of " + file.getName() + "!");
                            transcript.setProgress(transcript.getProgress() + 1L);
                            containerTranscriptRepository.save(transcript);
                        } catch (NumberFormatException | IndexOutOfBoundsException | NoSuchElementException e) {
                            LOGGER.error("TRANSCRIPTOR_WORKER: Problem with chunk file. Moving chunk file to error folder.");
                            moveAudioFileToFolder(file, "error");
                        }
                    }
                }
            }
        }

        private void convertMeetingToTranscript(Meeting meeting, Long totalSteps) {
            LOGGER.trace("TranscriptorWorker.convertMeetingToTranscript " + meeting.toString() + ", " + totalSteps.toString());

            ContainerTranscript transcript = buildContainerTranscript(meeting, totalSteps);
            this.containerTranscriptRepository.save(transcript);
            this.meetingRepository.delete(meeting);
        }

        private void transcribeAudioFileToTranscript(File file, ContainerTranscript transcript) {
            if (!file.isDirectory()) {
                String filePath = file.getPath();
                Process whisperProcess = null;
                try {
                    LOGGER.info("TRANSCRIPTOR_WORKER: Starting transcription of " + file.getName() + " ...");
                    whisperProcess = Runtime.getRuntime().exec("python python/whisperAI.py transcribe " + filePath);
                    LOGGER.info("TRANSCRIPTOR_WORKER: Transcription command was executed.");
                } catch (IOException e) {
                    LOGGER.error("TRANSCRIPTOR_WORKER: An error occurred during transcription of a file: " + file.getName() + ". File will be moved to error folder.", e);
                    moveAudioFileToFolder(file, "error");
                }

                try {
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(whisperProcess.getInputStream()));
                    String whisperResult;

                    try {
                        LOGGER.info("TRANSCRIPTOR_WORKER: Waiting for transcription result from Whisper ...");
                        whisperResult = stdInput.readLine();
                        LOGGER.info("TRANSCRIPTOR_WORKER: Whisper returned " + whisperResult);

                        if (whisperResult != null) {

                            RawTranscription[] rawTranscriptions = parseWhisperResponse(whisperResult);
                            Optional<AudioChunk> chunk = getDbChunkFromChunkFile(file);

                            if (chunk.isPresent()) {
                                for (RawTranscription rawTranscription : rawTranscriptions) {
                                    Message transcribedMessage = buildMessageFromRawTranscription(rawTranscription, chunk, transcript);
                                    createTranscribedMessageIfNotEllipsis(transcribedMessage);
                                }
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("An error occured during transcription with whisper of file: " + file.getName(), e);
                        moveAudioFileToFolder(file, "error");
                    }
                    moveAudioFileToFolder(file, "transcribed");
                } catch (NullPointerException e) {
                    LOGGER.error("An error occured during transcription with whisper of file: " + file.getName(), e);
                    moveAudioFileToFolder(file, "error");
                }
            }
        }

        private void createTranscribedMessageIfNotEllipsis(Message transcribedMessage) {
            LOGGER.trace("TranscriptorWorker.createTranscribedMessageIfNotEllipsis " + transcribedMessage.toString());

            if (!transcribedMessage.getContent().trim().equals("...")) {
                messageRepository.save(transcribedMessage);
                LOGGER.info("TRANSCRIPTOR_WORKER: Saved message to transcript! ID: " + transcribedMessage.getId());
            } else {
                LOGGER.warn("TRANSCRIPTOR_WORKER: Ignored transcribed message containing only an ellipsis.");
            }
        }

        private Message buildMessageFromRawTranscription(RawTranscription rawTranscription, Optional<AudioChunk> chunk, ContainerTranscript transcript) {
            LOGGER.trace("TranscriptorWorker.buildMessageFromRawTranscription");

            return Message
                .MessageBuilder
                .aMessage()
                .withIsTranscript(true)
                .withTimestamp(
                    chunk.get()
                        .getTimestamp()
                        .plusSeconds(
                            rawTranscription.getStart().longValue()
                        )
                )
                .withContent(rawTranscription.getTranscript())
                .withUser(chunk.get().getAuthor())
                .withContainer(transcript).build();
        }

        private Optional<AudioChunk> getDbChunkFromChunkFile(File file) {
            LOGGER.trace("TranscriptorWorker.getAudioChunk " + file.toString());

            String fileName = file.getName().substring(0, file.getName().indexOf("."));
            Long chunkId = (long) Integer.parseInt(fileName);
            return audioChunkRepository.findById(chunkId);
        }

        private static RawTranscription[] parseWhisperResponse(String whisperResponse) throws JsonProcessingException {
            LOGGER.trace("TranscriptorWorker.parseWhisperResponse");

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(whisperResponse, RawTranscription[].class);
        }

        private void moveAudioFileToFolder(File file, String folderName) {
            LOGGER.trace("TranscriptorWorker.moveAudioFileToFolder " + file.toString() + ", " + folderName);

            final File oldFolder = new File("./chunks/" + folderName);
            oldFolder.mkdir();
            LOGGER.info("Moving file " + file.getPath() + " to " + "./chunks/" + folderName + "/" + file.getName() + "...");
            try {
                Files.move(Paths.get(file.getPath()), Paths.get("./chunks/" + folderName + "/" + file.getName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("TRANSCRIPTOR_WORKER: An error occurred during moving files to folder.", e);
            }
        }

        private Long processAudioChunksFromDb(Meeting meeting) {
            LOGGER.trace("TranscriptorWorker.processAudioChunksFromDb " + meeting.toString());

            LOGGER.info("TRANSCRIPTOR_WORKER: Finding audio chunks for this meeting in DB ...");
            Long numberOfChunksForThisMeeting = 0L;
            List<AudioChunk> nonTranscribedAudioChunksForThisMeeting = this.audioChunkRepository.findAllByTimestampBetweenAndSavedToFileAtIsNullOrderByTimestampAsc(meeting.getStartTime(), meeting.getEndTime());

            if (!nonTranscribedAudioChunksForThisMeeting.isEmpty()) {
                LOGGER.info("TRANSCRIPTOR_WORKER: Found audio chunks for this meeting in DB!");
                for (ApplicationUser user : this.userRepository.findAll()) {
                    numberOfChunksForThisMeeting = processAudioChunksFromDbForUser(meeting, user, numberOfChunksForThisMeeting);
                }
            } else {
                LOGGER.warn("TRANSCRIPTOR_WORKER: Found no audio chunks for meeting " + meeting + " in DB.");
            }
            return numberOfChunksForThisMeeting;
        }

        private Long processAudioChunksFromDbForUser(Meeting meeting, ApplicationUser user, Long numberOfChunksForThisMeeting) {
            LOGGER.trace("TranscriptorWorker.processAudioChunksFromDbForUser " + meeting + ", " + user + ", " + numberOfChunksForThisMeeting.toString());

            List<AudioChunk> nonTranscribedAudioChunks;
            LOGGER.info("TRANSCRIPTOR_WORKER: Processing audio chunks for user " + user.getEmail());

            nonTranscribedAudioChunks = this.audioChunkRepository
                .findAllByTimestampBetweenAndSavedToFileAtIsNullAndAuthorOrderByTimestampAsc(meeting.getStartTime(), meeting.getEndTime(), user);

            if (!nonTranscribedAudioChunks.isEmpty()) {
                AudioChunk previousChunk = null;
                long previousChunkLength = 5L;

                for (AudioChunk currentChunk : nonTranscribedAudioChunks) {
                    LOGGER.info("TRANSCRIPTOR_WORKER: Processing chunk " + currentChunk.getId() + " by author: " + currentChunk.getAuthor());

                    if (currentChunk.getData().length() > 5) {
                        try {
                            writeBytesToFile(currentChunk);
                            markAsSaved(currentChunk);
                            numberOfChunksForThisMeeting++;

                            if (previousChunk != null) {
                                if (areConjoined(previousChunk, previousChunkLength, currentChunk) && previousChunkLength <= 20L) {
                                    LOGGER.info("TRANSCRIPTOR_WORKER: Merging chunks " + previousChunk.getId() + " and " + currentChunk.getId());
                                    mergeAudioFiles(previousChunk, currentChunk);
                                    previousChunkLength += 5L;
                                    numberOfChunksForThisMeeting--;
                                } else {
                                    LOGGER.info("TRANSCRIPTOR_WORKER: Not merging – pause between chunks or would be too long!");
                                    previousChunk = currentChunk;
                                    previousChunkLength = 5L;
                                }
                            } else {
                                previousChunk = currentChunk;
                                previousChunkLength = 5L;
                            }
                        } catch (IOException e) {
                            LOGGER.error("An error occurred during writing chunk bytes to file: " + currentChunk.getId(), e);
                            nonTranscribedAudioChunks.remove(currentChunk);
                            this.audioChunkRepository.delete(currentChunk);
                        } catch (InterruptedException e) {
                            LOGGER.error("An error occurred during ffmpeg procession of audio chunk: " + currentChunk.getId(), e);
                            nonTranscribedAudioChunks.remove(currentChunk);
                            this.audioChunkRepository.delete(currentChunk);
                        }
                    } else {
                        nonTranscribedAudioChunks.remove(currentChunk);
                        this.audioChunkRepository.delete(currentChunk);
                    }
                }
            }
            return numberOfChunksForThisMeeting;
        }

        private static boolean areConjoined(AudioChunk firstChunk, Long firstChunkLength, AudioChunk secondChunk) {
            LOGGER.trace("TranscriptorWorker.areConjoined " + firstChunk + ", " + firstChunkLength + ", " + secondChunk);

            // margin of +- 0.5 s
            return (!firstChunk.getTimestamp().plusSeconds(firstChunkLength).isAfter(secondChunk.getTimestamp().plusNanos(500000000L)))
                && (!firstChunk.getTimestamp().plusSeconds(firstChunkLength).isBefore(secondChunk.getTimestamp().minusNanos(500000000L)));
        }

        private void markAsSaved(AudioChunk chunk) {
            LOGGER.trace("TranscriptorWorker.markAsSaved " + chunk.toString());

            chunk.setSavedToFileAt(LocalDateTime.now());
            this.audioChunkRepository.save(chunk);
        }

        private void writeBytesToFile(AudioChunk chunk) throws IOException {
            LOGGER.trace("TranscriptorWorker.writeBytesToFile " + chunk.toString());

            byte[] data = decodeBase64ChunkToBytes(chunk);
            OutputStream outputStream = new FileOutputStream("./chunks/" + chunk.getId().toString() + ".ogg");
            outputStream.write(data);
            outputStream.close();
        }

        private byte[] decodeBase64ChunkToBytes(AudioChunk chunk) {
            LOGGER.trace("TranscriptorWorker.decodeBase64ChunkToBytes " + chunk.toString());

            String base64Data = chunk.getData();
            base64Data = base64Data.substring(35, base64Data.length() - 36);
            return Base64.decode(base64Data);
        }

        private void mergeAudioFiles(AudioChunk firstChunk, AudioChunk secondChunk) throws IOException, InterruptedException {
            LOGGER.trace("TranscriptorWorker.mergeAudioFiles " + firstChunk.toString() + ", " + secondChunk.toString());

            LOGGER.info("TRANSCRIPTOR_WORKER: Merging current chunk " + secondChunk.getId() + " into previous chunk " + firstChunk.getId() + "...");
            FFmpeg ffmpeg = new FFmpeg(this.ffmpegPath);
            FFprobe ffprobe = new FFprobe(this.ffprobePath);
            PrintWriter printWriter = new PrintWriter(new FileOutputStream("./chunks/concatlist" + firstChunk.getId().toString() + ".txt"));
            printWriter.println("file '" + "./" + firstChunk.getId().toString() + ".ogg'");
            printWriter.println("file '" + "./" + secondChunk.getId().toString() + ".ogg'");
            printWriter.close();

            FFmpegBuilder builder = new FFmpegBuilder().setInput("./chunks/concatlist" + firstChunk.getId().toString() + ".txt").addExtraArgs("-f", "concat", "-safe", "0").overrideOutputFiles(true)
                .addOutput("./chunks/concat" + firstChunk.getId().toString() + ".ogg").addExtraArgs("-c", "copy").done();
            FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
            FFmpegJob job = executor.createJob(builder);
            job.run();

            do {
                sleep(250);
            } while (job.getState() == FFmpegJob.State.RUNNING);

            LOGGER.info("FILE DELETE: concatlist" + firstChunk.getId().toString() + ".txt");
            LOGGER.info("FILE DELETE: " + firstChunk.getId().toString());
            LOGGER.info("FILE DELETE: " + secondChunk.getId().toString());
            new File("./chunks/concatlist" + firstChunk.getId().toString() + ".txt").delete();
            new File("./chunks/" + firstChunk.getId().toString() + ".ogg").delete();
            new File("./chunks/" + secondChunk.getId().toString() + ".ogg").delete();
            new File("./chunks/concat" + firstChunk.getId().toString() + ".ogg").renameTo(new File("./chunks/" + firstChunk.getId().toString() + ".ogg"));
        }

        private ContainerTranscript buildContainerTranscript(Meeting meeting, Long totalSteps) {
            LOGGER.trace("TranscriptorWorker.buildContainerTranscript " + meeting.toString() + ", " + totalSteps.toString());

            ContainerTranscript transcript = new ContainerTranscript();

            transcript.setAuthor(meeting.getAuthor());
            transcript.setName(meeting.getTitle());
            transcript.setDescription(meeting.getDescription());
            transcript.setTimestamp(meeting.getStartTime());
            transcript.setEndTime(meeting.getEndTime());
            transcript.setSummary(meeting.getSummary());
            transcript.setPinned(false);
            transcript.setProgress(0L);
            transcript.setTotalSteps(totalSteps);
            return transcript;
        }
    }

}
