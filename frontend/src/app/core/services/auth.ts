
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable, switchMap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuthUser {
  email: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
}

interface CsrfToken {
  headerName: string;
  token: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/auth`;

 csrfHeaders(): Observable<HttpHeaders> {
  return this.http
    .get<CsrfToken>(`${this.apiUrl}/csrf`, {
      withCredentials: true,
    })
    .pipe(
      map(() => {
        const cookie = document.cookie
          .split('; ')
          .find((entry) => entry.startsWith('XSRF-TOKEN='));

        if (!cookie) {
          throw new Error('Cookie CSRF introuvable');
        }

        const token = decodeURIComponent(
          cookie.substring('XSRF-TOKEN='.length),
        );

        return new HttpHeaders().set('X-XSRF-TOKEN', token);
      }),
    );
}

  register(request: RegisterRequest): Observable<{ id: number }> {
    return this.csrfHeaders().pipe(
      switchMap((headers) =>
        this.http.post<{ id: number }>(
          `${this.apiUrl}/register`,
          request,
          {
            headers,
            withCredentials: true,
          },
        ),
      ),
    );
  }

  login(email: string, password: string): Observable<void> {
    const body = new HttpParams()
      .set('email', email)
      .set('password', password);

    return this.csrfHeaders().pipe(
      switchMap((headers) =>
        this.http.post<void>(`${this.apiUrl}/login`, body, {
          headers,
          withCredentials: true,
        }),
      ),
    );
  }

  getCurrentUser(): Observable<AuthUser> {
    return this.http.get<AuthUser>(`${this.apiUrl}/me`, {
      withCredentials: true,
    });
  }

  logout(): Observable<void> {
    return this.csrfHeaders().pipe(
      switchMap((headers) =>
        this.http.post<void>(
          `${this.apiUrl}/logout`,
          null,
          {
            headers,
            withCredentials: true,
          },
        ),
      ),
    );
  }
}
