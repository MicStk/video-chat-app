import {Component, OnInit} from '@angular/core';
import {MeetingService} from '../../services/meeting.service';
import {TopicService} from '../../services/topic.service';
import {TranscriptService} from '../../services/transcript.service';
import {Transcript} from '../../dtos/transcript';
import {UserService} from '../../services/user.service';
import {Topic} from '../../dtos/topic';
import {NgForm} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {VoiceChatService} from '../../services/voice-chat.service';
import {User} from '../../dtos/user';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;
  open = true;

  newTopic: Topic = {
    name: '',
    author: new User(),
    timestamp: new Date(),
    pinned: false,
    messages: null,
    isTranscript: false,
  };

  private topics: Topic[];

  private transcripts: Transcript[];

  constructor(private topicService: TopicService,
              private userService: UserService,
              private meetingService: MeetingService,
              private transcriptService: TranscriptService,
              public authService: AuthService,
              public voiceService: VoiceChatService,
              private notification: ToastrService
  ) {
  }

  ngOnInit() {
    this.loadTopics();
    this.loadTranscripts();
    setInterval(() => {
      if (this.authService.isLoggedIn()) {
        this.loadTopics();
      }
    }, 10 * 1000);


    setInterval(() => {
      if (this.authService.isLoggedIn()) {
        this.loadTranscripts();
      }
    }, 10 * 1000);
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  getTopic(): Topic[] {
    return this.topics;
  }

  getTranscripts(): Transcript[] {
    return this.transcripts;
  }

  toggleSideBar() {
    this.open = !this.open;
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  /**
   * Sends container creation request
   *
   * @param container the container which should be created
   */
  public createTopic(form: NgForm) {
    this.newTopic.name = this.newTopic.name.trim();
    if (this.newTopic.name === '' || this.newTopic.name.length < 1) {
      return;
    }

    this.newTopic.timestamp = new Date();

    this.userService.getUserWithEmail(this.authService.getUserEMail()).subscribe({
      next: res => {
        this.newTopic.author = res;

        this.topicService.create(this.newTopic).subscribe({
            next: () => {
              this.newTopic.name = '';
              this.loadTopics();
            },
            error: error => {
              console.error('Error creating new topic.', error);
              this.notification.error(error.error, 'Error could not create new topic.');
            }
          }
        );
      },
      error: error => {
        console.error('Error retrieving user.', error);
        this.notification.error(error.error, 'Error could not retrieve user.');
      }
    });

  }

  /**
   * Changes the value of the parameter
   *
   * @param container the container which should be created
   */
  public togglePin(id: number) {
    this.topicService.getByID(id).subscribe({
      next: (topic: Topic) => {
        let t = new Topic();
        t = topic;
        t.pinned = !topic.pinned;
        this.topicService.update(t).subscribe();

        setInterval(() => {
          this.loadTopics();
        }, 5 * 1000);
      },
      error: error => {
        console.log('Error toggling pinned.', error);
        this.notification.error(error, 'Error toggling pinned.');
      }
    });
  }

  /**
   * Changes the value of the parameter
   *
   * @param container the container which should be created
   */
  public togglePinTranscript(id: number) {
    this.transcriptService.getByID(id).subscribe({
      next: (transcript: Transcript) => {
        let t = new Transcript();
        t = transcript;
        t.pinned = !transcript.pinned;
        this.transcriptService.update(t).subscribe();

        setInterval(() => {
          this.loadTranscripts();
        }, 5 * 1000);
      },
      error: error => {
        console.log('Error toggling pinned.', error);
        this.notification.error(error, 'Error toggling pinned.');
      }
    });
  }

  /**
   * Deletes a container in the backend
   *
   * @param id of topic to delete
   */
  public deleteTopic(id: number) {
    this.topicService.deleteTopic(id).subscribe({
      next: () => {
        this.loadTopics();
      },
      error: error => {
        console.error('Error deleting topic', error);
        const errorMessage = error.status === 0
          ? 'There is a server issue. Try again later.'
          : error.error;
        this.notification.error(errorMessage, 'Error: Could not delete topic.');
      }
    });
  }

  /**
   * Deletes a container in the backend
   *
   * @param id of transcript to delete
   */
  public deleteTranscript(id: number) {
    this.transcriptService.deleteTranscript(id).subscribe({
      next: () => {
        this.loadTranscripts();
      },
      error: error => {
        console.error('Error deleting transcript.', error);
        const errorMessage = error.status === 0
          ? 'There is a server issue. Try again later.'
          : error.error;
        this.notification.error(errorMessage, 'Error: Could not delete transcript.');
      }
    });
  }

  /**
   * Loads all topics from the backend
   */
  private loadTopics() {
    if (this.authService.isLoggedIn()) {
      this.topicService.getTopics().subscribe({
        next: (topics: Topic[]) => {
          this.topics = topics;
          this.topics = this.sortTopics();
        },
        error: error => {
          console.error('Error retrieving topic', error);
          const errorMessage = error.status === 0
            ? 'There is a server issue. Try again later.'
            : error.error;
          this.notification.error(errorMessage, 'Error could not load topic.');
        }
      });
    }
  }

  /**
   * Loads all transcripts from the backend
   */
  private loadTranscripts() {
    if (this.authService.isLoggedIn()) {
      this.transcriptService.getTranscripts().subscribe({
        next: (transcripts: Transcript[]) => {
          this.transcripts = transcripts;
          this.transcripts = this.sortTranscript();
        },
        error: error => {
          console.log('Error retrieving transcript.', error);
          const errorMessage = error.status === 0
            ? 'There is a server issue. Try again later.'
            : error.error;
          this.notification.error(errorMessage, 'Error could not load transcript.');
        }
      });
    }
  }

  private sortTopics(): Topic[] {
    if (this.topics != null) {

      const sortedTopic = this.topics.sort((a, b) => {
        if (a.pinned === true && b.pinned === false) {
          return -1;
        }
        if (a.pinned === false && b.pinned === true) {
          return 1;
        }
        if (a.timestamp > b.timestamp) {
          return 1;
        }
        if (a.timestamp < b.timestamp) {
          return -1;
        }
        return 0;
      });
      return sortedTopic;
    }
  }

  private sortTranscript(): Transcript[] {
    if (this.transcripts != null) {

      const sortedTranscript = this.transcripts.sort((a, b) => {
        if (a.pinned === true && b.pinned === false) {
          return -1;
        }
        if (a.pinned === false && b.pinned === true) {
          return 1;
        }
        if (a.timestamp > b.timestamp) {
          return 1;
        }
        if (a.timestamp < b.timestamp) {
          return -1;
        }
        return 0;
      });
      return sortedTranscript;
    }
  }
}
