import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Topic } from '../../../core/models/topic';
import { User } from '../../../core/models/user';
import { SessionService } from '../../../core/services/session.service';
import { TopicService } from '../../topics/topic.service';
import { UserService } from '../user.service';
import { Profile } from './profile';

const USER: User = { id: 1, username: 'alice', email: 'alice@mail.com', createdAt: '2026-10-01' };
const TOPIC: Topic = { id: 2, title: 'Java', description: 'JVM', subscribed: true };

describe('Profile', () => {
  const userService = { getMe: vi.fn(), update: vi.fn() };
  const topicService = { getSubscriptions: vi.fn(), unsubscribe: vi.fn() };
  const session = { update: vi.fn(), signOut: vi.fn(), isLogged: signal(true) };
  const snackBar = { open: vi.fn() };

  async function render(): Promise<ComponentFixture<Profile>> {
    await TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userService },
        { provide: TopicService, useValue: topicService },
        { provide: SessionService, useValue: session },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(Profile);
    await fixture.whenStable();
    return fixture;
  }

  beforeEach(() => {
    vi.clearAllMocks();
    userService.getMe.mockReturnValue(of(USER));
    topicService.getSubscriptions.mockReturnValue(of([TOPIC]));
  });

  it('prefills the form and lists the subscriptions', async () => {
    const fixture = await render();

    expect(fixture.componentInstance.form.getRawValue()).toEqual({
      username: 'alice',
      email: 'alice@mail.com',
      password: '',
    });
    expect(fixture.nativeElement.querySelectorAll('app-topic-card').length).toBe(1);
  });

  it('shows the error when the profile cannot be loaded', async () => {
    userService.getMe.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 0 })));

    const fixture = await render();

    expect(fixture.componentInstance.error()).toBe('Le serveur est injoignable');
  });

  it('saves without the password when it is left empty', async () => {
    const fixture = await render();
    userService.update.mockReturnValue(of({ user: { ...USER, username: 'bob' }, token: 'new' }));

    fixture.componentInstance.form.patchValue({ username: 'bob' });
    fixture.componentInstance.save();

    expect(userService.update).toHaveBeenCalledWith({ username: 'bob', email: 'alice@mail.com' });
    expect(session.update).toHaveBeenCalledWith('new', { ...USER, username: 'bob' });
    expect(snackBar.open).toHaveBeenCalledWith('Profil mis à jour', 'OK', { duration: 3000 });
  });

  it('sends the new password when one is typed', async () => {
    const fixture = await render();
    userService.update.mockReturnValue(of({ user: USER, token: 'new' }));

    fixture.componentInstance.form.patchValue({ password: 'N3wPassw0rd!' });
    fixture.componentInstance.save();

    expect(userService.update).toHaveBeenCalledWith({
      username: 'alice',
      email: 'alice@mail.com',
      password: 'N3wPassw0rd!',
    });
    expect(fixture.componentInstance.form.getRawValue().password).toBe('');
  });

  it('does not save an invalid form', async () => {
    const fixture = await render();

    fixture.componentInstance.form.patchValue({ username: '' });
    fixture.componentInstance.save();

    expect(userService.update).not.toHaveBeenCalled();
  });

  it('displays the API error on save', async () => {
    const fixture = await render();
    userService.update.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { message: 'Déjà utilisé' } })),
    );

    fixture.componentInstance.save();

    expect(fixture.componentInstance.error()).toBe('Déjà utilisé');
    expect(fixture.componentInstance.saving()).toBe(false);
  });

  it('unsubscribes from a topic and reloads the subscriptions', async () => {
    const fixture = await render();
    topicService.unsubscribe.mockReturnValue(of(undefined));

    fixture.componentInstance.unsubscribe(TOPIC);
    await fixture.whenStable();

    expect(topicService.unsubscribe).toHaveBeenCalledWith(2);
    expect(snackBar.open).toHaveBeenCalledWith('Désabonné du thème Java', 'OK', {
      duration: 3000,
    });
    expect(topicService.getSubscriptions).toHaveBeenCalledTimes(2);
    expect(fixture.componentInstance.pending().has(2)).toBe(false);
  });

  it('notifies when the unsubscription fails', async () => {
    const fixture = await render();
    topicService.unsubscribe.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500, error: { message: 'Erreur' } })),
    );

    fixture.componentInstance.unsubscribe(TOPIC);

    expect(snackBar.open).toHaveBeenCalledWith('Erreur', 'OK', { duration: 4000 });
  });

  it('logs out', async () => {
    const fixture = await render();

    fixture.componentInstance.logout();

    expect(session.signOut).toHaveBeenCalled();
  });
});
