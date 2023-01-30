import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {UserCreateComponent} from './components/user-create/user-create.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {SearchComponent} from './components/search/search.component';
import {TranscriptComponent} from './components/transcript/transcript.component';

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'user-create', canActivate: [AuthGuard], component: UserCreateComponent},
  {path: 'topic/:id/message', canActivate: [AuthGuard], component: MessageComponent},
  {path: 'meeting/:id/message', canActivate: [AuthGuard], component: TranscriptComponent},
  {path: 'search', canActivate: [AuthGuard], component: SearchComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true, anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
