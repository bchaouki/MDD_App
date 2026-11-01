import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-back-button',
  imports: [RouterLink, MatIconButton, MatIcon],
  template: `
    <a matIconButton [routerLink]="link()" aria-label="Retour">
      <mat-icon>arrow_back</mat-icon>
    </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BackButton {
  readonly link = input('/');
}
