import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, finalize, of, switchMap } from 'rxjs';
import { errorMessage } from '../../../core/models/api-error';
import { Topic } from '../../../core/models/topic';
import { FormError } from '../../../shared/components/form-error/form-error';
import { TopicCard } from '../../../shared/components/topic-card/topic-card';
import { TopicService } from '../topic.service';

@Component({
  selector: 'app-topic-list',
  imports: [MatProgressSpinner, TopicCard, FormError],
  template: `
    <section class="page">
      <app-form-error [message]="error()" />

      @if (topics(); as topics) {
        @if (topics.length) {
          <div class="card-grid">
            @for (topic of topics; track topic.id) {
              <app-topic-card
                [topic]="topic"
                [actionLabel]="topic.subscribed ? 'Déjà abonné' : 'S\\'abonner'"
                [disabled]="topic.subscribed || pending().has(topic.id)"
                (action)="subscribe($event)"
              />
            }
          </div>
        } @else {
          <p class="empty-state">Aucun thème disponible pour le moment.</p>
        }
      } @else {
        <mat-spinner class="spinner" diameter="40" />
      }
    </section>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicList {
  private readonly topicService = inject(TopicService);
  private readonly snackBar = inject(MatSnackBar);

  private readonly version = signal(0);

  /** Thèmes dont l'abonnement est en cours d'envoi. */
  readonly pending = signal<ReadonlySet<number>>(new Set());
  readonly error = signal<string | null>(null);
  readonly topics = toSignal(
    toObservable(this.version).pipe(
      switchMap(() =>
        this.topicService.getAll().pipe(
          catchError((err: unknown) => {
            this.error.set(errorMessage(err));
            return of([] as Topic[]);
          }),
        ),
      ),
    ),
  );

  subscribe(topic: Topic): void {
    this.setPending(topic.id, true);
    this.topicService
      .subscribe(topic.id)
      .pipe(finalize(() => this.setPending(topic.id, false)))
      .subscribe({
        next: () => {
          this.snackBar.open(`Abonné au thème ${topic.title}`, 'OK', { duration: 3000 });
          this.version.update((version) => version + 1);
        },
        error: (err: unknown) => this.snackBar.open(errorMessage(err), 'OK', { duration: 4000 }),
      });
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
