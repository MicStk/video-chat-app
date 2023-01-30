import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class Globals {
    readonly backendUri: string = this.findBackendUrl();

    private findBackendUrl(): string {

        const str = window.location.host;
        const split = str.split(':', 1);
        const host = split[0];
        return 'https' + '://' + host + ':8080' + '/api/v1';
    }


}


