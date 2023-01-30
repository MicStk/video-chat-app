import { User } from './user';

export class Meeting {
    id?: number;
    title: string;
    author: User;
    description: string;
    startTime: Date;
    endTime: Date;
    summary: string;
}
