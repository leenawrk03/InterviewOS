import { Component, Input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Interview } from '../../shared/models/interview.model';

@Component({
  selector: 'app-upcoming-interviews',
  standalone: true,
  imports: [DatePipe],
  template: `
    <section class="wrap">
      <h2 class="serif heading">Upcoming interviews</h2>

      @if (items.length === 0) {
        <div class="empty">
          <p class="strong">No interviews found</p>
          <p class="muted sub">
            Connect Google Calendar to pull your scheduled interviews in automatically.
          </p>
          <button class="btn btn-ghost cta" disabled>Connect Google Calendar — coming soon</button>
        </div>
      } @else {
        <ul class="list">
          @for (it of items; track it.id) {
            <li class="card row">
              <div>
                <p class="strong">{{ it.role }}</p>
                <p class="muted sub2">{{ it.company }} · {{ it.stage }}</p>
              </div>
              <span class="muted sub2">{{ it.startsAt | date: 'medium' }}</span>
            </li>
          }
        </ul>
      }
    </section>
  `,
  styles: [
    `
      .wrap { margin-top: 40px; }
      .heading { margin: 0; font-size: 24px; }
      .empty {
        margin-top: 16px; border: 1px dashed var(--border); border-radius: 24px;
        background: var(--card); padding: 40px; text-align: center;
      }
      .strong { margin: 0; font-weight: 500; }
      .sub { margin: 8px auto 0; max-width: 22rem; font-size: 14px; }
      .sub2 { margin: 4px 0 0; font-size: 14px; }
      .cta { margin-top: 24px; padding: 10px 20px; font-size: 14px; }
      .list { list-style: none; margin: 16px 0 0; padding: 0; display: grid; gap: 12px; }
      .row { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; }
    `,
  ],
})
export class UpcomingInterviewsComponent {
  @Input() items: Interview[] = [];
}
