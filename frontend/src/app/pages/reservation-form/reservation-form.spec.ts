import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReservationForm } from './reservation-form';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of, throwError } from 'rxjs';

import { MeetingRoomService } from '../../core/services/meeting-room';
import { ReservationService } from '../../core/services/reservation';
import { HttpErrorResponse } from '@angular/common/http';

describe('ReservationForm', () => {
  let component: ReservationForm;
  let fixture: ComponentFixture<ReservationForm>;

  beforeEach(async () => {
    const meetingRoomServiceMock = {
      getAll: () =>
        of([
          {
            id: 2,
            name: 'Simone Veil',
            location: 'Étage 1',
            capacity: 6,
            equipment: ['Écran'],
            status: 'ACTIVE' as const,
          },
        ]),
    };

    const reservationServiceMock = {
      create: () => of({}),
    };

    await TestBed.configureTestingModule({
      imports: [ReservationForm],
      providers: [
        {
          provide: MeetingRoomService,
          useValue: meetingRoomServiceMock,
        },
        {
          provide: ReservationService,
          useValue: reservationServiceMock,
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: convertToParamMap({ roomId: '2' }),
            },
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ReservationForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should initialize the form with the selected room', () => {
    expect(component).toBeTruthy();
    expect(component.form.controls.meetingRoomId.value).toBe(2);
    expect(component.form.invalid).toBe(true);
  });

  it('should reject a reservation ending before it starts', () => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);

    const date = [
      tomorrow.getFullYear(),
      String(tomorrow.getMonth() + 1).padStart(2, '0'),
      String(tomorrow.getDate()).padStart(2, '0'),
    ].join('-');

    component.form.patchValue({
      startAt: `${date}T15:00`,
      endAt: `${date}T14:00`,
    });

    expect(component.form.hasError('invalidPeriod')).toBe(true);
    expect(component.form.invalid).toBe(true);
  });

it('should confirm a successful reservation', () => {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  const date = [
    tomorrow.getFullYear(),
    String(tomorrow.getMonth() + 1).padStart(2, '0'),
    String(tomorrow.getDate()).padStart(2, '0'),
  ].join('-');

  component.form.patchValue({
    startAt: `${date}T10:00`,
    endAt: `${date}T11:00`,
  });

  component.submit();

  expect(component.submissionSuccess()).toBe(true);
  expect(component.submitting()).toBe(false);
  expect(component.submissionError()).toBeNull();
});

it('should display an error when the reservation conflicts', () => {
  const service = TestBed.inject(ReservationService);

  service.create = () =>
    throwError(() => new HttpErrorResponse({ status: 409 }));

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  const date = [
    tomorrow.getFullYear(),
    String(tomorrow.getMonth() + 1).padStart(2, '0'),
    String(tomorrow.getDate()).padStart(2, '0'),
  ].join('-');

  component.form.patchValue({
    startAt: `${date}T10:00`,
    endAt: `${date}T11:00`,
  });

  component.submit();
  fixture.detectChanges();

  const element: HTMLElement = fixture.nativeElement;

  expect(component.submissionSuccess()).toBe(false);
  expect(component.submitting()).toBe(false);
  expect(element.querySelector('[role="alert"]')?.textContent)
    .toContain('Ce créneau est déjà réservé.');
});

});
