import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { SessionService } from '../services/session.service';

/** Réserve une route aux utilisateurs connectés. */
export const authGuard: CanActivateFn = () =>
  inject(SessionService).isLogged() || inject(Router).createUrlTree(['/login']);

/** Accueil, connexion, inscription : redirige vers le fil si déjà connecté. */
export const unauthGuard: CanActivateFn = () =>
  !inject(SessionService).isLogged() || inject(Router).createUrlTree(['/feed']);
