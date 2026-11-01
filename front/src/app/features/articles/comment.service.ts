import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Comment } from '../../core/models/comment';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly http = inject(HttpClient);

  getByArticle(articleId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(this.url(articleId));
  }

  create(articleId: number, content: string): Observable<Comment> {
    return this.http.post<Comment>(this.url(articleId), { content });
  }

  private url(articleId: number): string {
    return `${environment.apiUrl}/articles/${articleId}/comments`;
  }
}
