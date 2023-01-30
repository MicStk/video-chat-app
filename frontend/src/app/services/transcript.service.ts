import { Injectable } from '@angular/core';
import { Transcript } from '../dtos/transcript';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';

@Injectable({
    providedIn: 'root'
})
export class TranscriptService {

    private transcriptBaseUri: string = this.globals.backendUri + '/transcripts';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    /**
     * Loads all transcripts from the backend
     */
    getTranscripts(): Observable<Transcript[]> {
        return this.httpClient.get<Transcript[]>(this.transcriptBaseUri);
    }

    /**
     * Get a transcript from the backend by id
     *
     * @param id of the transcript
     */
    getByID(id: number): Observable<Transcript> {
        console.log('Get a transcript by the id ' + id);
        return this.httpClient.get<Transcript>(this.transcriptBaseUri + '/' + id);
    }

    /**
     * Update a container in the backend
     *
     * @param topic to update
     */
    update(transcript: Transcript): Observable<Transcript> {
        console.log('Updated container with id ' + transcript.id);
        return this.httpClient.put<Transcript>(this.transcriptBaseUri + '/' + transcript.id, transcript);
    }

    /**
     * Deletes a container in the backend
     *
     * @param id of transcript to delete
     */
    deleteTranscript(id: number): Observable<Transcript> {
        console.log('Delete container with id ' + id);
        return this.httpClient.delete<Transcript>(this.transcriptBaseUri + '/' + id);
    }

}
