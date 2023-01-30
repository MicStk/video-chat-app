import {
  ChangeDetectorRef,
  ElementRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild,
  ViewChildren,
  QueryList,
  AfterViewInit
} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MessageService} from '../../services/message.service';
import {UserService} from '../../services/user.service';
import {TranscriptService} from '../../services/transcript.service';
import {Message} from '../../dtos/message';
import {User} from '../../dtos/user';
import {Transcript} from '../../dtos/transcript';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder, NgForm, NgModel} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-transcript',
  templateUrl: './transcript.component.html',
  styleUrls: ['./transcript.component.scss']
})
export class TranscriptComponent implements OnInit, AfterViewInit {
  @ViewChild('scrollDown') scrollDown: ElementRef;
  @ViewChildren('messageList') itemElements: QueryList<Message>;

  error = false;
  errorMessage = '';
  submitted = false;


  currentMessage: Message = {
    timestamp: new Date(),
    content: '',
    user: null,
    container: null,
    isTranscript: true
  };

  transcript: Transcript;
  transcriptName = '';
  currentUser: User;
  id = 1;

  messages: Message[];
  isNearBottom = true;
  scrollContainer: any;
  newMessages = false;
  showDescription = false;
  showSummary = false;

  constructor(private messageService: MessageService,
              private userService: UserService,
              private transcriptService: TranscriptService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private router: Router,
              private route: ActivatedRoute,
              private notification: ToastrService,
              private modalService: NgbModal) {


  }

  ngAfterViewInit() {
    this.scrollContainer = this.scrollDown.nativeElement;
    this.itemElements.changes.subscribe(_ => this.onItemElementsChanged());
  }

  ngOnInit() {
    const observable = this.route.params;

    observable.subscribe({
      next: (params) => {
        this.id = Number(params.id);
        this.loadTranscript(this.id);
        this.loadMessages();
      },
      error: error => {
        console.log('Error while onInit.', error);
        this.notification.error(error.error, 'Error could not load transcript.');

      },
      complete: () => {
        this.scrollChatDown();
      }
    });

    setInterval(() => {
      this.loadTranscript(this.id);
      this.loadMessages();
    }, 5 * 1000);
  }

  onItemElementsChanged(): void {
    if (this.isNearBottom) {
      this.scrollChatDown();
    }
  }

  /**
   * Scroll the chat down to the bottom
   */
  scrollChatDown(): void {
    this.scrollContainer.scroll({
      top: this.scrollDown.nativeElement.scrollHeight,
      left: 0,
      behavior: 'smooth'
    });
  }

  copyLinkToClipboard(message: Message) {
    navigator.clipboard.writeText('!' + message.id);
  }

  isUserNearBottom(): boolean {
    const threshold = 150;
    const position = this.scrollContainer.scrollTop + this.scrollContainer.offsetHeight;
    const height = this.scrollContainer.scrollHeight;
    return position > height - threshold;
  }

  scrolled(event: any): void {
    this.isNearBottom = this.isUserNearBottom();
  }

  /**
   * Returns true if the authenticated user is an admin
   */
  isAdmin(): boolean {
    return this.authService.getUserRole() === 'ADMIN';
  }

  formatUserName(user: User | null): string {
    return (user == null)
      ? ''
      : `${user.firstName} ${user.lastName}`;
  }

  sortMessages(): Message[] {
    if (this.messages != null) {
      const sortedMessage = this.messages.sort((a, b) => {
        if (a.timestamp > b.timestamp) {
          return 1;
        }
        if (a.timestamp < b.timestamp) {
          return -1;
        }
        return 0;
      });
      return sortedMessage;
    }
  }

  toggleDescription() {
    this.showDescription = this.showDescription === false ? true : false;
  }

  toggleSummary() {
    this.showSummary = this.showSummary === false ? true : false;
  }

    /**
     * Loads the specified page of messages from the backend
     */
    private loadMessages() {
        if (this.transcript) {
            console.log('transcript.component.ts: loadMessages() with ' + this.transcript.id);
            this.messageService.getMessagesByContainer(this.transcript.id).subscribe({
                next: (messages: Message[]) => {
                    console.log('messages: ' + messages);
                  if (!this.isNearBottom
                    && this.messages
                    && this.messages.length < messages.length) {
                    this.newMessages = true;
                  }
                    this.messages = messages;
                },
                error: error => {
                    console.log('Error while retrieving topic messages.' + error);
                    this.notification.error(error.error, 'Error could not retrieve topic messages.');
                },
                complete: () => {
                    this.sortMessages();
                    this.scrolled(true);
                }
            });
        }
    }

  /**
   * Loads the transcript from the backend
   */
  private loadTranscript(id: number) {
    this.transcriptService.getByID(id).subscribe({
      next: (transcript: Transcript) => {
        this.transcript = transcript;
        this.transcriptName = transcript.name;
        this.loadMessages();
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

