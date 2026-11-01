import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Article,
  ArticleSummary,
  CreateArticleRequest,
  SortDirection,
} from '../../core/models/article';

@Injectable({ providedIn: 'root' })
export class ArticleService {
  private readonly url = `${environment.apiUrl}/articles`;

  private readonly http = inject(HttpClient);

  getFeed(sort: SortDirection = 'desc'): Observable<ArticleSummary[]> {
    return this.http.get<ArticleSummary[]>(`${environment.apiUrl}/feed`, {
      params: new HttpParams().set('sort', sort),
    });
  }

  getById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.url}/${id}`);
  }

  create(request: CreateArticleRequest): Observable<Article> {
    return this.http.post<Article>(this.url, request);
  }
}
