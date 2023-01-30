import { Injectable } from '@angular/core';
import { User } from '../dtos/user';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Globals } from '../global/globals';

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private userBaseUri: string = this.globals.backendUri + '/users';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    /**
     * Create the user. If it was successful, the user dto is returned
     *
     * @param user User data to create
     * @throws ValidateError if provided data fails validation
     * @throws ConflictError if provided data is inconsistent with currently persisted data
     */
    createUser(user: User): Observable<UserService> {
        console.log('Create user ' + user.firstName + ' ' + user.lastName);
        console.log(this.userBaseUri);
        return this.httpClient.post<UserService>(this.userBaseUri, user);
    }

    /**
     * Get the user with given Email
     *
     * @param email User Email
     */
    getUserWithEmail(email: string): Observable<User> {
        console.log('Get user with email ' + email);
        return this.httpClient.get<User>(this.userBaseUri + '/' + email);
    }

    /**
     * Get the user with given Email
     *
     * @param name User Name
     */
    getUserWithName(name: string): Observable<User[]> {
        console.log('Get user with name ' + name);
        return this.httpClient.get<User[]>(this.userBaseUri + '/name/' + name);
    }
}
