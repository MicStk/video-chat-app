// this webrtc implementation is adapted from the example from Jirka Hladis
// https://www.dmcinfo.com/latest-thinking/blog/id/9852/multi-user-video-chat-with-webrtc

import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Meeting } from '../../dtos/meeting';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { MeetingService } from '../../services/meeting.service';
import { AudioChunkService } from '../../services/audio-chunk.service';
import { environment } from '../../../environments/environment';
import { AudioChunk } from '../../dtos/audio-chunk';
import { User } from '../../dtos/user';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { VoiceChatService } from '../../services/voice-chat.service';

@Component({
    selector: 'app-voice-chat',
    templateUrl: './voice-chat.component.html',
    styleUrls: ['./voice-chat.component.scss']
})
export class VoiceChatComponent implements OnInit {
    @ViewChild('local_video') localVideo: ElementRef;

    localUuid;
    localDisplayName;
    localStream;
    audioOnlyStream;
    intervalId;
    chunkArray;
    checkVideo = false;
    peerConnections = {}; // key is uuid, values are peer connection object and user defined display name string
    peerUUIDs = new Set();
    voiceSubscription;

    mediaRecorder;
    stop = document.querySelector('.stop');

    activeMeeting: Meeting = null;

    meetingForm: UntypedFormGroup;
    isRecording = false;
    hasStartedRecording = false;
    recordedBy = [];
    // After first submission attempt, form validation will start
    submitted = false;

    success = false;


    currentUser: User;

    protected constraints = {
        video: {
            width: 240,
            height: 160
        },
        audio: true
    };

    protected audioConstraints = {
        video: false,
        audio: true
    };

    protected serverConnection;
    protected peerConnectionConfig = environment.RTCPeerConfiguration;

    constructor(private formBuilder: UntypedFormBuilder,
                private userService: UserService,
                public authService: AuthService,
                private meetingService: MeetingService,
                private router: Router,
                private route: ActivatedRoute,
                private audioChunkService: AudioChunkService,
                public voiceService: VoiceChatService) {
    }

    ngOnInit() {
        document.getElementById('voice').style.minWidth = '500px';
        this.voiceSubscription = this.voiceService.getVoiceServiceEventEmitter()
            .subscribe(event => this.voiceEvent(event));
        const currentDate = new Date();
        this.meetingForm = this.formBuilder.group({
            title: ['', [Validators.required]],
            description: ['', [Validators.required]],
            summary: ['', [Validators.required]]
        });

        // reload videos after navigating somewhere else
        this.router.events.subscribe(ev => {
            if (ev instanceof NavigationEnd) {
                if (this.localStream !== undefined && this.router.url === '/voice') {
                    this.localVideo.nativeElement.srcObject = this.localStream;
                    for (const peerUuid of this.peerUUIDs) {
                        (document.getElementById('remoteStream_' + peerUuid as any) as HTMLVideoElement).load();
                    }
                }
            }
        });

        // request LocalStream, AudioOnlyStream, WebSocketConnection
        if (navigator.mediaDevices.getUserMedia) {
            this.requestLocalStream()
                .then(() => this.createAudioOnlyStream())
                .catch((error) => {
                    this.errorHandler(error, 'createAudioOnlyStream');
                })
                .then(() => this.createWebSocketConnection())
                .catch((error) => {
                    this.errorHandler(error, 'createWebSocketConnection');
                })
                .then(() => {
                    const ownDesc = document.getElementById('videoLabelOwn');
                    const localName = this.localDisplayName.split('_');
                    ownDesc.innerHTML = localName[0] + ' ' + localName[1];
                }).catch(this.errorHandler);
        } else {
            alert('Your browser does not support getUserMedia API');
        }
    }

    requestLocalStream() {
        return navigator.mediaDevices.getUserMedia(this.constraints)
            .then(stream => {
                this.localStream = stream;
                this.localVideo.nativeElement.srcObject = this.localStream;
                if (this.voiceService.isMuted()) {
                    this.muteMic();
                }
                if (!this.voiceService.isCam()) {
                    this.muteCam();
                }
                if (!this.voiceService.isSound()) {
                    this.muteOthers();
                }
                this.checkVideo = true;
            })
            .catch(() => {
                this.createAltImage();
                this.deleteVideo();
                return navigator.mediaDevices.getUserMedia(this.audioConstraints)
                    .then(stream => {
                        this.localStream = stream;
                    })
                    .catch((er) => {
                        this.errorHandler(er, 'requestAudioStream');
                    });
            });
    }

    createAudioOnlyStream() {
        return new Promise<void>((resolve) => {
            console.log('Create stream clone');
            if (this.localStream !== undefined && this.localStream instanceof MediaStream) {
                this.audioOnlyStream = this.localStream.clone();
                const videoTrack = this.audioOnlyStream.getVideoTracks();
                if (videoTrack.length !== 0) {
                    this.audioOnlyStream.removeTrack(videoTrack[0]);
                }
                this.mediaRecorder = new MediaRecorder(this.audioOnlyStream);
                // iff audio -> show blank image
                if (!this.checkVideo) {
                    this.createAltImage();
                    this.deleteVideo();
                }
            }
            resolve();
        });
    }

    createWebSocketConnection() {
        return new Promise<void>((resolve) => {
            this.localDisplayName = this.authService.getName();
            this.localUuid = this.authService.getUserEMail();
            this.serverConnection = new WebSocket(environment.wssPath);
            this.serverConnection.onmessage = (msg) => {
                this.gotMessageFromServer(msg);
            };
            this.serverConnection.onopen = () => {
                console.log('connected to the signaling server');
                this.serverConnection.send(JSON.stringify({
                    displayName: this.localDisplayName, uuid: this.localUuid, dest: 'all'
                }));
            };
            this.serverConnection.onclose = () => {
                console.log('Disconnected from Signaling Server. Trying to reconnect...');
                this.serverConnection.send(JSON.stringify({
                    displayName: this.localDisplayName, uuid: this.localUuid, dest: 'all'
                }));
            };
            resolve();
        });
    }

    createAltImage() {
        const localVideoImage = document.getElementById('localVideoImageAlt') as HTMLImageElement;
        localVideoImage.src = '../../assets/blank-profile-picture.png';
        localVideoImage.alt = 'blank-profile-picture';
    }

    deleteVideo() {
        const vidCon = document.getElementById('localVideoContainer');
        const delVid = document.getElementById('localVideoVideo');
        if (delVid !== null) {
            vidCon.removeChild(delVid);
        }
    }

    startMeeting() {
        console.log('start Meeting.');
        const currentDate = new Date();

        this.activeMeeting = new Meeting();
        this.activeMeeting.title = 'Meeting '
            + currentDate.getUTCFullYear()
            + '-'
            + currentDate.getUTCMonth() + 1
            + '-'
            + currentDate.getUTCDate()
            + ' '
            + (currentDate.getUTCHours() + 1).toString().padStart(2, '0')
            + ':'
            + currentDate.getUTCMinutes().toString().padStart(2, '0')
        ;
        this.activeMeeting.startTime = currentDate;
        this.activeMeeting.author = new User();
        this.activeMeeting.author.email = this.authService.getUserEMail();

        this.userService.getUserWithEmail(this.authService.getUserEMail()).subscribe({
            next: res => {
                this.currentUser = res;
                const author = new User();
                author.id = this.currentUser.id;
                author.email = this.currentUser.email;
                author.firstName = this.currentUser.firstName;
                author.lastName = this.currentUser.lastName;
                author.role = this.currentUser.role;
                author.password = '';
                this.activeMeeting.author = author;
                this.meetingService.create(this.activeMeeting).subscribe({
                    next: res2 => {
                        this.activeMeeting = res2;
                        this.startRecording();
                        this.hasStartedRecording = true;
                        this.sendRecordingMessageToPeers('start');
                    }
                });
            },
            error: err => {
                console.log('error: ' + err);
            }
        });


    }

    stopMeeting() {
        this.submitted = true;
        if (this.activeMeeting == null) {
            return;
        }

        if (!this.meetingForm.valid) {
            return;
        }

        this.stopRecording();

        this.activeMeeting.title = this.meetingForm.controls.title.value;
        this.activeMeeting.description = this.meetingForm.controls.description.value;
        this.activeMeeting.summary = this.meetingForm.controls.summary.value;
        this.activeMeeting.endTime = new Date();

        this.meetingService.update(this.activeMeeting).subscribe();
        console.log(this.activeMeeting);
        this.success = true;
        // Cleanup.
        this.activeMeeting = null;
    }

    startRecording() {
        this.submitted = false;
        const currentDate = new Date();
        this.meetingForm.reset();
        this.meetingForm.patchValue({
            title: 'Meeting '
                + currentDate.getUTCFullYear()
                + '-'
                + currentDate.getUTCMonth() + 1
                + '-'
                + currentDate.getUTCDate()
                + ' '
                + (currentDate.getUTCHours() + 1).toString().padStart(2, '0')
                + ':'
                + currentDate.getUTCMinutes().toString().padStart(2, '0')
        });

        this.isRecording = true;
        this.chunkArray = [];
        this.mediaRecorder = new MediaRecorder(this.audioOnlyStream);
        this.mediaRecorder.ondataavailable = (e) => {
            console.log('onDataAvailable');
            console.log('Data: ', e.data);
            this.chunkArray.push(e.data);
        };
        this.mediaRecorder.onstop = () => {
            console.log('onStop');
            const actualChunks = this.chunkArray.splice(0, this.chunkArray.length);
            const blob = new Blob(actualChunks, {type: 'audio/ogg; codecs=opus'});
            const audioChunk = new AudioChunk();
            audioChunk.timestamp = new Date();
            this.userService.getUserWithEmail(this.authService.getUserEMail()).subscribe({
                next: res => {
                    this.currentUser = res;
                    const author = new User();
                    author.id = this.currentUser.id;
                    author.email = this.currentUser.email;
                    author.firstName = this.currentUser.firstName;
                    author.lastName = this.currentUser.lastName;
                    author.role = this.currentUser.role;
                    author.password = '';

                    audioChunk.author = author;

                    const reader = new FileReader();
                    reader.readAsDataURL(blob);
                    reader.onloadend = () => {
                        audioChunk.data = reader.result.toString();

                        console.log('audioChunk');
                        console.log(audioChunk);
                        this.audioChunkService.create(audioChunk).subscribe();
                    };
                },
                error: err => {
                    console.log('error: ' + err);
                }
            });
        };
        this.recordVideoChunk(this.audioOnlyStream);
    }

    recordVideoChunk(stream) {
        this.mediaRecorder.start();

        setTimeout(() => {
            if (this.mediaRecorder.state === 'recording') {
                this.mediaRecorder.stop();
            }
            if (this.isRecording) {
                this.recordVideoChunk(stream);
            }
        }, 5000);
    }

    stopRecording() {
        if (this.isRecording === true) {
            clearInterval(this.intervalId);
            this.mediaRecorder.stop();
            this.isRecording = false;
            this.hasStartedRecording = false;
            this.recordedBy = [];
        }
        this.sendRecordingMessageToPeers('stop');
    }

    sendRecordingMessageToPeers(recording: string) {
        this.serverConnection.send(JSON.stringify({
            rec: recording,
            uuid: this.localUuid,
            dest: 'all'
        }));
    }

    gotMessageFromServer(message) {
        const signal = JSON.parse(message.data);
        const peerUuid = signal.uuid;
        console.log(signal);

        // Ignore messages that are not for us or from ourselves
        if (peerUuid === this.localUuid || (signal.dest !== this.localUuid && signal.dest !== 'all')) {
            return;
        }

        // If recording has started before joining, start recording
        if (signal.isRec === true) {
            if (!this.isRecording) {
                this.recordedBy.push(peerUuid);
                this.startRecording();
            }
        }

        if (signal.displayName && signal.dest === 'all') {
            // set up peer connection object for a newcomer peer
            console.log('new Peer: ' + signal.displayName, message);
            this.setUpPeer(peerUuid, signal.displayName);
            this.serverConnection.send(JSON.stringify({
                displayName: this.localDisplayName,
                uuid: this.localUuid,
                dest: peerUuid,
                isRec: this.isRecording // send Recording status
            }));

        } else if (signal.displayName && signal.dest === this.localUuid) {
            // initiate call if we are the newcomer peer
            this.setUpPeer(peerUuid, signal.displayName, true);

        } else if (signal.sdp) {
            this.peerConnections[peerUuid].pc.setRemoteDescription(new RTCSessionDescription(signal.sdp)).then(() => {
                // Only create answers in response to offers
                if (signal.sdp.type === 'offer') {
                    console.log('sdp offer', message);
                    this.peerConnections[peerUuid].pc.createAnswer()
                        .then(description =>
                            this.createdDescription(description, peerUuid))
                        .catch(this.errorHandler);
                }
            }).catch(this.errorHandler);

        } else if (signal.ice) {
            console.log('ice-candidate', message);
            this.peerConnections[peerUuid].pc.addIceCandidate(new RTCIceCandidate(signal.ice)).catch(this.errorHandler);
        } else if (signal.rec) {
            console.log(peerUuid + ' started recording');
            if (signal.rec === 'start') {
                this.recordedBy.push(peerUuid);
                if (this.authService.getUserRole() === 'ADMIN') {
                    this.activeMeeting = new Meeting();
                    this.activeMeeting.startTime = new Date();
                    this.activeMeeting.author = new User();
                    this.activeMeeting.author.email = this.authService.getUserEMail();
                    this.hasStartedRecording = true;
                }
                this.startRecording();
            } else if (signal.rec === 'stop') {

                if (this.isRecording === true) {
                    this.stopRecording();
                    console.log('recorder stopped');
                } else {
                    const index = this.recordedBy.indexOf(peerUuid);
                    if (index > -1) {
                        this.recordedBy.splice(index, 1);
                    }
                }
            }
        } else if (signal.video) {
          console.log('Video: ' + peerUuid + ': ' + signal.video);
          if (signal.video === 'vidIs_false') {
            document.getElementById('remoteStream_' + peerUuid).hidden = true;
            document.getElementById('remoteImg_' + peerUuid).hidden = false;
          } else {
            document.getElementById('remoteStream_' + peerUuid).hidden = false;
            document.getElementById('remoteImg_' + peerUuid).hidden = true;
          }
        }
    }

    setUpPeer(peerUuid, displayName, initCall = false) {
        this.peerUUIDs.add(peerUuid);
        this.peerConnections[peerUuid] = {displayName, pc: new RTCPeerConnection(this.peerConnectionConfig)};
        this.peerConnections[peerUuid].pc.onicecandidate = event => this.gotIceCandidate(event, peerUuid);
        this.peerConnections[peerUuid].pc.ontrack = event => this.gotRemoteStream(event, peerUuid);
        this.peerConnections[peerUuid].pc.oniceconnectionstatechange = event => this.checkPeerDisconnect(event, peerUuid);
        if (this.localStream instanceof MediaStream) {
            this.peerConnections[peerUuid].pc.addStream(this.localStream);
        }
        if (initCall) {
            this.peerConnections[peerUuid].pc.createOffer()
                .then(description => this.createdDescription(description, peerUuid))
                .catch(this.errorHandler);
        }
    }

    gotIceCandidate(event, peerUuid) {
        if (event.candidate != null) {
            this.serverConnection.send(JSON.stringify({ice: event.candidate, uuid: this.localUuid, dest: peerUuid}));
        }
    }

    createdDescription(description, peerUuid) {
        this.peerConnections[peerUuid].pc.setLocalDescription(description).then(() => {
            this.serverConnection.send(JSON.stringify({
                sdp: this.peerConnections[peerUuid].pc.localDescription,
                uuid: this.localUuid,
                dest: peerUuid
            }));
        }).catch(this.errorHandler);
    }

    gotRemoteStream(event, peerUuid) {
        const remote = document.getElementById('remoteVideo_' + peerUuid);
        if (remote) {
            remote.remove();
        }
        const vidContainer = document.createElement('div');
        vidContainer.setAttribute('id', 'remoteVideo_' + peerUuid);
        vidContainer.className = 'videoContainer';
        vidContainer.style.margin = '10px 5px';

        const divElem = document.createElement('div');
        divElem.style.width = '210px';
        divElem.style.height = '140px';
        divElem.style.display = 'flex';
        divElem.style.alignItems = 'center';
        divElem.style.justifyContent = 'center';
        divElem.className = 'vidCont';

        // assign stream to new HTML video element
        const vidElement = document.createElement('video');
        vidElement.setAttribute('autoplay', '');
        vidElement.setAttribute('id', 'remoteStream_' + peerUuid);
        vidElement.srcObject = event.streams[0];
        vidElement.style.height = '100%';
        vidElement.style.width = '100%';
        vidElement.style.objectFit = 'contain';
        // if (!this.voiceService.isSound()) {
        //   vidElement.muted = true;
        // }
        divElem.appendChild(vidElement);
        vidContainer.appendChild(divElem);



        //vidElement.style.display = 'none';
        const imgElement = document.createElement('img');
        imgElement.src = '../../assets/CameraOff.png';
        imgElement.setAttribute('id', 'remoteImg_' + peerUuid);
        imgElement.alt = 'blank-profile-picture';
        imgElement.style.width = '210px';
        imgElement.style.height = '140px';
        imgElement.hidden = true;
        divElem.appendChild(imgElement);
        if (event.streams[0].getVideoTracks().length === 0) {
          imgElement.hidden = false;
          vidElement.hidden = true;
        }

        vidContainer.appendChild(divElem);
        vidContainer.appendChild(this.makeLabel(this.peerConnections[peerUuid].displayName));

        document.getElementById('videos').appendChild(vidContainer);

        this.updateLayout();
    }

    async checkPeerDisconnect(event, peerUuid) {
        const state = this.peerConnections[peerUuid].pc.iceConnectionState;
        if (state === 'failed' || state === 'closed' || state === 'disconnected') {
            await new Promise(r => setTimeout(r, 2000));
            if (state === 'failed' || state === 'closed' || state === 'disconnected') {
                delete this.peerConnections[peerUuid];
                for (const index in this.peerUUIDs) {
                    if (this.peerUUIDs[index] === peerUuid) {
                        this.peerUUIDs[index] = null;
                    }
                }
                document.getElementById('videos').removeChild(document.getElementById('remoteVideo_' + peerUuid));
                this.updateLayout();
            }
        }
    }

    updateLayout() {
        // update CSS grid based on number of diplayed videos
        let rowHeight = '98vh';
        let colWidth = '98vw';

        const numVideos = Object.keys(this.peerConnections).length + 1; // add one to include local video

        if (numVideos > 1 && numVideos <= 4) { // 2x2 grid
            rowHeight = '48vh';
            colWidth = '48vw';
        } else if (numVideos > 4) { // 3x3 grid
            rowHeight = '32vh';
            colWidth = '32vw';
        }

        document.documentElement.style.setProperty(`--rowHeight`, rowHeight);
        document.documentElement.style.setProperty(`--colWidth`, colWidth);
    }

    makeLabel(label) {
        const labelName = label.split('_');
        const userLabel = document.createElement('p');
        userLabel.setAttribute('style', 'text-align: center; color: #fff; margin-top: 10px');
        userLabel.innerHTML = labelName[0] + ' ' + labelName[1];
        return userLabel;
    }

    errorHandler(error, func?) {
        if (func) {
            console.warn('error in ' + func);
        }
        console.warn(error);
    }

    voiceEvent(event) {
        console.log('voiceEvent: ', event);
        switch (event) {
            case 'toggleMuted':
                this.muteMic();
                break;
            case 'toggleCam':
                this.muteCam();
                break;
            case 'toggleSound':
                this.muteOthers();
                break;
            case 'toggleActive':
                this.leaveChat();
                break;
        }
    }

    public muteMic() {
        this.localStream.getAudioTracks().forEach(track => track.enabled = !track.enabled);
        this.audioOnlyStream.getAudioTracks().forEach(track => track.enabled = !track.enabled);
    }

    public muteCam() {
      this.localStream.getVideoTracks().forEach(track => track.enabled = !track.enabled);
      this.serverConnection.send(JSON.stringify({
        video: 'vidIs_' + !this.voiceService.isCam(),
        uuid: this.localUuid,
        dest: 'all'
      }));
      document.getElementById('localVideoVideo').hidden = this.voiceService.isCam();
      document.getElementById('localVideoImageAlt'). hidden = !this.voiceService.isCam();
    }

    public muteOthers() {
        if (this.peerUUIDs.size > 0) {
            this.peerUUIDs.forEach((peerUUid) => {
                let log = '';
                // @ts-ignore
                if (this.peerUUIDs[peerUUid] !== null) {
                    log = log + peerUUid + ', ';
                    (document.getElementById('remoteStream_' + peerUUid) as HTMLVideoElement).muted = this.voiceService.isSound();
                }
                console.log(log);
            });
        }
    }

  leaveChat() {
    document.getElementById('voice').style.minWidth = '50px';
    document.getElementById('voice').style.width = '50px';
    this.localStream.getTracks().forEach((track) => {
      track.stop();
    });
    this.audioOnlyStream.getTracks().forEach((track) => {
      track.stop();
    });
        console.log('closed local Stream');
        this.peerUUIDs.forEach((peerUUid) => {
            // @ts-ignore
            if (this.peerConnections[peerUUid] !== null) {
                // @ts-ignore
                this.peerConnections[peerUUid].pc.close();
            }
        });
        this.isRecording = false;
        this.hasStartedRecording = false;
        this.peerUUIDs = null;
        this.peerConnections = null;
        console.log('closed all connections');
    }

  /**
   * Success flag will be deactivated, which clears the success message
   */
  vanishSuccess() {
    this.success = false;
  }

}
