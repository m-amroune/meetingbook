
import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, switchMap } from 'rxjs';

import { environment } from '../../../environments/environment';
import {
  ReservationRequest,
  ReservationResponse,
} from '../models/reservation';
import { AuthService } from './auth';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly apiUrl = `${environment.apiUrl}/reservations`;

  create(request: ReservationRequest): Observable<ReservationResponse> {
    return this.authService.csrfHeaders().pipe(
      switchMap((headers) =>
        this.http.post<ReservationResponse>(this.apiUrl, request, {
          headers,
          withCredentials: true,
        }),
      ),
    );
  }

  getAll(): Observable<ReservationResponse[]> {
    return this.http.get<ReservationResponse[]>(this.apiUrl, {
      withCredentials: true,
    });
  }

  cancel(id: number): Observable<ReservationResponse> {
    return this.authService.csrfHeaders().pipe(
      switchMap((headers) =>
        this.http.patch<ReservationResponse>(
          `${this.apiUrl}/${id}/cancel`,
          {},
          {
            headers,
            withCredentials: true,
          },
        ),
      ),
    );
  }
}
