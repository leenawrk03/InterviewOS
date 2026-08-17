// PATH: src/app/features/dashboard/interview-prep-page.component.ts  (NEW FILE)
// Route: /dashboard/interviews/:eventId/prep

import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { InterviewPrepService } from '../../core/services/interview-prep.service';
import { InterviewPrep } from '../../shared/models/interview-prep.model';

@Component({
  selector: 'app-interview-prep-page',
  standalone: true,
  imports: [DatePipe, RouterLink],
  template: `
    <div class="page">
      <div class="main">
        <a class="back" routerLink="/dashboard">← Back to dashboard</a>

        @if (loading()) {
          <div class="state">
            <div class="spinner"></div>
            <h2 class="serif">Generating your preparation...</h2>
            <p class="muted">
              Analyzing your interview and creating personalized study material.
            </p>
          </div>
        } @else if (error()) {
          <div class="state">
            <h2 class="serif">Something went wrong</h2>
            <p class="muted">{{ error() }}</p>
            <button class="cta" (click)="generate()">Try again</button>
          </div>
        } @else if (!prep()) {
          <div class="state">
            <div class="emoji">🤖</div>
            <h2 class="serif">AI interview preparation</h2>
            <p class="muted">
              We haven't prepared this interview yet. Generate a personalized
              study plan with topics, questions, resources and last-minute tips.
            </p>
            <button class="cta" (click)="generate()">Generate AI Preparation</button>
          </div>
        } @else {
          <header class="head">
            <h1 class="serif">Your preparation</h1>
            <p class="muted">
              {{ prep()!.provider }} ·
              {{ prep()!.generatedAt | date: 'MMM d, y, h:mm a' }}
            </p>
            <p class="summary">{{ prep()!.analysis.summary }}</p>
            <button class="ghost" (click)="generate(true)">Regenerate</button>
          </header>

          <section class="block">
            <h2>🎯 Topics</h2>
            <div class="grid">
              @for (t of prep()!.analysis.topics; track t.name) {
                <div class="card">
                  <div class="card-top">
                    <strong>{{ t.name }}</strong>
                    <span class="pill" [class]="t.priority.toLowerCase()">
                      {{ t.priority }}
                    </span>
                  </div>
                  <p class="muted">{{ t.why }}</p>
                </div>
              }
            </div>
          </section>

          <section class="block">
            <h2>📚 Preparation plan</h2>
            <ol class="plan">
              @for (s of prep()!.analysis.preparationPlan; track s.order) {
                <li>
                  <strong>{{ s.title }}</strong>
                  @if (s.estimatedMinutes) {
                    <span class="mins">{{ s.estimatedMinutes }} min</span>
                  }
                  <p class="muted">{{ s.detail }}</p>
                </li>
              }
            </ol>
          </section>

          <section class="block">
            <h2>❓ Possible questions</h2>
            @for (q of prep()!.analysis.questions; track q.question) {
              <div class="card">
                <div class="card-top">
                  <strong>{{ q.question }}</strong>
                  <span class="pill">{{ q.category }}</span>
                </div>
                <p class="muted">{{ q.answerHint }}</p>
              </div>
            }
          </section>

          <section class="block">
            <h2>🔗 Recommended resources</h2>
            @for (r of prep()!.analysis.resources; track r.url) {
              <div class="card">
                <a [href]="r.url" target="_blank" rel="noopener noreferrer">
                  {{ r.title }}
                </a>
                <span class="pill">{{ r.type }}</span>
                <p class="muted">{{ r.why }}</p>
              </div>
            }
          </section>

          <section class="block">
            <h2>⚡ Last-minute tips</h2>
            <ul class="tips">
              @for (tip of prep()!.analysis.lastMinuteTips; track tip) {
                <li>{{ tip }}</li>
              }
            </ul>
          </section>
        }
      </div>
    </div>
  `,
  styles: [
    `
      .page { min-height: 100vh; }
      .main { max-width: 880px; margin: 0 auto; padding: 40px 24px 80px; }
      .back { font-size: 14px; text-decoration: none; color: #77716d; }
      .serif { font-weight: 400; }
      .muted { color: #77716d; line-height: 1.6; }

      .state {
        margin-top: 80px;
        text-align: center;
        border: 1px dashed var(--border, #e0dcd6);
        border-radius: 24px;
        padding: 60px 40px;
      }
      .emoji { font-size: 38px; margin-bottom: 12px; }

      .spinner {
        width: 34px; height: 34px; margin: 0 auto 18px;
        border: 3px solid #e6e2dc; border-top-color: #1d1b19;
        border-radius: 50%; animation: spin 0.9s linear infinite;
      }
      @keyframes spin { to { transform: rotate(360deg); } }

      .cta {
        margin-top: 20px; padding: 12px 22px; border: none;
        border-radius: 999px; background: #1d1b19; color: #fff;
        font-size: 14px; cursor: pointer;
      }
      .ghost {
        margin-top: 16px; padding: 8px 16px;
        border: 1px solid var(--border, #e0dcd6); border-radius: 999px;
        background: transparent; font-size: 13px; cursor: pointer;
      }

      .head { margin-top: 24px; }
      .head h1 { margin: 0; font-size: 36px; }
      .summary { margin-top: 14px; font-size: 16px; line-height: 1.7; }

      .block { margin-top: 44px; }
      .block h2 { font-size: 20px; font-weight: 500; margin: 0 0 16px; }

      .grid { display: grid; gap: 12px; grid-template-columns: repeat(2, 1fr); }
      @media (max-width: 640px) { .grid { grid-template-columns: 1fr; } }

      .card {
        border: 1px solid var(--border, #e0dcd6);
        border-radius: 16px;
        background: var(--card, #fff);
        padding: 18px;
        margin-bottom: 12px;
      }
      .card-top {
        display: flex; justify-content: space-between;
        align-items: flex-start; gap: 12px; margin-bottom: 8px;
      }
      .card p { margin: 6px 0 0; font-size: 14px; }
      .card a { font-weight: 500; color: #24211f; }

      .pill {
        font-size: 11px; letter-spacing: 1px; text-transform: uppercase;
        padding: 3px 10px; border-radius: 999px;
        background: #f2efea; white-space: nowrap;
      }
      .pill.high { background: #f6dcd6; }
      .pill.medium { background: #f4ecd2; }
      .pill.low { background: #e3ebe0; }

      .plan { padding-left: 20px; display: grid; gap: 14px; }
      .plan strong { font-weight: 500; }
      .mins { margin-left: 8px; font-size: 12px; color: #77716d; }
      .plan p { margin: 4px 0 0; font-size: 14px; }

      .tips { padding-left: 20px; display: grid; gap: 10px; font-size: 15px; }
    `,
  ],
})
export class InterviewPrepPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly prepService = inject(InterviewPrepService);

  readonly eventId = this.route.snapshot.paramMap.get('eventId') ?? '';
  readonly prep = signal<InterviewPrep | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  constructor() {
    // First try the cache — no AI call, no cost.
    this.prepService.cached([this.eventId]).subscribe({
      next: (list) => {
        this.prep.set(list.find((p) => p.eventId === this.eventId) ?? null);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  generate(force = false): void {
    this.loading.set(true);
    this.error.set(null);

    this.prepService.generate(this.eventId, force).subscribe({
      next: (p) => {
        this.prep.set(p);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set(e?.error?.message ?? 'Could not generate the preparation.');
        this.loading.set(false);
      },
    });
  }
}