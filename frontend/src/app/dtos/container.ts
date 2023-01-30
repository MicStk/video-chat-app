import { Message } from './message';
import { User } from './user';

export class Container {
    id?: number;
    name: string;
    author: User;
    timestamp: Date;
    pinned: boolean;
    messages: Message[];
    isTranscript: boolean;
}
