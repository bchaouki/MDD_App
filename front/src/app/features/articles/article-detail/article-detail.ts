import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  numberAttribute,
  signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { EMPTY, catchError, of, switchMap } from 'rxjs';
import { errorMessage } from '../../../core/models/api-error';
import { Comment } from '../../../core/models/comment';
import { BackButton } from '../../../shared/components/back-button/back-button';
import { ArticleService } from '../article.service';
import { CommentForm } from '../comment-form/comment-form';
import { CommentList } from '../comment-list/comment-list';
import { CommentService } from '../comment.service';

@Component({
  selector: 'app-article-detail',
  imports: [
    DatePipe,
    RouterLink,
    MatButton,
    MatDivider,
    MatProgressSpinner,
    BackButton,
    CommentList,
    CommentForm,
  ],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArticleDetail {
  private readonly articleService = inject(ArticleService);
  private readonly commentService = inject(CommentService);
  private readonly snackBar = inject(MatSnackBar);

  /** Paramètre `:id` de la route (via withComponentInputBinding). */
  readonly id = input.required({ transform: numberAttribute });

  private readonly commentsVersion = signal(0);

  readonly error = signal<string | null>(null);
  /** true si l'API a répondu 404 (article inexistant). */
  readonly notFound = signal(false);
  readonly sendingComment = signal(false);

  readonly article = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) =>
        this.articleService.getById(id).pipe(
          catchError((err: unknown) => {
            this.error.set(
              err instanceof HttpErrorResponse && err.status === 404
                ? "Cet article n'existe pas ou a été supprimé."
                : errorMessage(err, 'Impossible de charger cet article'),
            );
            this.notFound.set(err instanceof HttpErrorResponse && err.status === 404);
            return EMPTY;
          }),
        ),
      ),
    ),
  );

  readonly comments = toSignal(
    toObservable(computed(() => ({ id: this.id(), version: this.commentsVersion() }))).pipe(
      switchMap(({ id }) =>
        this.commentService.getByArticle(id).pipe(catchError(() => of([] as Comment[]))),
      ),
    ),
    { initialValue: [] },
  );

  addComment(content: string, form: CommentForm): void {
    this.sendingComment.set(true);
    this.commentService.create(this.id(), content).subscribe({
      next: () => {
        this.sendingComment.set(false);
        form.reset();
        this.commentsVersion.update((version) => version + 1);
      },
      error: (err: unknown) => {
        this.sendingComment.set(false);
        this.snackBar.open(errorMessage(err), 'OK', { duration: 4000 });
      },
    });
  }
}
