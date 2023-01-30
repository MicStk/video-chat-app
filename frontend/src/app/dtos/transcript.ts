import { User } from './user';
import { Container } from './container';

export class Transcript extends Container {
    id?: number;
    name: string;
    author: User;
    description: string;
    timestamp: Date;
    endTime: Date;
    summary: string;
    progress: number;
    totalSteps: number;
    isTranscript = true;
}
