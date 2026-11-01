import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { User } from '../models/user';
import { SessionService } from './session.service';
import { TokenStorageService } from './token-storage.service';

describe('SessionService', () => {
  const user: User = {
    id: 1,
    username: 'alice',
    email: 'alice@mail.com',
    createdAt: '2026-10-18T10:00:00',
  };
  let http: HttpTestingController;

  function setup(): SessionService {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    http = TestBed.inject(HttpTestingController);
    return TestBed.inject(SessionService);
  }

  beforeEach(() => localStorage.clear());
  afterEach(() => {
    http.verify();
    localStorage.clear();
  });

  it('is logged out without a stored token', () => {
    const session = setup();
    expect(session.isLogged()).toBe(false);
  });

  it('is logged in when a token was stored during a previous visit', () => {
    localStorage.setItem('mdd_token', 'stored');
    const session = setup();
    expect(session.isLogged()).toBe(true);
  });

  it('logIn stores the token and loads the current user', () => {
    const session = setup();
    let loaded: User | undefined;
    session.logIn('token').subscribe((u) => (loaded = u));

    http.expectOne('/api/users/me').flush(user);

    expect(TestBed.inject(TokenStorageService).get()).toBe('token');
    expect(session.isLogged()).toBe(true);
    expect(loaded).toEqual(user);
  });

  it('restore reloads the user when a token exists', () => {
    localStorage.setItem('mdd_token', 'stored');
    const session = setup();

    session.restore().subscribe();
    http.expectOne('/api/users/me').flush(user);

    expect(session.user()).toEqual(user);
  });

  it('restore does nothing without a token', () => {
    const session = setup();
    let result: User | null | undefined;
    session.restore().subscribe((u) => (result = u));
    http.expectNone('/api/users/me');
    expect(result).toBeNull();
  });

  it('logOut clears the session and redirects', () => {
    localStorage.setItem('mdd_token', 'stored');
    const session = setup();
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigateByUrl');

    session.logOut('/login');

    expect(localStorage.getItem('mdd_token')).toBeNull();
    expect(session.isLogged()).toBe(false);
    expect(navigate).toHaveBeenCalledWith('/login');
  });
});
