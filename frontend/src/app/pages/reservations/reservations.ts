import { Component, inject, OnInit, signal } from '@angular/core';
import { ReservationService } from '../../core/services/reservation';
import { ReservationResponse } from '../../core/models/reservation';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-reservations',
  imports: [DatePipe],
  templateUrl: './reservations.html',
  styleUrl: './reservations.css',
})
export class Reservations implements OnInit {
  private readonly reservationService = inject(ReservationService);

  readonly reservations = signal<ReservationResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly cancellingId = signal<number | null>(null);
  readonly cancellationError = signal<string | null>(null);

  ngOnInit(): void {
    this.reservationService.getAll().subscribe({
      next: (reservations) => {
        this.reservations.set(reservations);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les réservations.');
        this.loading.set(false);
      },
    });
  }

  cancelReservation(id: number): void {
    if (this.cancellingId() !== null) {
      return;
    }
    const confirmed = window.confirm('Voulez-vous vraiment annuler cette réservation ?');

    if (!confirmed) {
      return;
    }

    this.cancellingId.set(id);
    this.cancellationError.set(null);

    this.reservationService.cancel(id).subscribe({
      next: (updatedReservation) => {
        this.reservations.update((reservations) =>
          reservations.map((reservation) =>
            reservation.id === id ? updatedReservation : reservation,
          ),
        );

        this.cancellingId.set(null);
      },
      error: () => {
        this.cancellationError.set("Impossible d'annuler la réservation.");
        this.cancellingId.set(null);
      },
    });
  }
}
