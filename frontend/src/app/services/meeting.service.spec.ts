import {TestBed} from '@angular/core/testing';

import {MeetingService} from './meeting.service';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {ReactiveFormsModule} from '@angular/forms';

describe('MeetingService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [HttpClientTestingModule, RouterTestingModule, ReactiveFormsModule],
  }));

  it('should be created', () => {
    const service: MeetingService = TestBed.inject(MeetingService);
    expect(service).toBeTruthy();
  });
});
