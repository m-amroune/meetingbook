
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { environment } from '../../../environments/environment';
import { AuthService } from './auth';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const apiUrl = `${environment.apiUrl}/auth`;

  beforeEach(() => {
    document.cookie = 'XSRF-TOKEN=csrf-cookie-token; path=/';
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    document.cookie = 'XSRF-TOKEN=; Max-Age=0; path=/';
  });

  it('retrieves the authenticated user', () => {
    let email: string | undefined;

    service.getCurrentUser().subscribe((user) => {
      email = user.email;
    });

    const request = httpMock.expectOne(`${apiUrl}/me`);

    expect(request.request.method).toBe('GET');
    expect(request.request.withCredentials).toBe(true);

    request.flush({ email: 'test@example.com' });

    expect(email).toBe('test@example.com');
  });

  it('logs in with form data and a CSRF token', () => {
    let completed = false;

    service.login('test@example.com', 'password123456').subscribe(() => {
      completed = true;
    });

    const csrf = httpMock.expectOne(`${apiUrl}/csrf`);

    expect(csrf.request.withCredentials).toBe(true);

    csrf.flush({
      headerName: 'X-XSRF-TOKEN',
      token: 'csrf-test-token',
    });

    const request = httpMock.expectOne(`${apiUrl}/login`);

    expect(request.request.method).toBe('POST');
    expect(request.request.withCredentials).toBe(true);
    expect(request.request.headers.get('X-XSRF-TOKEN'))
      .toBe('csrf-cookie-token');
    expect(request.request.body.get('email'))
      .toBe('test@example.com');
    expect(request.request.body.get('password'))
      .toBe('password123456');

    request.flush(null);

    expect(completed).toBe(true);
  });

  it('registers a new user with a CSRF token', () => {
    let userId: number | undefined;

    service.register({
      email: 'new@example.com',
      password: 'password123456',
    }).subscribe((response) => {
      userId = response.id;
    });

    const csrf = httpMock.expectOne(`${apiUrl}/csrf`);

    csrf.flush({
      headerName: 'X-XSRF-TOKEN',
      token: 'csrf-test-token',
    });

    const request = httpMock.expectOne(`${apiUrl}/register`);

    expect(request.request.method).toBe('POST');
    expect(request.request.withCredentials).toBe(true);
    expect(request.request.headers.get('X-XSRF-TOKEN'))
      .toBe('csrf-cookie-token');
    expect(request.request.body).toEqual({
      email: 'new@example.com',
      password: 'password123456',
    });

    request.flush({ id: 1 }, { status: 201, statusText: 'Created' });

    expect(userId).toBe(1);
  });

  it('logs out with the session cookie and CSRF token', () => {
    let completed = false;

    service.logout().subscribe(() => {
      completed = true;
    });

    const csrf = httpMock.expectOne(`${apiUrl}/csrf`);

    csrf.flush({
      headerName: 'X-XSRF-TOKEN',
      token: 'csrf-test-token',
    });

    const request = httpMock.expectOne(`${apiUrl}/logout`);

    expect(request.request.method).toBe('POST');
    expect(request.request.withCredentials).toBe(true);
    expect(request.request.headers.get('X-XSRF-TOKEN'))
      .toBe('csrf-cookie-token');

    request.flush(null);

    expect(completed).toBe(true);
  });
  it('rejects registration when the CSRF cookie is missing', () => {
  document.cookie = 'XSRF-TOKEN=; Max-Age=0; path=/';

  let errorMessage: string | undefined;

  service.register({
    email: 'new@example.com',
    password: 'password123456',
  }).subscribe({
    error: (error: Error) => {
      errorMessage = error.message;
    },
  });

  const csrf = httpMock.expectOne(`${apiUrl}/csrf`);

  csrf.flush({
    headerName: 'X-XSRF-TOKEN',
    token: 'masked-token',
  });

  expect(errorMessage).toBe('Cookie CSRF introuvable');
  httpMock.expectNone(`${apiUrl}/register`);
});
});
