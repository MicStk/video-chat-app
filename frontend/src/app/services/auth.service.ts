import { Injectable } from '@angular/core';
import { AuthRequest } from '../dtos/auth-request';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
// @ts-ignore
import jwt_decode from 'jwt-decode';
import { Globals } from '../global/globals';
import { UserService } from './user.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private authBaseUri: string = this.globals.backendUri + '/authentication';

    constructor(private httpClient: HttpClient, private globals: Globals, private userService: UserService) {
    }

    /**
     * Login in the user. If it was successful, a valid JWT token will be stored
     *
     * @param authRequest User data
     */
    loginUser(authRequest: AuthRequest): Observable<string> {
        return this.httpClient.post(this.authBaseUri, authRequest, {responseType: 'text'})
            .pipe(
                tap((authResponse: string) => this.setToken(authResponse))
            );
    }


    /**
     * Check if a valid JWT token is saved in the localStorage
     */
    isLoggedIn() {
        return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
    }

    logoutUser() {
        console.log('Logout');
        localStorage.removeItem('authToken');
        localStorage.removeItem('userFirstName');
        localStorage.removeItem('userLastName');
        localStorage.removeItem('userEmail');
    }

    getToken() {
        return localStorage.getItem('authToken');
    }

    /**
     * Returns the user role based on the current token
     */
    getUserRole() {
        if (this.getToken() != null) {
            const decoded: any = jwt_decode(this.getToken());
            const authInfo: string[] = decoded.rol;
            if (authInfo.includes('ROLE_ADMIN')) {
                return 'ADMIN';
            } else if (authInfo.includes('ROLE_EMPLOYEE')) {
                return 'EMPLOYEE';
            }
        }
        return 'UNDEFINED';
    }

    /**
     * Returns the user based on the email
     */
    getUserEMail(authToken?: string) {
        if (authToken) {
            const decoded: any = jwt_decode(authToken);
            const authInfo: string = decoded.sub;
            return authInfo;
        } else if (localStorage.getItem('userEmail')) {
            return localStorage.getItem('userEmail');
        } else if (localStorage.getItem('authToken')) {
            const decoded: any = jwt_decode(localStorage.getItem('authToken'));
            const authInfo: string = decoded.sub;
            return authInfo;
        }
    }

    getName(): string {
        return localStorage.getItem('userFirstName') + '_' + localStorage.getItem('userLastName');
    }

    private setToken(authResponse: string) {
        localStorage.setItem('authToken', authResponse);
        this.setUser(authResponse);
    }

    private getTokenExpirationDate(token: string): Date {

        const decoded: any = jwt_decode(token);
        if (decoded.exp === undefined) {
            return null;
        }

        const date = new Date(0);
        date.setUTCSeconds(decoded.exp);
        return date;
    }

    private setUser(authToken: string) {
        const eMail = this.getUserEMail(authToken);
        console.log('setting User', eMail);
        this.userService.getUserWithEmail(eMail).subscribe({
            next: user => {
                console.log('got user' + user);
                localStorage.setItem('userFirstName', user.firstName);
                localStorage.setItem('userLastName', user.lastName);
                localStorage.setItem('userEmail', user.email);
            }
        });
    }
}
