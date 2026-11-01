import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SessionService } from '../services/session.service';
import { TokenStorageService } from '../services/token-storage.service';
import { errorInterceptor } from './error.interceptor';
import { jwtInterceptor } from './jwt.interceptor';

describe('HTTP interceptors', () => {
  let http: HttpClient;
  let controller: HttpTestingController;
  let token: string | null;
  const session = { logOut: vi.fn() };
  const snackBar = { open: vi.fn() };

  beforeEach(() => {
    token = 'abc';
    vi.clearAllMocks();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([jwtInterceptor, errorInterceptor])),
        provideHttpClientTesting(),
        { provide: TokenStorageService, useValue: { get: () => token } },
        { provide: SessionService, useValue: session },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('adds the bearer token to API calls', () => {
    http.get('/api/feed').subscribe();
    expect(controller.expectOne('/api/feed').request.headers.get('Authorization')).toBe(
      'Bearer abc',
    );
  });

  it('does not add a header without token', () => {
    token = null;
    http.get('/api/feed').subscribe();
    expect(controller.expectOne('/api/feed').request.headers.has('Authorization')).toBe(false);
  });

  it('does not leak the token to other hosts', () => {
    http.get('https://example.com/data').subscribe();
    expect(
      controller.expectOne('https://example.com/data').request.headers.has('Authorization'),
    ).toBe(false);
  });

  it('logs out on 401 from a protected route', () => {
    http.get('/api/feed').subscribe({ error: () => undefined });
    controller.expectOne('/api/feed').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(session.logOut).toHaveBeenCalledWith('/login');
  });

  it('keeps the session on 401 from the login route', () => {
    let status = 0;
    http.post('/api/auth/login', {}).subscribe({ error: (e) => (status = e.status) });
    controller.expectOne('/api/auth/login').flush({}, { status: 401, statusText: 'Unauthorized' });
    expect(session.logOut).not.toHaveBeenCalled();
    expect(status).toBe(401);
  });

  it('notifies server errors', () => {
    http.get('/api/feed').subscribe({ error: () => undefined });
    controller.expectOne('/api/feed').flush({}, { status: 500, statusText: 'Server Error' });
    expect(snackBar.open).toHaveBeenCalled();
  });
});
