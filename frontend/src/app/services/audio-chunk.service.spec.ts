import { TestBed } from '@angular/core/testing';

import { AudioChunkService } from './audio-chunk.service';

describe('AudioChunkService', () => {
  let service: AudioChunkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AudioChunkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
