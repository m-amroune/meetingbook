import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export type MeetingRoomStatus = 'ACTIVE' | 'OUT_OF_SERVICE';

export interface MeetingRoom {
  id: number;
  name: string;
  location: string;
  capacity: number;
  equipment: string[];
  status: MeetingRoomStatus;
}

@Injectable({
  providedIn: 'root',
})
export class MeetingRoomService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/meeting-rooms`;

  getAll(): Observable<MeetingRoom[]> {
    return this.http.get<MeetingRoom[]>(this.apiUrl);
  }

  getById(id: number): Observable<MeetingRoom> {
    return this.http.get<MeetingRoom>(`${this.apiUrl}/${id}`);
  }
}
