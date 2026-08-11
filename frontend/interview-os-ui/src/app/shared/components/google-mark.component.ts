import { Component } from '@angular/core';

@Component({
  selector: 'app-google-mark',
  standalone: true,
  template: `
    <svg width="18" height="18" viewBox="0 0 18 18" aria-hidden="true">
      <path fill="#FFC107" d="M17.6 7.4H9.2v3.5h4.8A5 5 0 0 1 4 9a5 5 0 0 1 8.3-3.7l2.5-2.5A9 9 0 1 0 18 9c0-.6 0-1.1-.4-1.6Z" />
      <path fill="#FF3D00" d="M1 4.9 4 7a5 5 0 0 1 8.3-1.7l2.5-2.5A9 9 0 0 0 1 4.9Z" />
      <path fill="#4CAF50" d="M9 18a9 9 0 0 0 6.1-2.4l-2.8-2.4A5 5 0 0 1 4.3 11L1.2 13.4A9 9 0 0 0 9 18Z" />
      <path fill="#1976D2" d="M17.6 7.4H9.2v3.5h4.8a5 5 0 0 1-1.7 2.3l2.8 2.4A9 9 0 0 0 18 9c0-.6 0-1.1-.4-1.6Z" />
    </svg>
  `,
})
export class GoogleMarkComponent {}
