
import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { firstValueFrom, Observable, of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { AuthService } from '../services/auth';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  const authServiceMock = {
    getCurrentUser: vi.fn(),
  };

  beforeEach(() => {
    vi.resetAllMocks();

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: authServiceMock,
        },
      ],
    });
  });

  function runGuard(): Observable<boolean | UrlTree> {
    return TestBed.runInInjectionContext(
      () =>
        authGuard(
          {} as ActivatedRouteSnapshot,
          {} as RouterStateSnapshot,
        ),
    ) as Observable<boolean | UrlTree>;
  }

  it('allows access when the user is authenticated', async () => {
    authServiceMock.getCurrentUser.mockReturnValue(
      of({ email: 'test@example.com' }),
    );

    const result = await firstValueFrom(runGuard());

    expect(result).toBe(true);
    expect(authServiceMock.getCurrentUser).toHaveBeenCalledOnce();
  });

  it('redirects to login when the user is not authenticated', async () => {
    authServiceMock.getCurrentUser.mockReturnValue(
      throwError(() => new Error('Unauthorized')),
    );

    const result = await firstValueFrom(runGuard());

    expect(result).toBeInstanceOf(UrlTree);
    expect((result as UrlTree).toString()).toBe('/connexion');
  });
});
