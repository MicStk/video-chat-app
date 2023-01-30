import { Injectable } from '@angular/core';
import { Meeting } from '../dtos/meeting';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';

@Injectable({
    providedIn: 'root'
})
export class MeetingService {

    private meetingBaseUri: string = this.globals.backendUri + '/meetings';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    /**
     * Loads all meetings from the backend
     */
    getMeetings(): Observable<Meeting[]> {
        return this.httpClient.get<Meeting[]>(this.meetingBaseUri);
    }

    /**
     * Get a meeting from the backend by id
     *
     * @param id of the meeting
     */
    getByID(id: number): Observable<Meeting> {
        console.log('Get a message by the id ' + id);
        return this.httpClient.get<Meeting>(this.meetingBaseUri + '/' + id);
    }

    /**
     * Update a meeting in the backend
     *
     * @param meeting to update
     */
    update(meeting: Meeting): Observable<Meeting> {
        console.log('Updated meeting with id ' + meeting.id);
        return this.httpClient.put<Meeting>(this.meetingBaseUri + '/' + meeting.id, meeting);
    }

    /**
     * Create a meeting in the backend
     *
     * @param meeting to create
     */
    create(meeting: Meeting): Observable<Meeting> {
        console.log('Create meeting with title ' + meeting.title);
        return this.httpClient.post<Meeting>(this.meetingBaseUri, meeting);
    }

}
