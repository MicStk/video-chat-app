import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { Message } from '../dtos/message';
import { MessageSearch } from '../dtos/message-search';
import { Observable } from 'rxjs';
import { Globals } from '../global/globals';

@Injectable({
    providedIn: 'root'
})
export class MessageService {

    private messageBaseUri: string = this.globals.backendUri + '/messages';

    constructor(
        private httpClient: HttpClient,
        private datePipe: DatePipe,
        private globals: Globals
    ) { }

    /**
     * Loads all messages from the backend
     */
    getMessages(): Observable<Message[]> {
        return this.httpClient.get<Message[]>(this.messageBaseUri);
    }

    getMessagesByContainer(containerId: number): Observable<Message[]> {
        console.log('message.service.ts: getMessagesByContainer ' + containerId);
        const params = new HttpParams()
            .set('containerId', containerId);
        return this.httpClient.get<Message[]>(this.messageBaseUri, {params});
    }

    /**
     * Loads specific message from the backend
     *
     * @param id of message to load
     */
    getMessageById(id: number): Observable<Message> {
        console.log('Load message for ' + id);
        return this.httpClient.get<Message>(this.messageBaseUri + '/' + id);
    }

    /**
     * Loads all fitting messages from the backend
     *
     * @param searchParam the parameters used for the search
     */
    searchMessages(messageSearch: MessageSearch): Observable<Message[]> {
        console.log('message.service.ts: searchMessages', messageSearch);
        let params = new HttpParams();
        if (messageSearch.text !== null) {
          params = params.append('text', messageSearch.text);
        }
        if (messageSearch.author !== null) {
          params = params.append('author', messageSearch.author);
        }
        if (messageSearch.fromTime !== null) {
          params = params.append('fromTime', new Date(messageSearch.fromTime).toISOString());
        }
        if (messageSearch.toTime !== null) {
          params = params.append('toTime', new Date(messageSearch.toTime).toISOString());
        }
        return this.httpClient.get<Message[]>(this.messageBaseUri + '/search', {params});
    }

    /**
     * Persists message to the backend
     *
     * @param message to persist
     */
    createMessage(message: Message): Observable<Message> {
        console.log('Create message by user ' + message.user.firstName + ' ' + message.user.lastName);
        return this.httpClient.post<Message>(this.messageBaseUri, message);
    }

    /**
     * Update message in the backend
     *
     * @param message to update
     */
    updateMessage(message: Message): Observable<Message> {
        console.log('Update message with id' + message.id);
        return this.httpClient.put<Message>(this.messageBaseUri + '/' + message.id, message);;
    }

     /**
      * Deletes a message in the backend
      *
      * @param id of message to delete
      */
    deleteMessage(id: number): Observable<Message> {
        console.log('Delete message with id ' + id);
        return this.httpClient.delete<Message>(this.messageBaseUri + '/' + id);
    }


}
