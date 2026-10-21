import { TestBed } from '@angular/core/testing';
import { MeetingRoomService } from './meeting-room';
import { provideHttpClient } from '@angular/common/http';

describe('MeetingRoom', () => {
 let service: MeetingRoomService;

 beforeEach(() => {
  TestBed.configureTestingModule({
    providers: [provideHttpClient()],
  });
  service = TestBed.inject(MeetingRoomService);
});

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
