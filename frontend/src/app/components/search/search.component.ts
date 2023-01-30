import {Component, OnInit} from '@angular/core';
import {MessageService} from '../../services/message.service';
import {UserService} from '../../services/user.service';
import {Message} from '../../dtos/message';
import {MessageSearch} from '../../dtos/message-search';
import {fromEvent, of, interval, debounce} from 'rxjs';
import {ToastrService} from 'ngx-toastr';
import {Router, withEnabledBlockingInitialNavigation} from '@angular/router';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

  ms: MessageSearch = {
    text: null,
    author: null,
    fromTime: null,
    toTime: null
  };
  messageGroups: Message[][] = [];

  constructor(
    private service: MessageService,
    private userService: UserService,
    private notification: ToastrService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.reloadMessages();

    const keyup = fromEvent(document, 'keyup');
    const change = fromEvent(document, 'change');

    keyup.pipe(
      debounce(i => interval(200)))
      .subscribe(x => this.reloadMessages());
    change.pipe(
      debounce(i => interval(100)))
      .subscribe(x => this.reloadMessages());
  }

  reloadMessages() {
    console.log(this.ms);
    this.service.searchMessages(this.ms)
      .subscribe({
        next: data => {
          this.setMessagesByContainer(data);
        },
        error: error => {
          console.error('Error fetching messages', error);
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
        }
      }
    );
  }

  resetSearchParameters() {
    (document.getElementById('searchContent') as HTMLInputElement).value = '';
    (document.getElementById('searchAuthor') as HTMLInputElement).value = '';
    (document.getElementById('searchFromDate') as HTMLInputElement).value = '';
    (document.getElementById('searchToDate') as HTMLInputElement).value = '';
    this.service.getMessages().subscribe({
      next: data => {
        this.setMessagesByContainer(data);
      },
      error: error => {
        console.error('Error fetching messages', error);
        this.notification.error(error, 'Error while fetching messages');
      }
    });
  }

  setMessagesByContainer(messages: Message[]) {
    const workGroups: {[key: string]: Array<Message>} = {};
    for (const message of messages) {
      const containerName: string = message.container.name;
      if (!(containerName in workGroups)) {
        workGroups[containerName] = new Array<Message>();
      }
      workGroups[containerName].push(message);
    }
    this.messageGroups = Object.values(workGroups);
    //this.messageGroups = [messages]; // TODO: Turn workGroups into a Message[][].
  }

  userSuggestions = (input: string) => (input === '')
      ? of([])
      : this.userService.getUserWithName(input);

  navigateTo(message: Message) {
    const id = '' + message.container.id;
    const messageId = '' + message.id;
    const folder = message.isTranscript ? 'meeting' : 'topic';
    this.router.navigate(['..', folder, id, 'message'], {fragment: messageId});
  }
}
