import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/user';
import { SessionService } from '../../../core/services/session.service';
import { AuthService } from '../auth.service';
import { Register } from './register';

describe('Register', () => {
  let fixture: ComponentFixture<Register>;
  let component: Register;
  const authService = { register: vi.fn() };
  const session = { logIn: vi.fn(), isLogged: signal(false) };
  const valid = { username: 'alice', email: 'alice@mail.com', password: 'Passw0rd!' };

  beforeEach(async () => {
    vi.clearAllMocks();
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        { provide: SessionService, useValue: session },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('does not call the API when the password is too weak', () => {
    component.form.setValue({ ...valid, password: 'password' });

    component.submit();

    expect(authService.register).not.toHaveBeenCalled();
  });

  it('registers, logs in and redirects to the feed', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    authService.register.mockReturnValue(of({ token: 'token' }));
    session.logIn.mockReturnValue(of({} as User));

    component.form.setValue(valid);
    component.submit();

    expect(authService.register).toHaveBeenCalledWith(valid);
    expect(session.logIn).toHaveBeenCalledWith('token');
    expect(navigate).toHaveBeenCalledWith('/feed');
  });

  it('displays the API error when the e-mail is already used', async () => {
    authService.register.mockReturnValue(
      throwError(
        () => new HttpErrorResponse({ status: 400, error: { message: 'E-mail déjà utilisé' } }),
      ),
    );

    component.form.setValue(valid);
    component.submit();
    await fixture.whenStable();

    expect(fixture.nativeElement.querySelector('.form-error')?.textContent).toContain(
      'E-mail déjà utilisé',
    );
    expect(component.submitting()).toBe(false);
  });
});
