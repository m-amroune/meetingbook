import { TestBed } from '@angular/core/testing';
import { MeetingRoom } from './meeting-room';

describe('MeetingRoom', () => {
  let service: MeetingRoom;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MeetingRoom);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
