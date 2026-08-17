import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { InterviewPrep } from '../../shared/models/interview-prep.model';

@Injectable({ providedIn: 'root' })
export class InterviewPrepService {

  private readonly http = inject(HttpClient);
  private readonly base = `${environment.apiBaseUrl}/api/interviews`;

  /** Stored analyses for the given events. No AI call, no cost. */
  cached(eventIds: string[]): Observable<InterviewPrep[]> {
    const params = new HttpParams({ fromObject: { eventIds } });
    return this.http.get<InterviewPrep[]>(`${this.base}/prep`, { params });
  }

  /** Generates on the server (or returns the stored one unless force). */
  generate(eventId: string, force = false): Observable<InterviewPrep> {
    return this.http.post<InterviewPrep>(
      `${this.base}/${encodeURIComponent(eventId)}/prep?force=${force}`,
      {}
    );
  }
}
