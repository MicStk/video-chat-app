// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
    production: false,
    // for Angular Cli development with generated ssl certificate(helm directory)
    wssPath: 'wss://' + getHost() + ':8080' + '/signalingSocket',
    // eslint-disable-next-line @typescript-eslint/naming-convention
    RTCPeerConfiguration: {
        // // Google
        // iceServers: [
        //   { urls: 'stun:stun.stunprotocol.org:3478' },
        //   { urls: 'stun:stun.l.google.com:19302' },
        //   {
        //     urls: 'turn:openrelay.metered.ca:80',
        //     username: 'openrelayproject',
        //     credential: 'openrelayproject',
        //   },
        // ]
        // Metered
        iceServers: [
            {
                urls: 'stun:relay.metered.ca:80',
            },
            {
                urls: 'turn:relay.metered.ca:80',
                username: 'f32111ed815178e0be62d3b1',
                credential: 'DmqSaaDAd6IDuKW1',
            },
            {
                urls: 'turn:relay.metered.ca:443',
                username: 'f32111ed815178e0be62d3b1',
                credential: 'DmqSaaDAd6IDuKW1',
            },
            {
                urls: 'turn:relay.metered.ca:443?transport=tcp',
                username: 'f32111ed815178e0be62d3b1',
                credential: 'DmqSaaDAd6IDuKW1',
            },
        ],
    }
};

// eslint-disable-next-line prefer-arrow/prefer-arrow-functions
function getHost() {
    const str = window.location.host;
    console.log(str);
    const split = str.split(':', 1);
    return split[0];
}


/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
