import { TestBed } from '@angular/core/testing';
import { ReservationService } from './reservation';
import { provideHttpClient } from '@angular/common/http';
import { HttpHeaders } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';

import { AuthService } from './auth';
import { environment } from '../../../environments/environment';

describe('ReservationService', () => {
  let service: ReservationService;

  let httpMock: HttpTestingController;

  const authServiceMock = {
    csrfHeaders: vi.fn(),
  };

  const apiUrl = `${environment.apiUrl}/reservations`;

  beforeEach(() => {
    vi.resetAllMocks();

    authServiceMock.csrfHeaders.mockReturnValue(
      of(new HttpHeaders().set('X-XSRF-TOKEN', 'test-token')),
    );

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authServiceMock },
      ],
    });

    service = TestBed.inject(ReservationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('creates a reservation with session and CSRF token', () => {
    const reservation = {
      meetingRoomId: 1,
      startAt: '2026-11-02T10:00:00',
      endAt: '2026-11-02T11:00:00',
    };

    service.create(reservation).subscribe();

    const request = httpMock.expectOne(apiUrl);

    expect(request.request.method).toBe('POST');
    expect(request.request.withCredentials).toBe(true);
    expect(request.request.headers.get('X-XSRF-TOKEN')).toBe('test-token');
    expect(request.request.body).toEqual(reservation);
    expect(authServiceMock.csrfHeaders).toHaveBeenCalledOnce();

    request.flush({
      id: 1,
      ...reservation,
      meetingRoomName: 'Simone Veil',
      status: 'ACTIVE',
    });
  });

  it('retrieves reservations with the session cookie', () => {
    service.getAll().subscribe();

    const request = httpMock.expectOne(apiUrl);

    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBe(true);
    expect(authServiceMock.csrfHeaders).not.toHaveBeenCalled();

    request.flush([]);
  });

  it('cancels a reservation with session and CSRF token', () => {
    service.cancel(1).subscribe();

    const request = httpMock.expectOne(`${apiUrl}/1/cancel`);

    expect(request.request.method).toBe('PATCH');
    expect(request.request.withCredentials).toBe(true);
    expect(request.request.headers.get('X-XSRF-TOKEN')).toBe('test-token');
    expect(authServiceMock.csrfHeaders).toHaveBeenCalledOnce();

    request.flush({
      id: 1,
      meetingRoomId: 1,
      meetingRoomName: 'Simone Veil',
      startAt: '2026-11-02T10:00:00',
      endAt: '2026-11-02T11:00:00',
      status: 'CANCELLED',
    });
  });
});
