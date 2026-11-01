import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, MatButton],
  template: `
    <section class="landing">
      <img class="landing__logo" src="logo_p6.png" alt="MDD - Monde de Dév" />
      <div class="landing__actions">
        <a matButton="filled" routerLink="/login">Se connecter</a>
        <a matButton="filled" routerLink="/register">S'inscrire</a>
      </div>
    </section>
  `,
  styleUrl: './landing.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Landing {}
