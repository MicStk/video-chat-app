import { Component } from '@angular/core';
import {AuthService} from './services/auth.service';
import {VoiceChatService} from './services/voice-chat.service';
import {Router} from '@angular/router';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'Chatify4Business';
  chatOpen = true;
  constructor(public authService: AuthService, public voiceService: VoiceChatService, private router: Router) { }

  toggleChat() {
    document.getElementById('chat').hidden = this.chatOpen;
    this.chatOpen = !this.chatOpen;
    if(!this.chatOpen) {
      document.getElementById('chatContainer').style.maxWidth = '50px';
    }else {
      document.getElementById('chatContainer').style.maxWidth = '100%';
    }

  }

  getRoute() {
    return this.router.url;
  }
}
