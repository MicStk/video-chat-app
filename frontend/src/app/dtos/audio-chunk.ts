import { User } from './user';

export class AudioChunk {
    id: number;
    author: User;
    timestamp: Date;
    data: string;
}
