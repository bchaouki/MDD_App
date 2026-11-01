import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { RouterLink } from '@angular/router';
import { catchError, of, switchMap } from 'rxjs';
import { errorMessage } from '../../../core/models/api-error';
import { ArticleSummary, SortDirection } from '../../../core/models/article';
import { ArticleCard } from '../../../shared/components/article-card/article-card';
import { FormError } from '../../../shared/components/form-error/form-error';
import { ArticleService } from '../article.service';

@Component({
  selector: 'app-feed',
  imports: [RouterLink, MatButton, MatIcon, MatProgressSpinner, ArticleCard, FormError],
  templateUrl: './feed.html',
  styleUrl: './feed.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Feed {
  private readonly articleService = inject(ArticleService);

  readonly sort = signal<SortDirection>('desc');
  readonly error = signal<string | null>(null);
  /** `undefined` tant que le premier chargement n'est pas terminé. */
  readonly articles = toSignal(
    toObservable(this.sort).pipe(
      switchMap((sort) =>
        this.articleService.getFeed(sort).pipe(
          catchError((err: unknown) => {
            this.error.set(errorMessage(err));
            return of([] as ArticleSummary[]);
          }),
        ),
      ),
    ),
  );

  toggleSort(): void {
    this.error.set(null);
    this.sort.update((sort) => (sort === 'desc' ? 'asc' : 'desc'));
  }
}
