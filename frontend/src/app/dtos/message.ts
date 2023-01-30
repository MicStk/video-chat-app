import { User } from './user';
import { Container } from './container';
import { Observable } from 'rxjs';

export class Message {
    id?: number;
    timestamp: Date;
    content: string;
    user: User;
    container: Container;
    editedAt?: Date;
    isTranscript: boolean;
    renderedContent?: Observable<string>;
}
