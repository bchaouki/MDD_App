import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NotFound } from './not-found';

describe('NotFound', () => {
  it('explains the error and links back to the home page', async () => {
    await TestBed.configureTestingModule({
      imports: [NotFound],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(NotFound);

    await fixture.whenStable();
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('h1')?.textContent).toContain('Page introuvable');
    expect(element.querySelector('a')?.getAttribute('href')).toBe('/');
  });
});
