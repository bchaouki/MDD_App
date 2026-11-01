import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/user';
import { SessionService } from '../../../core/services/session.service';
import { AuthService } from '../auth.service';
import { Login } from './login';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;
  const authService = { login: vi.fn() };
  const session = { logIn: vi.fn(), isLogged: signal(false) };

  beforeEach(async () => {
    vi.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: SessionService, useValue: session },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('does not call the API when the form is invalid', () => {
    component.submit();
    expect(authService.login).not.toHaveBeenCalled();
  });

  it('logs in and redirects to the feed', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    authService.login.mockReturnValue(of({ token: 'token' }));
    session.logIn.mockReturnValue(of({} as User));

    component.form.setValue({ identifier: 'alice', password: 'Passw0rd!' });
    component.submit();

    expect(authService.login).toHaveBeenCalledWith({ identifier: 'alice', password: 'Passw0rd!' });
    expect(session.logIn).toHaveBeenCalledWith('token');
    expect(navigate).toHaveBeenCalledWith('/feed');
  });

  it('displays the API error', async () => {
    authService.login.mockReturnValue(
      throwError(
        () => new HttpErrorResponse({ status: 401, error: { message: 'Identifiants invalides' } }),
      ),
    );

    component.form.setValue({ identifier: 'alice', password: 'wrong' });
    component.submit();
    await fixture.whenStable();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('.form-error')?.textContent).toContain('Identifiants invalides');
    expect(component.submitting()).toBe(false);
  });
});
