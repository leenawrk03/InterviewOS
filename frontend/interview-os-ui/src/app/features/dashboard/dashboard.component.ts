import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';
import { InterviewService } from '../../core/services/interview.service';
import { DashboardStats, EMPTY_STATS, Interview } from '../../shared/models/interview.model';
import { StatCardComponent } from '../../shared/components/stat-card.component';
import { DashboardHeaderComponent } from './dashboard-header.component';
import { UpcomingInterviewsComponent } from '../dashboard/upcoming-interviews.component';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DashboardHeaderComponent, StatCardComponent, UpcomingInterviewsComponent],
  template: `
    <div class="page">
      <app-dashboard-header />

      <main class="main">
        <h1 class="serif greet">{{ greeting }}, {{ auth.displayName() }}</h1>
        <p class="muted sub">Here's what's happening with your interviews.</p>

        <div class="stats">
          <app-stat-card label="Upcoming" [value]="stats().upcoming" />
          <app-stat-card label="This week" [value]="stats().thisWeek" />
          <app-stat-card label="Prepared" [value]="stats().prepared" />
        </div>

        <app-upcoming-interviews [items]="interviews()" />
      </main>
    </div>
  `,
  styles: [
    `
      .page { min-height: 100vh; }
      .main { max-width: 880px; margin: 0 auto; padding: 40px 24px; }
      .greet { margin: 0; font-size: 30px; }
      .sub { margin: 8px 0 0; font-size: 14px; }
      .stats { margin-top: 32px; display: grid; gap: 12px; grid-template-columns: repeat(3, 1fr); }
      @media (max-width: 640px) { .stats { grid-template-columns: 1fr; } }
    `,
  ],
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
  private readonly service = inject(InterviewService);

  readonly stats = signal<DashboardStats>(EMPTY_STATS);
  readonly interviews = signal<Interview[]>([]);
  readonly greeting = DashboardComponent.greetingFor(new Date().getHours());

  constructor() {
    this.service.stats().subscribe((s) => this.stats.set(s));

    this.service.upcoming().subscribe({
      next: (items) => this.interviews.set(items),
      error: () => this.interviews.set([]),
    });
  }

  private static greetingFor(hour: number): string {
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  }
}
