import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {ToastrModule} from 'ngx-toastr';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {VoiceChatComponent} from './components/voice-chat/voice-chat.component';
import {UserCreateComponent} from './components/user-create/user-create.component';
import {SidebarComponent} from './components/sidebar/sidebar.component';
import {TranscriptComponent} from './components/transcript/transcript.component';
import {CustomReuseStrategy} from './app-reuse-routing';
import {RouteReuseStrategy} from '@angular/router';
import {VoiceChatService} from './services/voice-chat.service';
import {SearchComponent} from './components/search/search.component';
import {DatePipe} from '@angular/common';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    VoiceChatComponent,
    UserCreateComponent,
    SidebarComponent,
    TranscriptComponent,
    SearchComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ToastrModule.forRoot(),
    // Needed for Toastr
    BrowserAnimationsModule,
  ],
  providers: [
    httpInterceptorProviders, {provide: RouteReuseStrategy, useClass: CustomReuseStrategy},
    VoiceChatService,
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
