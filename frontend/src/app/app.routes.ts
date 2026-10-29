import { Routes } from '@angular/router';
import { Rooms } from './pages/rooms/rooms';
import { RoomDetail } from './pages/room-detail/room-detail';
import { ReservationForm } from './pages/reservation-form/reservation-form';
import { Reservations } from './pages/reservations/reservations';
import { Connexion } from './pages/connexion/connexion';
import { Inscription } from './pages/inscription/inscription';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'salles',
    pathMatch: 'full',
  },
  {
  path: 'connexion',
  component: Connexion,
},
{
  path: 'inscription',
  component: Inscription,
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
  canActivate: [authGuard],
},
{
  path: 'reservations',
  component: Reservations,
  canActivate: [authGuard],
},
];
