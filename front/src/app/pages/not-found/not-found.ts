import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  imports: [RouterLink, MatButton],
  template: `
    <section class="page empty-state">
      <h1>Page introuvable</h1>
      <p>La page que vous cherchez n'existe pas.</p>
      <a matButton="filled" routerLink="/">Retour à l'accueil</a>
    </section>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotFound {}
