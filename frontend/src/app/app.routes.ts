
import { Routes } from '@angular/router';
import { Rooms } from './pages/rooms/rooms';
import { RoomDetail } from './pages/room-detail/room-detail';
import { ReservationForm } from './pages/reservation-form/reservation-form';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'salles',
    pathMatch: 'full',
  },
  {
    path: 'salles',
    component: Rooms,
  },
  {
    path: 'salles/:id',
    component: RoomDetail,
  },
  {
  path: 'reservations/nouvelle',
  component: ReservationForm,
},
];
