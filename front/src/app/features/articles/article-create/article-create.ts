import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { catchError, finalize, of } from 'rxjs';
import { errorMessage } from '../../../core/models/api-error';
import { Topic } from '../../../core/models/topic';
import { BackButton } from '../../../shared/components/back-button/back-button';
import { FormError } from '../../../shared/components/form-error/form-error';
import { notBlankValidator } from '../../../shared/validators/not-blank.validator';
import { TopicService } from '../../topics/topic.service';
import { ArticleService } from '../article.service';

@Component({
  selector: 'app-article-create',
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInput,
    MatSelectModule,
    MatButton,
    BackButton,
    FormError,
  ],
  templateUrl: './article-create.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArticleCreate {
  private readonly articleService = inject(ArticleService);
  private readonly snackBar = inject(MatSnackBar);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly topics = toSignal(
    inject(TopicService)
      .getAll()
      .pipe(catchError(() => of([] as Topic[]))),
    { initialValue: [] },
  );
  readonly form = this.fb.group({
    topicId: this.fb.control<number | null>(null, Validators.required),
    title: this.fb.nonNullable.control('', [notBlankValidator(), Validators.maxLength(255)]),
    content: this.fb.nonNullable.control('', [notBlankValidator(), Validators.maxLength(20000)]),
  });
  readonly error = signal<string | null>(null);
  readonly submitting = signal(false);

  submit(): void {
    const { topicId, title, content } = this.form.getRawValue();
    if (this.form.invalid || topicId === null) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.error.set(null);
    this.articleService
      .create({ topicId, title: title.trim(), content })
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          this.snackBar.open('Article publié', 'OK', { duration: 3000 });
          this.router.navigateByUrl('/feed');
        },
        error: (err: unknown) => this.error.set(errorMessage(err)),
      });
  }
}
