
import { Component, inject, OnInit, signal } from '@angular/core';
import {
  MeetingRoomService,
  type MeetingRoom,
} from '../../core/services/meeting-room';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-rooms',
  imports: [RouterLink],
  templateUrl: './rooms.html',
  styleUrl: './rooms.css',
})
export class Rooms implements OnInit {
  private readonly meetingRoomService = inject(MeetingRoomService);

  readonly rooms = signal<MeetingRoom[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.meetingRoomService.getAll().subscribe({
      next: (rooms) => {
        this.rooms.set(rooms);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les salles.');
        this.loading.set(false);
      },
    });
  }
}

