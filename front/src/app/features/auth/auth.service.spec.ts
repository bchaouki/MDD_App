import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('registers a new user and returns the token', () => {
    let token: string | undefined;
    const request = { username: 'alice', email: 'alice@mail.com', password: 'Passw0rd!' };

    service.register(request).subscribe((response) => (token = response.token));
    const req = controller.expectOne('/api/auth/register');
    req.flush({ token: 'jwt' });

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    expect(token).toBe('jwt');
  });

  it('logs in with an identifier and a password', () => {
    service.login({ identifier: 'alice', password: 'Passw0rd!' }).subscribe();

    const req = controller.expectOne('/api/auth/login');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ identifier: 'alice', password: 'Passw0rd!' });
  });
});
