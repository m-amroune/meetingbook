
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  ActivatedRoute,
  convertToParamMap,
  provideRouter,
} from '@angular/router';
import { of, throwError } from 'rxjs';

import { MeetingRoomService } from '../../core/services/meeting-room';
import { RoomDetail } from './room-detail';
import { HttpErrorResponse } from '@angular/common/http';

describe('RoomDetail', () => {
  let fixture: ComponentFixture<RoomDetail>;

  beforeEach(async () => {
    const meetingRoomServiceMock = {
      getById: (id: number) =>
        of({
          id,
          name: 'Simone Veil',
          location: 'Étage 1',
          capacity: 6,
          equipment: ['Écran', 'Tableau blanc'],
          status: 'ACTIVE' as const,
        }),
    };

    await TestBed.configureTestingModule({
      imports: [RoomDetail],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({ id: '2' }),
            },
          },
        },
        {
          provide: MeetingRoomService,
          useValue: meetingRoomServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RoomDetail);
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should display meeting room details', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.textContent).toContain('Simone Veil');
    expect(element.textContent).toContain('Étage 1');
    expect(element.textContent).toContain('6 personnes');
    expect(element.textContent).toContain('Écran');
    expect(element.textContent).toContain('Tableau blanc');
  });

it('should display an error when the room does not exist', async () => {
  const service = TestBed.inject(MeetingRoomService);

  service.getById = () =>
    throwError(() => new HttpErrorResponse({ status: 404 }));

  const errorFixture = TestBed.createComponent(RoomDetail);
  errorFixture.detectChanges();
  await errorFixture.whenStable();

  const element: HTMLElement = errorFixture.nativeElement;
  const alert = element.querySelector('[role="alert"]');

  expect(alert?.textContent).toContain(
    'Impossible de charger la salle.'
  );
});

});
