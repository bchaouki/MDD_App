import { HttpErrorResponse } from '@angular/common/http';

/** Format d'erreur unique renvoyé par l'API. */
export interface ApiError {
  status: number;
  error: string;
  message: string;
  fields?: Record<string, string>;
  timestamp: string;
}

/** Message lisible à afficher pour une erreur HTTP. */
export function errorMessage(error: unknown, fallback = 'Une erreur est survenue'): string {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return 'Le serveur est injoignable';
    }
    const body = error.error as Partial<ApiError> | null;
    if (body && typeof body.message === 'string') {
      return body.message;
    }
  }
  return fallback;
}
