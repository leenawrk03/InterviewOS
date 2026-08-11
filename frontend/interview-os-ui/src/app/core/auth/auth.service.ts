import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthUser } from '../../shared/models/user.model';

/**
 * Login flow (Spring Boot owns OAuth 2.0):
 *
 *   Angular  ->  Spring Boot /oauth2/authorization/google
 *            ->  Google consent
 *            ->  Spring Boot /login/oauth2/code/google  (session cookie set)
 *            ->  redirect back to Angular /dashboard
 *
 * Angular never talks to Google and never handles tokens: it only asks the API
 * "who am I?" via GET /api/auth/me with the session cookie.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly _user = signal<AuthUser | null>(null);
  private readonly _loading = signal(true);

  readonly user = this._user.asReadonly();
  readonly loading = this._loading.asReadonly();
  readonly isAuthenticated = computed(() => this._user() !== null);

  /** Resolves once the current user has been resolved (or found to be absent). */
  readonly ready: Promise<void>;

  constructor() {
    this.ready = this.loadCurrentUser();
  }

  async loadCurrentUser(): Promise<void> {
    try {
      const user = await firstValueFrom(
        this.http.get<AuthUser>(`${environment.apiBaseUrl}/api/auth/me`, {
          withCredentials: true,
        }),
      );
      this._user.set(user ?? null);
    } catch {
      this._user.set(null);
    } finally {
      this._loading.set(false);
    }
  }

  /** Hands the browser to Spring Boot, which starts the Google OAuth 2.0 dance. */
  signInWithGoogle(): void {
    const redirect = encodeURIComponent(`${window.location.origin}/dashboard`);
    window.location.href = `${environment.apiBaseUrl}/oauth2/authorization/google?redirect_uri=${redirect}`;
  }

  async signOut(): Promise<void> {
    try {
      await firstValueFrom(
        this.http.post(`${environment.apiBaseUrl}/api/auth/logout`, {}, { withCredentials: true }),
      );
    } catch {
      /* ignore — clear locally regardless */
    }
    this._user.set(null);
    window.location.href = '/';
  }

  displayName(): string {
    const user = this._user();
    if (user?.name) return user.name.split(' ')[0]!;
    return user?.email?.split('@')[0] ?? 'there';
  }

  avatarUrl(): string | null {
    return this._user()?.pictureUrl ?? null;
  }
}
