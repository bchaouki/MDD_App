import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, of, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user';
import { TokenStorageService } from './token-storage.service';

/** État d'authentification de l'application. */
@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);

  private readonly loggedState = signal(this.tokenStorage.get() !== null);
  private readonly userState = signal<User | null>(null);

  readonly isLogged = this.loggedState.asReadonly();
  readonly user = this.userState.asReadonly();

  /** Enregistre le jeton puis charge l'utilisateur courant. */
  logIn(token: string): Observable<User> {
    this.tokenStorage.set(token);
    this.loggedState.set(true);
    return this.loadUser();
  }

  /** Après modification du profil : l'API renvoie un nouveau jeton. */
  update(token: string, user: User): void {
    this.tokenStorage.set(token);
    this.userState.set(user);
  }

  /** Au démarrage : restaure l'utilisateur si un jeton est présent. */
  restore(): Observable<User | null> {
    if (!this.isLogged()) {
      return of(null);
    }
    return this.loadUser().pipe(catchError(() => of(null)));
  }

  /**
   * Déconnexion demandée par l'utilisateur : le serveur révoque d'abord le jeton
   * (il ne pourra plus être réutilisé), puis la session locale est effacée,
   * même si l'appel échoue.
   */
  signOut(redirectTo = '/'): void {
    this.http
      .post<void>(`${environment.apiUrl}/auth/logout`, null)
      .pipe(
        catchError(() => of(null)),
        finalize(() => this.logOut(redirectTo)),
      )
      .subscribe();
  }

  /** Efface la session locale (sans appel API, ex. après une 401). */
  logOut(redirectTo = '/'): void {
    this.tokenStorage.clear();
    this.userState.set(null);
    this.loggedState.set(false);
    this.router.navigateByUrl(redirectTo);
  }

  private loadUser(): Observable<User> {
    return this.http
      .get<User>(`${environment.apiUrl}/users/me`)
      .pipe(tap((user) => this.userState.set(user)));
  }
}
