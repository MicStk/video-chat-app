import { Message } from './message';
import { Container } from './container';
import { User } from './user';

export class Topic extends Container {
    id?: number;
    name: string;
    author: User;
    timestamp: Date;
    pinned: boolean;
    messages: Message[];
    isTranscript = false;
}
