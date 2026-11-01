import { ChangeDetectionStrategy, Component, input } from '@angular/core';

/** Message d'erreur global d'un formulaire (ex. réponse de l'API). */
@Component({
  selector: 'app-form-error',
  template: `
    @if (message()) {
      <p class="form-error" role="alert">{{ message() }}</p>
    }
  `,
  styles: `
    .form-error {
      color: var(--mat-sys-error);
      text-align: center;
      margin: 0 0 16px;
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FormError {
  readonly message = input<string | null>(null);
}
