import { Routes } from '@angular/router';
import { Rooms } from './pages/rooms/rooms';

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
];
