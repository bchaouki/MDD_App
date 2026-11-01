import { HttpErrorResponse } from '@angular/common/http';
import { errorMessage } from './api-error';

describe('errorMessage', () => {
  it('uses the API message', () => {
    const error = new HttpErrorResponse({
      status: 409,
      error: { message: 'Cet e-mail est déjà utilisé' },
    });
    expect(errorMessage(error)).toBe('Cet e-mail est déjà utilisé');
  });

  it('handles network errors', () => {
    expect(errorMessage(new HttpErrorResponse({ status: 0 }))).toBe('Le serveur est injoignable');
  });

  it('falls back to the default message', () => {
    expect(errorMessage(new Error('boom'), 'fallback')).toBe('fallback');
  });
});
