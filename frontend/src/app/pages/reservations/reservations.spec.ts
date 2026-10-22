import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Reservations } from './reservations';

import { ReservationService } from '../../core/services/reservation';
import { vi } from 'vitest';
import { of, throwError } from 'rxjs';

describe('Reservations', () => {
  let component: Reservations;
  let fixture: ComponentFixture<Reservations>;

  beforeEach(async () => {
    const reservationServiceMock = {
      getAll: () =>
        of([
          {
            id: 1,
            meetingRoomId: 2,
            meetingRoomName: 'Simone Veil',
            startAt: '2026-10-23T10:00:00',
            endAt: '2026-10-23T11:00:00',
            status: 'CANCELLED',
          },
        ]),
    };

    await TestBed.configureTestingModule({
      imports: [Reservations],
      providers: [
        {
          provide: ReservationService,
          useValue: reservationServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Reservations);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

it('should display reservations returned by the service', () => {
  const element: HTMLElement = fixture.nativeElement;

  expect(element.textContent).toContain('Simone Veil');
  expect(element.textContent).toContain('23/10/2026 10:00');
  expect(element.textContent).toContain('Annulée');
});

it('should cancel an active reservation and update the table', () => {
  const reservation = component.reservations()[0];

  component.reservations.set([
    { ...reservation, status: 'ACTIVE' },
  ]);

  const service = TestBed.inject(ReservationService);

  service.cancel = () =>
    of({ ...reservation, status: 'CANCELLED' });

  const confirmSpy = vi.spyOn(window, 'confirm').mockReturnValue(true);

  fixture.detectChanges();

  const button: HTMLButtonElement =
    fixture.nativeElement.querySelector('tbody button');

  button.click();
  fixture.detectChanges();

  expect(confirmSpy).toHaveBeenCalledOnce();
  expect(component.reservations()[0].status).toBe('CANCELLED');
  expect(fixture.nativeElement.textContent).toContain('Annulée');
  expect(fixture.nativeElement.querySelector('tbody button')).toBeNull();

  confirmSpy.mockRestore();
});

it('should display an error when loading reservations fails', async () => {
  const service = TestBed.inject(ReservationService);

  vi.spyOn(service, 'getAll').mockReturnValue(
    throwError(() => new Error('API unavailable'))
  );

  const errorFixture = TestBed.createComponent(Reservations);
  errorFixture.detectChanges();
  await errorFixture.whenStable();

  const element: HTMLElement = errorFixture.nativeElement;

  expect(element.querySelector('[role="alert"]')?.textContent)
    .toContain('Impossible de charger les réservations.');

  expect(element.querySelector('table')).toBeNull();

  vi.restoreAllMocks();
});

it('should display an error when loading reservations fails', async () => {
  const service = TestBed.inject(ReservationService);

  vi.spyOn(service, 'getAll').mockReturnValue(
    throwError(() => new Error('API unavailable'))
  );

  const errorFixture = TestBed.createComponent(Reservations);
  errorFixture.detectChanges();
  await errorFixture.whenStable();

  const element: HTMLElement = errorFixture.nativeElement;

  expect(element.querySelector('[role="alert"]')?.textContent)
    .toContain('Impossible de charger les réservations.');

  expect(element.querySelector('table')).toBeNull();

  vi.restoreAllMocks();
});


});
