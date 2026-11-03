import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(UserService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('loads the current user', () => {
    service.getMe().subscribe();

    expect(controller.expectOne('/api/users/me').request.method).toBe('GET');
  });

  it('updates the current user', () => {
    service.update({ username: 'bob', email: 'bob@mail.com' }).subscribe();

    const req = controller.expectOne('/api/users/me');

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ username: 'bob', email: 'bob@mail.com' });
  });
});
