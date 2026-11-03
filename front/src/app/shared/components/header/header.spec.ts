import { BreakpointObserver } from '@angular/cdk/layout';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { SessionService } from '../../../core/services/session.service';
import { Header } from './header';

describe('Header', () => {
  const isLogged = signal(true);

  async function render(handset: boolean): Promise<ComponentFixture<Header>> {
    await TestBed.configureTestingModule({
      imports: [Header],
      providers: [
        provideRouter([]),
        { provide: SessionService, useValue: { isLogged } },
        { provide: BreakpointObserver, useValue: { observe: () => of({ matches: handset }) } },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(Header);
    await fixture.whenStable();
    return fixture;
  }

  beforeEach(() => isLogged.set(true));

  it('shows the navigation links on desktop', async () => {
    const fixture = await render(false);
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelectorAll('.header__link').length).toBe(2);
    expect(element.querySelector('.header__profile')).not.toBeNull();
  });

  it('shows a burger button on mobile that toggles the menu', async () => {
    const fixture = await render(true);
    let toggled = 0;
    fixture.componentInstance.menuToggle.subscribe(() => toggled++);

    fixture.nativeElement.querySelector('button[aria-label="Ouvrir le menu"]').click();

    expect(fixture.nativeElement.querySelector('.header__nav')).toBeNull();
    expect(toggled).toBe(1);
  });

  it('only shows the logo to a visitor', async () => {
    isLogged.set(false);

    const fixture = await render(false);
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('.header__logo')).not.toBeNull();
    expect(element.querySelector('.header__nav')).toBeNull();
  });
});
