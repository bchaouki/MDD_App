import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Landing } from './landing';

describe('Landing', () => {
  it('links to the login and register pages', async () => {
    await TestBed.configureTestingModule({
      imports: [Landing],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(Landing);

    await fixture.whenStable();
    const links: HTMLAnchorElement[] = Array.from(fixture.nativeElement.querySelectorAll('a'));

    expect(links.map((link) => link.getAttribute('href'))).toEqual(['/login', '/register']);
    expect(fixture.nativeElement.querySelector('img')?.getAttribute('alt')).toBe(
      'MDD - Monde de Dév',
    );
  });
});
