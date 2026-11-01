import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { environment } from '../../../environments/environment';
import { TokenStorageService } from '../services/token-storage.service';

/** Ajoute `Authorization: Bearer <token>` aux appels de l'API. */
export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  const token = inject(TokenStorageService).get();
  if (token && request.url.startsWith(environment.apiUrl)) {
    request = request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(request);
};
