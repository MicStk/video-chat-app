import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';
import { AudioChunk } from '../dtos/audio-chunk';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AudioChunkService {

    private audioChunkBaseUri: string = this.globals.backendUri + '/audiochunks';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    create(audioChunk: AudioChunk): Observable<AudioChunk> {
        console.log('Create audio chunk');
        console.log(audioChunk);
        return this.httpClient.post<AudioChunk>(this.audioChunkBaseUri, audioChunk);
    }

}
