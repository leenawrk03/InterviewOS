import { Component, inject } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  template: `
    <header class="header">
      <div class="inner">
        <span class="serif brand">InterviewOS</span>
        <div class="user">
          @if (auth.avatarUrl()) {
            <img [src]="auth.avatarUrl()" alt="" class="avatar" />
          } @else {
            <span class="avatar initial">{{ auth.displayName().charAt(0).toUpperCase() }}</span>
          }
          <span class="name">{{ auth.displayName() }}</span>
          <button class="btn btn-ghost" (click)="auth.signOut()">Sign out</button>
        </div>
      </div>
    </header>
  `,
  styles: [
    `
      .header {
        position: sticky; top: 0; z-index: 10;
        border-bottom: 1px solid var(--border);
        background: color-mix(in srgb, var(--background) 85%, transparent);
        backdrop-filter: blur(8px);
      }
      .inner {
        max-width: 880px; margin: 0 auto; padding: 16px 24px;
        display: flex; align-items: center; justify-content: space-between;
      }
      .brand { font-size: 20px; }
      .user { display: flex; align-items: center; gap: 12px; }
      .avatar { width: 32px; height: 32px; border-radius: 999px; object-fit: cover; }
      .initial {
        display: grid; place-items: center; background: var(--accent);
        color: var(--accent-foreground); font-size: 12px; font-weight: 500;
      }
      .name { font-size: 14px; }
    `,
  ],
})
export class DashboardHeaderComponent {
  readonly auth = inject(AuthService);
}
