
import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet,
} from '@angular/router';
import { catchError, filter, of, startWith, switchMap } from 'rxjs';

import { AuthService, AuthUser } from './core/services/auth';

@Component({
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly title = signal('frontend');
  readonly currentUser = signal<AuthUser | null>(null);
  readonly loggingOut = signal(false);
  readonly logoutError = signal<string | null>(null);

  constructor() {
    // Refresh the session state after each navigation.
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        startWith(null),
        switchMap(() =>
          this.authService.getCurrentUser().pipe(
            catchError(() => of(null)),
          ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((user) => this.currentUser.set(user));
  }

  logout(): void {
    if (this.loggingOut()) {
      return;
    }

    this.loggingOut.set(true);
    this.logoutError.set(null);

    this.authService.logout().subscribe({
      next: () => {
        this.loggingOut.set(false);
        this.currentUser.set(null);
        void this.router.navigateByUrl('/connexion');
      },
      error: () => {
        this.loggingOut.set(false);
        this.logoutError.set(
          'Impossible de se déconnecter. Veuillez réessayer.',
        );
      },
    });
  }
}
