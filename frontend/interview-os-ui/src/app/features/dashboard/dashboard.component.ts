// PATH: src/app/features/dashboard/dashboard.component.ts
// FIX: the template used *ngFor but CommonModule/NgFor was never imported,
// so the stat cards never rendered. Switched to the @for block syntax.

import { Component, inject, signal } from '@angular/core';

import { AuthService } from '../../core/auth/auth.service';
import { InterviewService } from '../../core/services/interview.service';
import { InterviewPrepService } from '../../core/services/interview-prep.service';
import { DashboardStats, EMPTY_STATS, Interview } from '../../shared/models/interview.model';
import { InterviewPrep } from '../../shared/models/interview-prep.model';
import { StatCardComponent } from '../../shared/components/stat-card.component';
import { DashboardHeaderComponent } from './dashboard-header.component';
import { UpcomingInterviewsComponent } from './upcoming-interviews.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DashboardHeaderComponent, StatCardComponent, UpcomingInterviewsComponent],
  template: `
    <div class="page">
      <div class="main">
        <app-dashboard-header></app-dashboard-header>

        <h1 class="greet">{{ greeting }}, {{ auth.displayName() }}</h1>
        <p class="sub">Here's what's happening with your interviews.</p>

        <div class="stats">
          @for (s of statCards; track s.label) {
            <app-stat-card [label]="s.label" [value]="s.value"></app-stat-card>
          }
        </div>

        <div class="upcoming">
          <app-upcoming-interviews
            [interviews]="interviews()"
            [prepByEvent]="prepByEvent()"
          ></app-upcoming-interviews>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .page { min-height: 100vh; }
      .main { max-width: 880px; margin: 0 auto; padding: 40px 24px; }
      .greet { margin: 0; font-size: 30px; }
      .sub { margin: 8px 0 0; font-size: 14px; }
      .stats {
        margin-top: 32px;
        display: grid;
        gap: 12px;
        grid-template-columns: repeat(3, 1fr);
      }
      @media (max-width: 640px) { .stats { grid-template-columns: 1fr; } }
      .upcoming { margin-top: 40px; }
    `,
  ],
})
export class DashboardComponent {
  readonly auth = inject(AuthService);
  private readonly service = inject(InterviewService);
  private readonly prepService = inject(InterviewPrepService);

  readonly stats = signal<DashboardStats>(EMPTY_STATS);
  readonly interviews = signal<Interview[]>([]);
  readonly prepByEvent = signal<Record<string, InterviewPrep>>({});
  readonly greeting = DashboardComponent.greetingFor(new Date().getHours());

  get statCards() {
    const s = this.stats();
    return [
      { label: 'Upcoming', value: s.upcoming },
      { label: 'This Week', value: s.thisWeek },
      { label: 'Total Interviews', value: s.prepared },
    ];
  }

  constructor() {
    this.service.stats().subscribe((s) => this.stats.set(s));

    this.service.upcoming().subscribe({
      next: (items) => {
        this.interviews.set(items);
        this.loadCachedPrep(items);
      },
      error: () => this.interviews.set([]),
    });
  }

  private loadCachedPrep(interviews: Interview[]): void {
    const ids = interviews.map((i) => i.id).filter(Boolean);
    if (!ids.length) return;

    this.prepService.cached(ids).subscribe({
      next: (list) =>
        this.prepByEvent.set(Object.fromEntries(list.map((p) => [p.eventId, p]))),
      error: () => this.prepByEvent.set({}),
    });
  }

  private static greetingFor(hour: number): string {
    if (hour < 12) return 'Good morning';
    if (hour < 18) return 'Good afternoon';
    return 'Good evening';
  }
}