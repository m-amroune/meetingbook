
import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { AuthService } from '../../core/services/auth';
import { Inscription } from './inscription';

describe('Inscription', () => {
  const authServiceMock = {
    register: vi.fn(),
  };

  beforeEach(() => {
    vi.resetAllMocks();

    TestBed.configureTestingModule({
      imports: [Inscription],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    });
  });

  it('does not submit an invalid form', () => {
    const fixture = TestBed.createComponent(Inscription);
    fixture.detectChanges();

    fixture.componentInstance.submit();

    expect(authServiceMock.register).not.toHaveBeenCalled();
  });

  it('redirects to login after successful registration', () => {
    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigateByUrl')
      .mockResolvedValue(true);

    authServiceMock.register.mockReturnValue(of({ id: 1 }));

    const fixture = TestBed.createComponent(Inscription);
    fixture.detectChanges();

    fixture.componentInstance.form.setValue({
      email: 'new@example.com',
      password: 'password123456',
    });

    fixture.componentInstance.submit();

    expect(authServiceMock.register).toHaveBeenCalledWith({
      email: 'new@example.com',
      password: 'password123456',
    });

    expect(navigateSpy).toHaveBeenCalledWith('/connexion');
  });

  it('displays an error when the email already exists', () => {
    authServiceMock.register.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409 })),
    );

    const fixture = TestBed.createComponent(Inscription);
    fixture.detectChanges();

    fixture.componentInstance.form.setValue({
      email: 'existing@example.com',
      password: 'password123456',
    });

    fixture.componentInstance.submit();

    expect(fixture.componentInstance.error())
      .toBe('Un compte existe déjà avec cette adresse email.');
  });
});
