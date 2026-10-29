
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';

import { App } from './app';
import { AuthService } from './core/services/auth';

describe('App', () => {
  const authServiceMock = {
    getCurrentUser: vi.fn(),
    logout: vi.fn(),
  };

  beforeEach(async () => {
    vi.resetAllMocks();
    authServiceMock.getCurrentUser.mockReturnValue(of(null));

    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the MeetingBook brand', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('.sidebar-brand')?.textContent)
      .toContain('MeetingBook');
  });

  it('shows login and registration links to guests', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const actions = compiled.querySelector('.sidebar-actions');

    expect(actions?.textContent).toContain('Se connecter');
    expect(actions?.textContent).toContain('Créer un compte');
    expect(compiled.querySelector('.sidebar-logout')).toBeNull();
  });

  it('shows the authenticated user and logout button', () => {
    authServiceMock.getCurrentUser.mockReturnValue(
      of({ email: 'test@example.com' }),
    );

    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('.sidebar-user')?.textContent)
      .toContain('test@example.com');

    expect(compiled.querySelector('.sidebar-logout')).not.toBeNull();
    expect(compiled.querySelector('.sidebar-actions')).toBeNull();
  });

  it('clears the session and redirects after logout', () => {
    authServiceMock.getCurrentUser.mockReturnValue(
      of({ email: 'test@example.com' }),
    );
    authServiceMock.logout.mockReturnValue(of(void 0));

    const router = TestBed.inject(Router);
    const navigateSpy = vi.spyOn(router, 'navigateByUrl')
      .mockResolvedValue(true);

    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    fixture.componentInstance.logout();
    fixture.detectChanges();

    expect(authServiceMock.logout).toHaveBeenCalledOnce();
    expect(fixture.componentInstance.currentUser()).toBeNull();
    expect(navigateSpy).toHaveBeenCalledWith('/connexion');
  });

  it('displays an error when logout fails', () => {
    authServiceMock.getCurrentUser.mockReturnValue(
      of({ email: 'test@example.com' }),
    );

    authServiceMock.logout.mockReturnValue(
      throwError(() => new Error('Logout failed')),
    );

    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    fixture.componentInstance.logout();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.querySelector('.sidebar-error')?.textContent)
      .toContain('Impossible de se déconnecter');

    expect(fixture.componentInstance.currentUser()?.email)
      .toBe('test@example.com');
  });
});
