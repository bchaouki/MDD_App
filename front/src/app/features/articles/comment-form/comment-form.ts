import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { notBlankValidator } from '../../../shared/validators/not-blank.validator';

@Component({
  selector: 'app-comment-form',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInput, MatIconButton, MatIcon],
  template: `
    <form class="comment-form" [formGroup]="form" (ngSubmit)="submit()">
      <mat-form-field appearance="outline" class="comment-form__field">
        <mat-label>Écrivez ici votre commentaire</mat-label>
        <textarea matInput formControlName="content" rows="3" maxlength="2000"></textarea>
      </mat-form-field>
      <button
        matIconButton
        type="submit"
        class="comment-form__send"
        [disabled]="form.invalid || sending()"
        aria-label="Envoyer le commentaire"
      >
        <mat-icon>send</mat-icon>
      </button>
    </form>
  `,
  styleUrl: './comment-form.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CommentForm {
  readonly sending = input(false);
  readonly send = output<string>();

  readonly form = inject(NonNullableFormBuilder).group({
    content: ['', [notBlankValidator(), Validators.maxLength(2000)]],
  });

  submit(): void {
    if (this.form.invalid || this.sending()) {
      return;
    }
    this.send.emit(this.form.getRawValue().content.trim());
  }

  reset(): void {
    this.form.reset();
  }
}
