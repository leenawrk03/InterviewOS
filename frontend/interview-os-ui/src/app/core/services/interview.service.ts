import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { DashboardStats, EMPTY_STATS, Interview } from '../../shared/models/interview.model';

@Injectable({ providedIn: 'root' })
export class InterviewService {
  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/interviews`;

  upcoming(): Observable<Interview[]> {
    return this.http.get<Interview[]>(`${this.base}/upcoming`).pipe(catchError(() => of([])));
  }

  stats(): Observable<DashboardStats> {
    return this.http
      .get<DashboardStats>(`${this.base}/stats`)
      .pipe(catchError(() => of(EMPTY_STATS)));
  }
}
