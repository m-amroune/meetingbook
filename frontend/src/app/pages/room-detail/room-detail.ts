
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute,  RouterLink } from '@angular/router';
import {
  MeetingRoomService,
  type MeetingRoom,
} from '../../core/services/meeting-room';

@Component({
  selector: 'app-room-detail',
  imports: [RouterLink],
  templateUrl: './room-detail.html',
  styleUrl: './room-detail.css',
})
export class RoomDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly meetingRoomService = inject(MeetingRoomService);

  readonly room = signal<MeetingRoom | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!Number.isInteger(id) || id <= 0) {
      this.error.set('Identifiant de salle invalide.');
      this.loading.set(false);
      return;
    }

    this.meetingRoomService.getById(id).subscribe({
      next: (room) => {
        this.room.set(room);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger la salle.');
        this.loading.set(false);
      },
    });
  }
}

