import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { GoogleMarkComponent } from '../../shared/components/google-mark.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [GoogleMarkComponent],
  template: `
    <main class="login">
      <div class="card panel">
        <span class="badge">Phase 1</span>
        <h1 class="serif title">InterviewOS</h1>
        <p class="muted tagline">Prepare smarter. Interview better.</p>

        <button class="btn google" [disabled]="busy()" (click)="onGoogle()">
          <app-google-mark />
          {{ busy() ? 'Redirecting…' : 'Continue with Google' }}
        </button>

        <p class="muted fine">
          Sign-in is handled by the InterviewOS API using Google OAuth 2.0. We only use your Google
          account to identify you and sync interview events later.
        </p>
      </div>
    </main>
  `,
  styles: [
    `
      .login { min-height: 100vh; display: grid; place-items: center; padding: 64px 24px; }
      .panel { width: 100%; max-width: 380px; padding: 32px; box-shadow: var(--shadow-soft); }
      .badge {
        display: inline-flex; gap: 8px; border-radius: 999px; background: var(--accent);
        color: var(--accent-foreground); padding: 4px 12px; font-size: 12px; font-weight: 500;
      }
      .title { margin: 20px 0 0; font-size: 40px; line-height: 1.1; }
      .tagline { margin: 8px 0 0; font-size: 14px; }
      .google {
        margin-top: 32px; width: 100%; display: flex; align-items: center;
        justify-content: center; gap: 12px;
      }
      .fine { margin-top: 24px; font-size: 12px; line-height: 1.6; }
    `,
  ],
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  readonly busy = signal(false);

  onGoogle() {
    this.busy.set(true);
    this.auth.signInWithGoogle();
  }
}
