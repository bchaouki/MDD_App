import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { Topic } from '../../../core/models/topic';

/** Carte thème, réutilisée sur la page Thèmes (S'abonner) et la page Profil (Se désabonner). */
@Component({
  selector: 'app-topic-card',
  imports: [MatButton],
  template: `
    <article class="card">
      <h3 class="card__title">{{ topic().title }}</h3>
      <p class="card__body">{{ topic().description }}</p>
      <button
        matButton="filled"
        type="button"
        class="card__action"
        [disabled]="disabled()"
        (click)="action.emit(topic())"
      >
        {{ actionLabel() }}
      </button>
    </article>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TopicCard {
  readonly topic = input.required<Topic>();
  readonly actionLabel = input('');
  readonly disabled = input(false);
  readonly action = output<Topic>();
}
