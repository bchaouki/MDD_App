import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatDivider } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RouterLink } from '@angular/router';
import { catchError, finalize, of, switchMap } from 'rxjs';
import { errorMessage } from '../../../core/models/api-error';
import { Topic } from '../../../core/models/topic';
import { UpdateUserRequest } from '../../../core/models/user';
import { SessionService } from '../../../core/services/session.service';
import { FormError } from '../../../shared/components/form-error/form-error';
import { TopicCard } from '../../../shared/components/topic-card/topic-card';
import {
  PASSWORD_HINT,
  strongPasswordValidator,
} from '../../../shared/validators/password.validator';
import { TopicService } from '../../topics/topic.service';
import { UserService } from '../user.service';

@Component({
  selector: 'app-profile',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInput,
    MatButton,
    MatIcon,
    MatDivider,
    MatProgressSpinner,
    TopicCard,
    FormError,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Profile {
  private readonly userService = inject(UserService);
  private readonly topicService = inject(TopicService);
  private readonly session = inject(SessionService);
  private readonly snackBar = inject(MatSnackBar);

  private readonly subscriptionsVersion = signal(0);

  protected readonly passwordHint = PASSWORD_HINT;
  readonly form = inject(NonNullableFormBuilder).group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    password: ['', strongPasswordValidator()],
  });
  readonly subscriptions = toSignal(
    toObservable(this.subscriptionsVersion).pipe(
      switchMap(() =>
        this.topicService.getSubscriptions().pipe(catchError(() => of([] as Topic[]))),
      ),
    ),
  );
  /** Thèmes dont le désabonnement est en cours d'envoi. */
  readonly pending = signal<ReadonlySet<number>>(new Set());
  readonly error = signal<string | null>(null);
  readonly saving = signal(false);

  constructor() {
    this.userService
      .getMe()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: ({ username, email }) => this.form.patchValue({ username, email }),
        error: (err: unknown) => this.error.set(errorMessage(err)),
      });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { username, email, password } = this.form.getRawValue();
    const request: UpdateUserRequest = password
      ? { username, email, password }
      : { username, email };
    this.saving.set(true);
    this.error.set(null);
    this.userService
      .update(request)
      .pipe(finalize(() => this.saving.set(false)))
      .subscribe({
        next: ({ user, token }) => {
          this.session.update(token, user);
          this.form.patchValue({ username: user.username, email: user.email, password: '' });
          this.form.controls.password.markAsUntouched();
          this.snackBar.open('Profil mis à jour', 'OK', { duration: 3000 });
        },
        error: (err: unknown) => this.error.set(errorMessage(err)),
      });
  }

  unsubscribe(topic: Topic): void {
    this.setPending(topic.id, true);
    this.topicService
      .unsubscribe(topic.id)
      .pipe(finalize(() => this.setPending(topic.id, false)))
      .subscribe({
        next: () => {
          this.snackBar.open(`Désabonné du thème ${topic.title}`, 'OK', { duration: 3000 });
          this.subscriptionsVersion.update((version) => version + 1);
        },
        error: (err: unknown) => this.snackBar.open(errorMessage(err), 'OK', { duration: 4000 }),
      });
  }

  logout(): void {
    this.session.signOut();
  }

  private setPending(id: number, pending: boolean): void {
    this.pending.update((ids) => {
      const next = new Set(ids);
      if (pending) {
        next.add(id);
      } else {
        next.delete(id);
      }
      return next;
    });
  }
}
