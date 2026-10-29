
import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { AuthService } from '../../core/services/auth';
import { Connexion } from './connexion';

describe('Connexion', () => {
  const authServiceMock = {
    login: vi.fn(),
  };

  beforeEach(() => {
    vi.resetAllMocks();

    TestBed.configureTestingModule({
      imports: [Connexion],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    });
  });

  it('does not submit an invalid form', () => {
    const fixture = TestBed.createComponent(Connexion);
    fixture.detectChanges();

    fixture.componentInstance.submit();

    expect(authServiceMock.login).not.toHaveBeenCalled();
  });

  it('redirects to reservations after successful login', () => {
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigateByUrl')
      .mockResolvedValue(true);

    authServiceMock.login.mockReturnValue(of(void 0));

    const fixture = TestBed.createComponent(Connexion);
    fixture.detectChanges();

    fixture.componentInstance.form.setValue({
      email: 'test@example.com',
      password: 'password123456',
    });

    fixture.componentInstance.submit();

    expect(authServiceMock.login).toHaveBeenCalledWith(
      'test@example.com',
      'password123456',
    );

    expect(navigateSpy).toHaveBeenCalledWith('/reservations');
  });

  it('displays an error when credentials are incorrect', () => {
    authServiceMock.login.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 401 })),
    );

    const fixture = TestBed.createComponent(Connexion);
    fixture.detectChanges();

    fixture.componentInstance.form.setValue({
      email: 'test@example.com',
      password: 'wrong-password',
    });

    fixture.componentInstance.submit();

    expect(fixture.componentInstance.error())
      .toBe('Email ou mot de passe incorrect.');
  });
});
