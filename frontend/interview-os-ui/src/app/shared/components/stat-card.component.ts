import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  template: `
    <div class="card stat">
      <p class="label">{{ label }}</p>
      <p class="serif value">{{ value }}</p>
    </div>
  `,
  styles: [
    `
      .stat { padding: 20px; }
      .label {
        margin: 0; font-size: 12px; font-weight: 500; text-transform: uppercase;
        letter-spacing: .12em; color: var(--muted-foreground);
      }
      .value { margin: 12px 0 0; font-size: 36px; }
    `,
  ],
})
export class StatCardComponent {
  @Input({ required: true }) label!: string;
  @Input({ required: true }) value!: number;
}
