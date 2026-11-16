import { Component, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-connexion',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css',
})
export class Connexion {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });
  loginDemo(): void {
    if (this.submitting()) {
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    this.authService.login('demo@meetingbook.example', 'Demo2026!').subscribe({
      next: () => {
        this.submitting.set(false);
        void this.router.navigateByUrl('/salles');
      },
      error: () => {
        this.submitting.set(false);
        this.error.set('Impossible de se connecter au compte démo. Veuillez réessayer.');
      },
    });
  }

  submit(): void {
    this.form.markAllAsTouched();

    if (this.form.invalid || this.submitting()) {
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const { email, password } = this.form.getRawValue();

    this.authService.login(email, password).subscribe({
      next: () => {
        this.submitting.set(false);
        void this.router.navigateByUrl('/reservations');
      },
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);

        this.error.set(
          error.status === 401
            ? 'Email ou mot de passe incorrect.'
            : 'Impossible de se connecter. Veuillez réessayer.',
        );
      },
    });
  }
}
