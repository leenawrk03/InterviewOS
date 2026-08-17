import { Component, Input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Interview } from '../../shared/models/interview.model';
import { InterviewPrep } from '../../shared/models/interview-prep.model';
import { InterviewPrepComponent } from './interview-prep.component';




@Component({
  selector: 'app-upcoming-interviews',
  standalone: true,
  imports: [DatePipe, InterviewPrepComponent],   // 2) add the prep component
  template: `
    <section class="wrap">

      <div class="section-label">
        YOUR SCHEDULE
      </div>

      <h2 class="serif heading">
        Upcoming interviews
      </h2>

      @if (interviews.length === 0) {

        <div class="empty">
          <div class="empty-icon">
            ○
          </div>

          <p class="strong">
            No upcoming interviews
          </p>

          <p class="muted sub">
            Your scheduled Google Calendar interviews
            will appear here.
          </p>
        </div>

      } @else {

        <div class="list">

          @for (it of interviews; track it.id) {

            <article class="interview-card">

              <div class="card-top">

                <div>
                  @if (it.title) {
                  
                    <h3>
                      {{ it.title }}
                    </h3>
                  }
                  <app-interview-prep
                    [eventId]="it.id"
                    [existing]="prepByEvent[it.id] ?? null" />
                </div>

                @if (it.startTime) {
                  <div class="date">
                    {{ it.startTime | date:'EEE, MMM d' }}
                  </div>
                }

              </div>


              @if (it.startTime) {

                <div class="info-row">

                  <span class="icon">◷</span>

                  <span>
                    {{ it.startTime | date:'h:mm a' }}

                    @if (it.endTime) {
                      -
                      {{ it.endTime | date:'h:mm a' }}
                    }
                  </span>

                </div>

              }


              @if (it.description) {

                <div class="info-row">

                  <span class="icon">▱</span>

                  <span>
                    {{ it.description }}
                  </span>

                </div>

              }


              @if (it.link) {

                <div class="actions">

                  <a
                    class="join-button"
                    [href]="it.link"
                    target="_blank"
                    rel="noopener noreferrer">

                    Open Calendar Event →
                    
                  </a>

                </div>

              }

            </article>

          }

        </div>

      }

    </section>
  `,

  styles: [`

    .wrap {
      margin-top: 56px;
    }

    .section-label {
      font-size: 13px;
      font-weight: 600;
      letter-spacing: 2px;
      color: #24211f;
      margin-bottom: 8px;
    }

    .heading {
      margin: 0;
      font-size: 36px;
      font-weight: 400;
    }

    .empty {
      margin-top: 24px;
      border: 1px dashed var(--border);
      border-radius: 24px;
      background: var(--card);
      padding: 60px 40px;
      text-align: center;
    }

    .empty-icon {
      font-size: 38px;
      margin-bottom: 20px;
    }

    .strong {
      margin: 0;
      font-size: 20px;
      font-weight: 500;
    }

    .sub {
      margin: 10px auto 0;
      max-width: 420px;
      font-size: 15px;
      line-height: 1.6;
    }

    .list {
      margin-top: 24px;
      display: grid;
      gap: 16px;
    }

    .interview-card {
      border: 1px solid var(--border);
      border-radius: 20px;
      background: var(--card);
      padding: 24px;
      transition: transform 0.15s ease;
    }

    .interview-card:hover {
      transform: translateY(-2px);
    }

    .card-top {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 20px;
      margin-bottom: 18px;
    }

    h3 {
      margin: 0;
      font-size: 22px;
      font-weight: 500;
    }

    .date {
      font-size: 14px;
      color: #77716d;
      white-space: nowrap;
    }

    .info-row {
      display: flex;
      gap: 10px;
      align-items: flex-start;
      margin-top: 12px;
      color: #716b67;
      font-size: 14px;
      line-height: 1.5;
    }

    .icon {
      width: 18px;
      flex-shrink: 0;
    }

    .actions {
      margin-top: 20px;
    }

    .join-button {
      display: inline-block;
      padding: 10px 16px;
      border-radius: 999px;
      background: #1d1b19;
      color: white;
      text-decoration: none;
      font-size: 14px;
    }

  `]
})
// 4) class body
export class UpcomingInterviewsComponent {

  @Input() interviews: Interview[] = [];

  @Input() prepByEvent: Record<string, InterviewPrep> = {};
}