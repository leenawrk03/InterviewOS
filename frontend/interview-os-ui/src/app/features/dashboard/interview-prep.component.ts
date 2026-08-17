// PATH: src/app/features/dashboard/interview-prep.component.ts
// The small button that sits inside each interview card.
// Label flips between "Generate" and "View" based on cached prep.

import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { InterviewPrep } from '../../shared/models/interview-prep.model';

@Component({
  selector: 'app-interview-prep',
  standalone: true,
  imports: [RouterLink],
  template: `
    <a
      class="prep-link"
      [class.ready]="!!existing"
      [routerLink]="['/dashboard/interviews', eventId, 'prep']"
    >
      @if (existing) {
        <span>View AI Preparation</span>
      } @else {
        <span>Generate AI Preparation</span>
      }
    </a>
  `,
  styles: [
    `
      .prep-link {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        margin-top: 10px;
        padding: 7px 14px;
        border: 1px solid var(--border, #e0dcd6);
        border-radius: 999px;
        font-size: 13px;
        text-decoration: none;
        color: #24211f;
        background: transparent;
        transition: background 0.15s ease;
      }
      .prep-link:hover {
        background: #f2efea;
      }
      .prep-link.ready {
        background: #1d1b19;
        border-color: #1d1b19;
        color: #fff;
      }
      .prep-link.ready:hover {
        opacity: 0.9;
      }
    `,
  ],
})
export class InterviewPrepComponent {
  @Input() eventId = '';
  @Input() existing: InterviewPrep | null = null;
}