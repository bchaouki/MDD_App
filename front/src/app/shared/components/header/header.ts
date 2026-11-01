import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { map } from 'rxjs';
import { SessionService } from '../../../core/services/session.service';
import { NAV_LINKS } from '../../nav-links';

/** Barre de navigation desktop ; sur mobile, bouton burger qui ouvre le menu latéral. */
@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, MatIconButton, MatIcon],
  templateUrl: './header.html',
  styleUrl: './header.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Header {
  readonly menuToggle = output<void>();

  protected readonly navLinks = NAV_LINKS;
  protected readonly isLogged = inject(SessionService).isLogged;
  protected readonly isHandset = toSignal(
    inject(BreakpointObserver)
      .observe([Breakpoints.Handset, Breakpoints.TabletPortrait])
      .pipe(map((state) => state.matches)),
    { initialValue: false },
  );
}
