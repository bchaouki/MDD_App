import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Injector, inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SessionService } from '../services/session.service';

const SNACKBAR_DURATION = 4000;

/**
 * 401 hors authentification (jeton expiré ou invalide) : déconnexion et redirection
 * vers /login. Erreurs réseau et serveur : notification. L'erreur est toujours propagée.
 */
export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  // SessionService dépend de HttpClient : on le résout seulement en cas de besoin.
  const injector = inject(Injector);
  const snackBar = inject(MatSnackBar);

  return next(request).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 401 && !request.url.startsWith(`${environment.apiUrl}/auth/`)) {
          injector.get(SessionService).logOut('/login');
          snackBar.open('Votre session a expiré, veuillez vous reconnecter', 'OK', {
            duration: SNACKBAR_DURATION,
          });
        } else if (error.status === 0) {
          snackBar.open('Le serveur est injoignable', 'OK', { duration: SNACKBAR_DURATION });
        } else if (error.status >= 500) {
          snackBar.open('Une erreur serveur est survenue', 'OK', { duration: SNACKBAR_DURATION });
        }
      }
      return throwError(() => error);
    }),
  );
};
