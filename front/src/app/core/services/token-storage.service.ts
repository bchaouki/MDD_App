import { Injectable } from '@angular/core';

const TOKEN_KEY = 'mdd_token';

/**
 * Persistance du jeton JWT. localStorage permet de rester connecté
 * entre deux visites (le jeton expire côté serveur après 24 h).
 */
@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  get(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  set(token: string): void {
    localStorage.setItem(TOKEN_KEY, token);
  }

  clear(): void {
    localStorage.removeItem(TOKEN_KEY);
  }
}
