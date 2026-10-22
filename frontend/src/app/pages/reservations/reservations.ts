import { Component, computed, inject, OnInit, signal } from '@angular/core';
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

  readonly statusFilter = signal<'ALL' | 'ACTIVE' | 'CANCELLED'>('ALL');

  readonly roomFilter = signal('ALL');

  readonly availableRooms = computed(() => {
    const rooms = new Map<number, string>();

    for (const reservation of this.reservations()) {
      rooms.set(reservation.meetingRoomId, reservation.meetingRoomName);
    }

    return Array.from(rooms, ([id, name]) => ({ id, name })).sort((a, b) =>
      a.name.localeCompare(b.name, 'fr'),
    );
  });

  readonly filteredReservations = computed(() => {
    const status = this.statusFilter();
    const room = this.roomFilter();

    return this.reservations().filter((reservation) => {
      const matchesStatus = status === 'ALL' || reservation.status === status;

      const matchesRoom = room === 'ALL' || String(reservation.meetingRoomId) === room;

      return matchesStatus && matchesRoom;
    });
  });

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
