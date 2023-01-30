import { EventEmitter, Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class VoiceChatService {
    voiceChange: EventEmitter<string> = new EventEmitter<string>();
    private active = false;
    private muted = false;
    private sound = true;
    private cam = true;

    constructor() {
    }

    public isActive() {
        return this.active;
    }

    public toggleActive() {
        if (this.active) {
            this.voiceChange.emit('toggleActive');
        }
        this.active = !this.active;
    }

    public isMuted() {
        return this.muted;
    }

    public toggleMuted() {
        this.voiceChange.emit('toggleMuted');
        this.muted = !this.muted;
    }

    public isSound() {
        return this.sound;
    }

    public toggleSound() {
        this.voiceChange.emit('toggleSound');
        this.sound = !this.sound;
    }

    public isCam() {
        return this.cam;
    }

    public toggleCam() {
        this.voiceChange.emit('toggleCam');
        this.cam = !this.cam;
    }

    public getVoiceServiceEventEmitter() {
        return this.voiceChange;
    }

}
