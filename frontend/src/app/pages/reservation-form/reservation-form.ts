import { Component, inject, OnInit, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { MeetingRoomService, type MeetingRoom } from '../../core/services/meeting-room';
import { HttpErrorResponse } from '@angular/common/http';
import { ReservationService } from '../../core/services/reservation';
import { ActivatedRoute } from '@angular/router';

function getMaxReservationDate(): string {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 6;

  const lastDay = new Date(year, month + 1, 0).getDate();
  const day = Math.min(today.getDate(), lastDay);

  const maxDate = new Date(year, month, day);

  return [
    maxDate.getFullYear(),
    String(maxDate.getMonth() + 1).padStart(2, '0'),
    String(maxDate.getDate()).padStart(2, '0'),
  ].join('-');
}

function reservationPeriodValidator(control: AbstractControl): ValidationErrors | null {
  const startAt = control.get('startAt')?.value;
  const endAt = control.get('endAt')?.value;

  if (!startAt || !endAt) {
    return null;
  }

  const start = new Date(startAt);
  const end = new Date(endAt);

  if (!Number.isFinite(start.getTime()) || !Number.isFinite(end.getTime())) {
    return { invalidDates: true };
  }

  if (start.getTime() <= Date.now()) {
    return { dateNotFuture: true };
  }

  if (startAt.slice(0, 10) > getMaxReservationDate()) {
  return { dateTooFar: true };
}

  if (end <= start) {
    return { invalidPeriod: true };
  }

  if (start.toDateString() !== end.toDateString()) {
    return { differentDay: true };
  }

  if (start.getDay() === 0) {
    return { closedDay: true };
  }

  const startMinutes = start.getHours() * 60 + start.getMinutes();
  const endMinutes = end.getHours() * 60 + end.getMinutes();

  if (startMinutes < 9 * 60 || endMinutes > 19 * 60) {
    return { outsideHours: true };
  }

  if (
    start.getMinutes() % 30 !== 0 ||
    end.getMinutes() % 30 !== 0 ||
    start.getSeconds() !== 0 ||
    end.getSeconds() !== 0 ||
    start.getMilliseconds() !== 0 ||
    end.getMilliseconds() !== 0
  ) {
    return { invalidSlot: true };
  }

  const durationMinutes = (end.getTime() - start.getTime()) / 60_000;

  if (durationMinutes < 60 || durationMinutes > 180 || durationMinutes % 30 !== 0) {
    return { invalidDuration: true };
  }

  return null;
}

@Component({
  selector: 'app-reservation-form',
  imports: [ReactiveFormsModule],
  templateUrl: './reservation-form.html',
  styleUrl: './reservation-form.css',
})
export class ReservationForm implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly meetingRoomService = inject(MeetingRoomService);
  private readonly reservationService = inject(ReservationService);
  private readonly route = inject(ActivatedRoute);

  readonly submitting = signal(false);
  readonly submissionSuccess = signal(false);
  readonly submissionError = signal<string | null>(null);

  readonly rooms = signal<MeetingRoom[]>([]);
  readonly roomsLoading = signal(true);
  readonly roomsError = signal(false);

  readonly maxReservationDate = getMaxReservationDate();

  readonly form = this.formBuilder.nonNullable.group(
    {
      meetingRoomId: [0, [Validators.required, Validators.min(1)]],
      startAt: ['', Validators.required],
      endAt: ['', Validators.required],
    },
    { validators: reservationPeriodValidator },
  );

readonly reservationDate = this.formBuilder.nonNullable.control('');
readonly startTime = this.formBuilder.nonNullable.control('');
readonly endTime = this.formBuilder.nonNullable.control('');

// Start times: 09:00 to 18:00, every 30 minutes.
readonly startTimeOptions = Array.from({ length: 19 }, (_, index) => {
  const minutes = 9 * 60 + index * 30;
  return this.formatTime(minutes);
});

// End times depend on the selected start time.
get endTimeOptions(): string[] {
  const start = this.startTime.value;

  if (!start) {
    return [];
  }

  const [hours, minutes] = start.split(':').map(Number);
  const startMinutes = hours * 60 + minutes;

  return [60, 90, 120, 150, 180]
    .map((duration) => startMinutes + duration)
    .filter((endMinutes) => endMinutes <= 19 * 60)
    .map((endMinutes) => this.formatTime(endMinutes));
}

private formatTime(minutes: number): string {
  const hours = Math.floor(minutes / 60);
  const remainder = minutes % 60;

  return `${String(hours).padStart(2, '0')}:${String(remainder).padStart(2, '0')}`;
}

updateDateTime(): void {
  const date = this.reservationDate.value;
  const start = this.startTime.value;
  const end = this.endTime.value;

  this.form.patchValue({
    startAt: date && start ? `${date}T${start}` : '',
    endAt: date && end ? `${date}T${end}` : '',
  });
}

updateStartTime(): void {
  this.endTime.setValue('');
  this.updateDateTime();
}


  ngOnInit(): void {
    this.meetingRoomService.getAll().subscribe({
      next: (rooms) => {
        this.rooms.set(rooms);

        const roomId = Number(this.route.snapshot.queryParamMap.get('roomId'));

        const selectedRoom = rooms.find((room) => room.id === roomId && room.status === 'ACTIVE');

        if (selectedRoom) {
          this.form.controls.meetingRoomId.setValue(selectedRoom.id);
        }

        this.roomsLoading.set(false);
      },

      error: () => {
        this.roomsError.set(true);
        this.roomsLoading.set(false);
      },
    });
  }

  submit(): void {
    this.form.markAllAsTouched();
    this.form.updateValueAndValidity();

    if (this.form.invalid || this.submitting()) {
      return;
    }

    this.submitting.set(true);
    this.submissionSuccess.set(false);
    this.submissionError.set(null);

    this.reservationService.create(this.form.getRawValue()).subscribe({
      next: () => {
        this.submitting.set(false);
        this.submissionSuccess.set(true);
      },
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);

        if (error.status === 409) {
          this.submissionError.set(
            'Créneau indisponible ou quota quotidien de réservation atteint.',
          );
        } else if (error.status === 400) {
          this.submissionError.set('Les informations de réservation sont invalides.');
        } else if (error.status === 404) {
          this.submissionError.set('La salle sélectionnée est introuvable.');
        } else {
          this.submissionError.set('Impossible de créer la réservation.');
        }
      },
    });
  }
}
