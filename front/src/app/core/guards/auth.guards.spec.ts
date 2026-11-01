import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
  UrlTree,
  provideRouter,
} from '@angular/router';
import { SessionService } from '../services/session.service';
import { authGuard, unauthGuard } from './auth.guards';

describe('Guards', () => {
  let logged: boolean;
  let router: Router;

  const run = (guard: CanActivateFn) =>
    TestBed.runInInjectionContext(() =>
      guard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot),
    );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: SessionService, useValue: { isLogged: () => logged } },
      ],
    });
    router = TestBed.inject(Router);
  });

  it('authGuard lets logged users through', () => {
    logged = true;
    expect(run(authGuard)).toBe(true);
  });

  it('authGuard redirects anonymous users to /login', () => {
    logged = false;
    expect(router.serializeUrl(run(authGuard) as UrlTree)).toBe('/login');
  });

  it('unauthGuard lets anonymous users through', () => {
    logged = false;
    expect(run(unauthGuard)).toBe(true);
  });

  it('unauthGuard redirects logged users to /feed', () => {
    logged = true;
    expect(router.serializeUrl(run(unauthGuard) as UrlTree)).toBe('/feed');
  });
});
