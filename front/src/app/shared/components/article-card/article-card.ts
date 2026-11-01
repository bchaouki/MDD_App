import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ArticleSummary } from '../../../core/models/article';

@Component({
  selector: 'app-article-card',
  imports: [RouterLink, DatePipe],
  template: `
    <a class="card" [routerLink]="['/articles', article().id]">
      <h3 class="card__title">{{ article().title }}</h3>
      <div class="card__meta">
        <span>{{ article().createdAt | date: 'longDate' }}</span>
        <span>{{ article().authorUsername }}</span>
        <span>{{ article().topicTitle }}</span>
      </div>
      <p class="card__body">{{ article().excerpt }}</p>
    </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArticleCard {
  readonly article = input.required<ArticleSummary>();
}
