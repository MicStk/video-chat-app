import {
  ChangeDetectorRef,
  ElementRef,
  Component,
  OnInit,
  TemplateRef,
  ViewChild,
  ViewChildren,
  QueryList,
  AfterViewInit,
  Renderer2
} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MessageService} from '../../services/message.service';
import {UserService} from '../../services/user.service';
import {TopicService} from '../../services/topic.service';
import {Message} from '../../dtos/message';
import {User} from '../../dtos/user';
import {Topic} from '../../dtos/topic';
import {NgbModal, NgbPaginationConfig} from '@ng-bootstrap/ng-bootstrap';
import {UntypedFormBuilder, NgForm, NgModel} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {ToastrService} from 'ngx-toastr';
import { from, lastValueFrom, Observable } from 'rxjs';
import { of } from 'rxjs';


@Component({
  selector: 'app-messages',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.scss']
})
export class MessageComponent implements OnInit, AfterViewInit {
  @ViewChild('scrollDown') scrollDown: ElementRef;
  @ViewChildren('item') itemElements: QueryList<any>;

  error = false;
  errorMessage = '';
  // After first submission attempt, form validation will start
  submitted = false;

  currentMessage: Message = {
    timestamp: new Date(),
    content: '',
    user: null,
    container: null,
    isTranscript: false
  };

  topic: Topic;
  topicName = '';
  currentUser: User;

  id = 1;
  deleteId = -1;

  messages: Message[];
  scrollContainer: any;
  items = [];
  isNearBottom = true;
  userEmail: string;
  userRole: string;

  editMode: boolean;
  deleteMode: boolean;
  newMessages = false;
  fragment = '/';

  constructor(private messageService: MessageService,
              private userService: UserService,
              private topicService: TopicService,
              private ngbPaginationConfig: NgbPaginationConfig,
              private formBuilder: UntypedFormBuilder,
              private cd: ChangeDetectorRef,
              private authService: AuthService,
              private renderer: Renderer2,
              private router: Router,
              private route: ActivatedRoute,
              private notification: ToastrService,
              private modalService: NgbModal) {
  }

  ngAfterViewInit() {
      this.scrollContainer = this.scrollDown.nativeElement;
     this.itemElements.changes.subscribe(_ => this.onItemElementsChanged());
      setTimeout(() => {
                      this.fragment = this.router.url.charAt(this.router.url.length-1);
                          const hashValue = this.router.url.charAt(this.router.url.length-2);
                          if (this.fragment !== '/' && hashValue === '#') {
                         (document.getElementById(this.fragment) as HTMLElement).scrollIntoView({behavior: 'smooth',
                         block: 'start', inline: 'nearest'});
                         this.fragment = '/';
                          }
                    }, 250);
  }

  ngOnInit() {
    const observable = this.route.params;
    this.editMode = false;
    this.deleteMode = false;

    observable.subscribe({
      next: (params) => {
        this.id = Number(params.id);
        this.loadTopic(this.id);
        this.loadMessages();
        this.userEmail = this.authService.getUserEMail();
        this.userRole = this.authService.getUserRole();
      },
      error: error => {
        console.log('Error while onInit.', error);
        this.notification.error(error.error, 'Error could not load transcript.');
      },
      complete: () => {
        this.scrollChatDown();
        this.newMessages = false;
      }
    });

        setInterval(() => {
            if (this.editMode === false && this.deleteMode === false) {
                 this.loadTopic(this.id);
                 this.loadMessages();
             // (document.getElementById("2") as HTMLElement).scrollIntoView({behavior: 'smooth', block: 'start', inline: 'nearest'});
            }
        }, 10 * 1000);
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
      top: this.scrollContainer.scrollHeight,
      left: 0,
      behavior: 'smooth'
    });
    this.newMessages = false;
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

  /**
   * Error flag will be deactivated, which clears the error message
   */

  editMessage(message: Message) {
    this.editMode = true;
    this.currentMessage = message;
  }

  copyLinkToClipboard(message: Message) {
    navigator.clipboard.writeText('!' + message.id);
  }

  containsValidationError(): boolean {
    if (this.currentMessage.content === '' || this.currentMessage.content.length < 1) {
      this.notification.info('Message must contain a character and can not only contain whitespaces',
        '', {closeButton: true, timeOut: 4000, progressBar: true});
      return true;
    }
    if (this.currentMessage.content.length > 1000) {
      this.notification.error('Message is too long. <br>(' + this.currentMessage.content.length + '/1000 characters are used)',
        '', {closeButton: true, timeOut: 4000, progressBar: true, enableHtml: true});
      return true;
    }
  }

  prepCurrentMessage() {
    this.currentMessage.user = new User();
    this.currentMessage.user.email = this.authService.getUserEMail();
    this.currentMessage.container = this.topic;
  }

  createAuthor(user: User): User {
    this.currentUser = user;
    const author = new User();
    author.id = this.currentUser.id;
    author.email = this.currentUser.email;
    author.firstName = this.currentUser.firstName;
    author.lastName = this.currentUser.lastName;
    author.role = this.currentUser.role;
    return author;
  }

  resetCurrentMessage() {
    this.currentMessage.content = '';
    this.currentMessage.container = null;
    this.currentMessage.id = null;
    this.currentMessage.editedAt = null;
  }

  /**
   * Sends message update request
   *
   * @param form contains message content to be updated
   */
  updateMessage(form: NgForm) {
    this.currentMessage.content = this.currentMessage.content.trim();
    if (this.containsValidationError()) {
      return;
    }
    this.prepCurrentMessage();
    const observable = this.userService.getUserWithEmail(this.authService.getUserEMail());

    observable.subscribe({
        next: (user: User) => {
          this.currentMessage.user = this.createAuthor(user);

          this.messageService.updateMessage(this.currentMessage).subscribe({
              next: () => {
                console.log(this.currentMessage);
                this.editMode = false;
                this.resetCurrentMessage();
                this.loadMessages();
              },
              error: error => {
                console.log('Error while updating message.' + error.error.error);
                this.notification.error(error.error.error, 'Error: Could not update message.');
              }
            }
          );
        },
        error: error => {
          console.error('Error could not load user', error);
        },
      }
    );
  }

  /**
   * Sends message creation request
   *
   * @param message the message which should be created
   */
  createMessage(form: NgForm) {
    this.currentMessage.content = this.currentMessage.content.trim();
    if (this.containsValidationError()) {
      return;
    }
    this.prepCurrentMessage();
    const observable = this.userService.getUserWithEmail(this.authService.getUserEMail());

    observable.subscribe({
        next: (user: User) => {
          this.currentMessage.user = this.createAuthor(user);

          this.currentMessage.timestamp = new Date();
          this.messageService.createMessage(this.currentMessage).subscribe({
              next: () => {
                console.log(this.currentMessage);
                this.resetCurrentMessage();
                this.loadMessages();
                setTimeout(() => {
                  this.scrollChatDown();
                }, 500);
              },
              error: error => {
                console.log('Error could not create message.' + error.error.error);
                this.notification.error(error.error.error, 'Error could not create message.');
              }
            }
          );
        },
        error: error => {
          console.error('Error could not load user', error);
        },
      }
    );
  }

  /**
   * Sets deleteID the value of the message which should be deleted
   *
   * @param id of message to delete
   */
    public setDeleteId(id: number) {
      this.deleteId = id;
      this.deleteMode = true;
    }

  /**
   * Sets deleteID to null and deleteMode to false
   *
   * @param id of message to delete
   */
    public endDeleteMode() {
      this.deleteId = null;
      this.deleteMode = false;
    }

  /**
   * Sets editMode to false
   */
    public endEditMode() {
      this.editMode = false;
      this.loadMessages();
      this.currentMessage.id = null;
      this.currentMessage.content = '';
    }

  /**
   * Deletes a message in the backend
   */
    public deleteMessage() {
        this.messageService.deleteMessage(this.deleteId).subscribe({
            next: () => {
                this.deleteId = null;
                this.deleteMode = false;
                this.loadMessages();
            },
            error: error => {
                console.error('Error deleting message', error);
                const errorMessage = error.status === 0
                    ? 'There is a server issue. Try again later.'
                    : error.error;
                this.notification.error(errorMessage, 'Error: Could not delete message.');
            }
        });
    }

  processLinks(e) {
    const element: HTMLElement = e.target;
    if (element.nodeName === 'A') {
      e.preventDefault();
      const link = element.getAttribute('routerLink');
      alert(link);
      this.router.navigate([link]);
    }
  }


  /**
   * Loads the specified page of messages from the backend
   */
  private loadMessages() {
    if (this.topic) {
      console.log('transcript.component.ts: loadMessages() with ' + this.topic.id);
      this.messageService.getMessagesByContainer(this.topic.id).subscribe({
        next: (messages: Message[]) => {
          console.log(messages);
          if (!this.isNearBottom
            && this.messages
            && this.messages.length < messages.length) {
            this.newMessages = true;
          }
          this.messages = messages;

          this.messages.forEach((message) => {
            message.renderedContent = from(new Promise<string>((resolve) => {
              resolve(message.content);
            }));
          });

          this.messages.forEach((message) => {
            message.renderedContent = this.getRenderedMessage(message);
          });
        },
        error: error => {
          console.log('Error while retrieving topic messages.' + error);
          this.notification.error(error.error, 'Error could not retrieve topic messages.');
        },
        complete: () => {
          this.sortMessages();
        }
      });
    }
  }

  private getRenderedMessage(message: Message): Observable<string> {
    return from(this.getRenderedMessagePromise(message));
  }

  private async getRenderedMessagePromise(message: Message): Promise<string> {
    const pattern = /(!\d+)/gm;
    let matches;
    let renderedMessage = message.content;
    let i = 0;


    do {
      matches = renderedMessage.match(pattern);
      console.log('MATCHES: ' + matches);

      if (matches !== null) {
        if (matches.length > 0) {
          const getMessage$ = this.messageService.getMessageById(matches[i].slice(1));
          const m: Message = await lastValueFrom(getMessage$);
          const messageLinkAndText: string[] = [];
          const folder = m.isTranscript ? 'meeting' : 'topic';
          messageLinkAndText.push('/' + folder + '/' + m.container.id.toString() + '/message#' + matches[i].slice(1).toString());
          messageLinkAndText.push(m.content);

          renderedMessage = [
            renderedMessage.slice(0, renderedMessage.indexOf(matches[i])),
            '<b><a href="/#' + messageLinkAndText[0] + '" routerLinkActive="active">',
            renderedMessage.slice(renderedMessage.indexOf(matches[i]), renderedMessage.indexOf(matches[i]) + matches[i].length),
            '</a></b>',
            renderedMessage.slice(renderedMessage.indexOf(matches[i]) + 3)
          ].join('');
          console.log(renderedMessage);
        }
      }
      i++;
    } while (matches !== null && matches.length > 0 && i <= matches.length - 1);
    return new Promise((resolve) => {
      resolve(renderedMessage);
    });
  }

  /**
   * Loads a topic from the backend
   */
  private loadTopic(id: number) {

    const observable = this.topicService.getByID(id);

    observable.subscribe({
      next: (topic: Topic) => {
        this.topic = topic;
        this.topicName = topic.name;
        this.loadMessages();
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
