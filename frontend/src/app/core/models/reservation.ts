export interface ReservationRequest {
  meetingRoomId: number;
  startAt: string;
  endAt: string;
}


export type ReservationStatus = 'ACTIVE' | 'CANCELLED';

export interface ReservationResponse {
  id: number;
  meetingRoomId: number;
  meetingRoomName: string;
  startAt: string;
  endAt: string;
  status: ReservationStatus;
}
