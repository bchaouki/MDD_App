import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Topic } from '../../core/models/topic';

@Injectable({ providedIn: 'root' })
export class TopicService {
  private readonly url = `${environment.apiUrl}/topics`;

  private readonly http = inject(HttpClient);

  getAll(): Observable<Topic[]> {
    return this.http.get<Topic[]>(this.url);
  }

  getSubscriptions(): Observable<Topic[]> {
    return this.http.get<Topic[]>(`${environment.apiUrl}/users/me/subscriptions`);
  }

  subscribe(topicId: number): Observable<void> {
    return this.http.post<void>(`${this.url}/${topicId}/subscribe`, null);
  }

  unsubscribe(topicId: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${topicId}/subscribe`);
  }
}
