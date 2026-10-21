
import { Component, inject, OnInit } from '@angular/core';
import { MeetingRoomService } from '../../core/services/meeting-room';

@Component({
  selector: 'app-rooms',
  imports: [],
  templateUrl: './rooms.html',
  styleUrl: './rooms.css',
})
export class Rooms implements OnInit {
  private readonly meetingRoomService = inject(MeetingRoomService);

  ngOnInit(): void {
    this.meetingRoomService.getAll().subscribe({
      next: (rooms) => {
        console.log('Meeting rooms:', rooms);
      },
      error: (error) => {
        console.error('Failed to load meeting rooms:', error);
      },
    });
  }
}

