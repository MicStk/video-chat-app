import {TestBed} from '@angular/core/testing';

import {TranscriptService} from './transcript.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {ReactiveFormsModule} from '@angular/forms';

describe('CreateService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
  }));

  it('should be created', () => {
    const service: TranscriptService = TestBed.inject(TranscriptcService);
    expect(service).toBeTruthy();
  });
});
