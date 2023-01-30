import { Injectable } from '@angular/core';
import { Topic } from '../dtos/topic';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';

@Injectable({
    providedIn: 'root'
})
export class TopicService {

    private topicBaseUri: string = this.globals.backendUri + '/topics';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    /**
     * Loads all topics from the backend
     */
    getTopics(): Observable<Topic[]> {
        return this.httpClient.get<Topic[]>(this.topicBaseUri);
    }

    /**
     * Get a container from the backend by id
     *
     * @param id of the container
     */
    getByID(id: number): Observable<Topic> {
        console.log('Get a container by the id ' + id);
        return this.httpClient.get<Topic>(this.topicBaseUri + '/' + id);
    }

    /**
     * Update a container in the backend
     *
     * @param topic to update
     */
    update(topic: Topic): Observable<Topic> {
        console.log('Updated container with id ' + topic.id);
        return this.httpClient.put<Topic>(this.topicBaseUri + '/' + topic.id, topic);
    }

    /**
     * Create a container in the backend
     *
     * @param topic to create
     */
    create(topic: Topic): Observable<Topic> {
        console.log('Create container with name ' + topic.name);
        return this.httpClient.post<Topic>(this.topicBaseUri, topic);
    }

    /**
     * Deletes a container in the backend
     *
     * @param id of topic to delete
     */
    deleteTopic(id: number): Observable<Topic> {
        console.log('Delete container with id ' + id);
        return this.httpClient.delete<Topic>(this.topicBaseUri + '/' + id);
    }


}
