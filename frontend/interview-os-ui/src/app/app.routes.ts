// PATH: src/app/app.routes.ts

import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    canActivate: [guestGuard],
    loadComponent: () =>
      import('./features/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/dashboard.component').then((m) => m.DashboardComponent),
  },
  // NEW: AI preparation page
  {
    path: 'dashboard/interviews/:eventId/prep',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/dashboard/interview-prep-page.component').then(
        (m) => m.InterviewPrepPageComponent,
      ),
  },
  { path: '**', redirectTo: '' },
];