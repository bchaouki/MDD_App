import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { Comment } from '../../../core/models/comment';

@Component({
  selector: 'app-comment-list',
  imports: [DatePipe],
  template: `
    @if (comments().length) {
      <ul class="comments">
        @for (comment of comments(); track comment.id) {
          <li class="comment">
            <span class="comment__author">{{ comment.authorUsername }}</span>
            <div class="comment__bubble">
              <p class="comment__content">{{ comment.content }}</p>
              <time class="comment__date">{{ comment.createdAt | date: 'short' }}</time>
            </div>
          </li>
        }
      </ul>
    } @else {
      <p class="comments__empty">Aucun commentaire pour le moment. Soyez le premier !</p>
    }
  `,
  styleUrl: './comment-list.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentList {
  readonly comments = input<Comment[]>([]);
}
