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
  function futureDate(offsetFromMonday = 0): string {
    const date = new Date();
    const daysUntilMonday = (8 - date.getDay()) % 7 || 7;

    date.setDate(date.getDate() + daysUntilMonday + offsetFromMonday);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

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
    const date = futureDate();

    component.form.patchValue({
      startAt: `${date}T15:00`,
      endAt: `${date}T14:00`,
    });

    expect(component.form.hasError('invalidPeriod')).toBe(true);
    expect(component.form.invalid).toBe(true);
  });

  it('should confirm a successful reservation', () => {
    const date = futureDate();
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

    service.create = () => throwError(() => new HttpErrorResponse({ status: 409 }));

    const date = futureDate();

    component.form.patchValue({
      startAt: `${date}T10:00`,
      endAt: `${date}T11:00`,
    });

    component.submit();
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(component.submissionSuccess()).toBe(false);
    expect(component.submitting()).toBe(false);
    expect(element.querySelector('[role="alert"]')?.textContent).toContain(
      'Créneau indisponible ou quota quotidien de réservation atteint.',
    );
  });

  const invalidPeriods = [
    {
      name: 'Sunday',
      start: `${futureDate(6)}T10:00`,
      end: `${futureDate(6)}T11:00`,
      error: 'closedDay',
    },
    {
      name: 'before opening hours',
      start: `${futureDate()}T08:00`,
      end: `${futureDate()}T09:00`,
      error: 'outsideHours',
    },
    {
      name: 'after closing hours',
      start: `${futureDate()}T18:00`,
      end: `${futureDate()}T20:00`,
      error: 'outsideHours',
    },
    {
      name: 'unaligned time slots',
      start: `${futureDate()}T10:15`,
      end: `${futureDate()}T11:15`,
      error: 'invalidSlot',
    },
    {
      name: 'duration below one hour',
      start: `${futureDate()}T10:00`,
      end: `${futureDate()}T10:30`,
      error: 'invalidDuration',
    },
    {
      name: 'duration above three hours',
      start: `${futureDate()}T10:00`,
      end: `${futureDate()}T13:30`,
      error: 'invalidDuration',
    },
    {
      name: 'reservation spanning two days',
      start: `${futureDate()}T18:00`,
      end: `${futureDate(1)}T10:00`,
      error: 'differentDay',
    },
  ];

  for (const period of invalidPeriods) {
    it(`should reject ${period.name}`, () => {
      component.form.patchValue({
        startAt: period.start,
        endAt: period.end,
      });

      expect(component.form.hasError(period.error)).toBe(true);
      expect(component.form.invalid).toBe(true);
    });
  }

  it('should allow a Saturday reservation ending at 19:00', () => {
    const date = futureDate(5);

    component.form.patchValue({
      startAt: `${date}T16:00`,
      endAt: `${date}T19:00`,
    });

    expect(component.form.valid).toBe(true);
  });
});
