package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTopic;
import at.ac.tuwien.sepm.groupphase.backend.entity.ContainerTranscript;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTopicRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ContainerTranscriptRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MeetingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.MessageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Profile("generateData")
@DependsOn({"generateUsers", "generateTopic", "generateMeetings"})
@Component
public class MessageDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ContainerTopicRepository topicRepository;
    private final ContainerTranscriptRepository containerTranscriptRepository;
    private final MeetingRepository meetingRepository;

    public MessageDataGenerator(MessageRepository messageRepository,
                                UserRepository userRepository,
                                ContainerTopicRepository topicRepository,
                                ContainerTranscriptRepository containerTranscriptRepository,
                                MeetingRepository meetingRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.containerTranscriptRepository = containerTranscriptRepository;
        this.meetingRepository = meetingRepository;
    }

    @PostConstruct
    private void generateMessage() {
        if (messageRepository.findAll().size() > 0) {
            LOGGER.debug("message already generated");
        } else {
            generateTopic1();
            generateTopic2();
            generateTopic3();
            generateTopic4();
            generateTopic5();
            generateMeeting1();
        }
    }

    private void generateTopic1() {
        LOGGER.debug("generating {} message entries", 2);
        ContainerTopic topic1 = topicRepository.findAll().get(0);
        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:01:00.00"))
            .withContent("Hey guys!")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:02:00.00"))
            .withContent("Hey, whats up?")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:03:00.00"))
            .withContent("Alles gut! Bei dir?")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:04:00.00"))
            .withContent("Hallo! Alles ok bei euch?")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:05:00.00"))
            .withContent("Ja")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:06:00.00"))
            .withContent("Ja")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:07:00.00"))
            .withContent("Was steht heute an? Ich merge gerade mein letztes Issue in den dev? Was habt ihr und wollen wir es dann gleich testen?")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:08:00.00"))
            .withContent("Passt mach ma! Ich streame wieder")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:09:00.00"))
            .withContent("Hallo Leute!")
            .withUser(userRepository.findApplicationUserByEmail("michael@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:10:00.00"))
            .withContent("Sehr fein! Ich hoff es gibt nicht zuviele Konflikte ....")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:11:00.00"))
            .withContent("Hallo Michael - hab gesehen dass du auch was gepushed hast ...")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:12:00.00"))
            .withContent("Hallo")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:13:00.00"))
            .withContent("Hallo Michael")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic1)
            .withIsTranscript(false)
            .build()
        );
    }

    private void generateTopic2() {
        LOGGER.debug("generating {} message entries", 2);
        ContainerTopic topic2 = topicRepository.findAll().get(1);
        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:01:00.00"))
            .withContent("War ganz gut würde ich sagen obwohl nix so richtig funktioniert hat :(")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic2)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:02:00.00"))
            .withContent("Ja super aber da haben wir noch viel vor!")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic2)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:03:00.00"))
            .withContent("Das schaffen wir schon!")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic2)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:04:00.00"))
            .withContent("Ja geht schon!")
            .withUser(userRepository.findApplicationUserByEmail("constantin@email.com"))
            .withContainer(topic2)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-01T14:05:00.00"))
            .withContent("Ja denke auch")
            .withUser(userRepository.findApplicationUserByEmail("michael@email.com"))
            .withContainer(topic2)
            .withIsTranscript(false)
            .build()
        );
    }

    private void generateTopic3() {
        LOGGER.debug("generating {} message entries", 2);
        ContainerTopic topic = topicRepository.findAll().get(2);
        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:01:00.00"))
            .withContent("Naja stabil ist was anderes")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:02:00.00"))
            .withContent("Ja wenn viele im video chat sind dann gehts nicht!")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:03:00.00"))
            .withContent("Mein Server hats gar nicht gepackt ... :)")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:04:00.00"))
            .withContent("Ich bin irgendwann rausgeflogen.")
            .withUser(userRepository.findApplicationUserByEmail("constantin@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:05:00.00"))
            .withContent("Ich hab nur den Jakob und den David gesehen")
            .withUser(userRepository.findApplicationUserByEmail("michael@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:06:00.00"))
            .withContent("Ok ich glaub es ist klar was wir tun müssen!")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2022-12-23T14:07:00.00"))
            .withContent("Besprechen wir es morgen? Ich erstell einmal lauter issues aus meinen Notizen.")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );
    }

    private void generateTopic4() {
        LOGGER.debug("generating {} message entries", 2);
        ContainerTopic topic = topicRepository.findAll().get(3);
        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-19T17:01:00.00"))
            .withContent("Schade dass das neue Design nicht funktioniert hat.")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-19T17:02:00.00"))
            .withContent("Ja aber es war zumindest zum ersten Mal relativ stabil ...")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-19T17:03:00.00"))
            .withContent("Ja.")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-19T17:04:00.00"))
            .withContent("Ja.")
            .withUser(userRepository.findApplicationUserByEmail("constantin@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-19T17:05:00.00"))
            .withContent("Ja.")
            .withUser(userRepository.findApplicationUserByEmail("michael@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-19T17:06:00.00"))
            .withContent("Ja und mein Computer hats auch ganz gut gepackt.")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );
    }

    private void generateTopic5() {
        LOGGER.debug("generating {} message entries", 2);
        ContainerTopic topic = topicRepository.findAll().get(4);

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:01:00.00"))
            .withContent("Und wie gefällt euch chatify4business?")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:02:00.00"))
            .withContent("Sehr gut. Schaut gut aus. Da ist sehr viel passiert seit dem letzten Mal.")
            .withUser(userRepository.findApplicationUserByEmail("alexander@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:03:00.00"))
            .withContent("Ja war richtig anstrengend die letzten Tage aber wir haben viel geschafft.")
            .withUser(userRepository.findApplicationUserByEmail("michael@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:04:00.00"))
            .withContent("Ja finde ich auch. Sieht gut aus.")
            .withUser(userRepository.findApplicationUserByEmail("peter@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:05:00.00"))
            .withContent("Schauen wir einmal wir die Transkription funktioniert.")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:06:00.00"))
            .withContent("Das Layout ist auch viel besser.")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        /*messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:07:00.00"))
            .withContent("Haha mein Server rennt auf Hochtouren. Hoffe der packt das!")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:08:00.00"))
            .withContent("Hallo. Nein leider ich bin heute schon verplant.")
            .withUser(userRepository.findApplicationUserByEmail("alexander@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:09:00.00"))
            .withContent("Sorry, bin schon zum Essen verabredet.")
            .withUser(userRepository.findApplicationUserByEmail("peter@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:10:00.00"))
            .withContent("Habe heute nachher auch keine Zeit.")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:11:00.00"))
            .withContent("Ich leider auch nicht, hab morgen noch einen Abgabe bis dahin kann ich nix machen!")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:12:00.00"))
            .withContent("Morgen hab ich gut Zeit. Passt am Nachmittag?")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:13:00.00"))
            .withContent("Hmm. Bei mir geht Vormittag besser.")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:14:00.00"))
            .withContent("Ok. Vormittag lässt sich bei mir einrichten. Peter?")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:15:00.00"))
            .withContent("Hmm. Leider nein, aber da sollte ich eh nicht dabei sein.")
            .withUser(userRepository.findApplicationUserByEmail("peter@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:16:00.00"))
            .withContent("Morgen hab ich Abgabegespräche, da kann ich nicht. Muss eh auch nicht dabei sein.")
            .withUser(userRepository.findApplicationUserByEmail("alexander@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:17:00.00"))
            .withContent("Vormittag geht.")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:18:00.00"))
            .withContent("Bis 11 ist die Abgabe dann gehts.")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:19:00.00"))
            .withContent("Passt, dann morgen 11 bis 12 Nachbesprechung.")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:20:00.00"))
            .withContent("Ok ist notiert. Bis morgen dann.")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:21:00.00"))
            .withContent("Ciao.")
            .withUser(userRepository.findApplicationUserByEmail("peter@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:22:00.00"))
            .withContent("Servus.")
            .withUser(userRepository.findApplicationUserByEmail("alexander@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:23:00.00"))
            .withContent("Passt.")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T19:24:00.00"))
            .withContent("Bis morgen.")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(false)
            .build()
        );*/
    }

    private void generateMeeting1() {
        LOGGER.debug("generating {} message entries", 2);
        ContainerTranscript topic = containerTranscriptRepository.findAll().get(0);
        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T14:01:00.00"))
            .withContent("Perfekt! besser hätte es nicht laufen können!")
            .withUser(userRepository.findApplicationUserByEmail("florian@email.com"))
            .withContainer(topic)
            .withIsTranscript(true)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T14:02:00.00"))
            .withContent("Das Mieting ist es sehr gut gelaufen. Super am einzigen Bargainigkeiten, aber sonst kann man nicht")
            .withUser(userRepository.findApplicationUserByEmail("jakob@email.com"))
            .withContainer(topic)
            .withIsTranscript(true)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T14:03:00.00"))
            .withContent("viel sagen und die Transkription hat auch gut funktioniert. Besser als erwartet nur als zu viele")
            .withUser(userRepository.findApplicationUserByEmail("nico@email.com"))
            .withContainer(topic)
            .withIsTranscript(true)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T14:04:00.00"))
            .withContent(" im Videochelp.")
            .withUser(userRepository.findApplicationUserByEmail("michael@email.com"))
            .withContainer(topic)
            .withIsTranscript(true)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T14:05:00.00"))
            .withContent("  Ja, da hat man gemerkt, dass das System und die Grenzen kommt.")
            .withUser(userRepository.findApplicationUserByEmail("constantin@email.com"))
            .withContainer(topic)
            .withIsTranscript(true)
            .build()
        );

        messageRepository.save(Message.MessageBuilder.aMessage()
            .withTimestamp(LocalDateTime.parse("2023-01-24T14:02:00.00"))
            .withContent(" und wenn die Kamera der Aktiviert ist, sehr schön.")
            .withUser(userRepository.findApplicationUserByEmail("david@email.com"))
            .withContainer(topic)
            .withIsTranscript(true)
            .build()
        );
    }
}