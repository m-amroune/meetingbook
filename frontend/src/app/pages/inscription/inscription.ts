import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-inscription',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './inscription.html',
  styleUrl: './inscription.css',
})
export class Inscription {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly submitting = signal(false);
  readonly error = signal<string | null>(null);

  readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    this.form.markAllAsTouched();

    if (this.form.invalid || this.submitting()) {
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const request = this.form.getRawValue();

    this.authService.register(request).subscribe({
      next: () => {
        this.submitting.set(false);
        void this.router.navigateByUrl('/connexion');
      },
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);

        this.error.set(
          error.status === 409
            ? 'Un compte existe déjà avec cette adresse email.'
            : 'Impossible de créer le compte. Veuillez réessayer.',
        );
      },
    });
  }
}
