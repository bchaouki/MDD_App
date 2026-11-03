import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ArticleCard } from './article-card';

describe('ArticleCard', () => {
  it('shows the article summary and links to its detail page', async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleCard],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(ArticleCard);
    fixture.componentRef.setInput('article', {
      id: 7,
      title: 'Angular Signals',
      excerpt: 'Les signals…',
      authorUsername: 'alice',
      topicTitle: 'Angular',
      createdAt: '2026-10-01T10:00:00',
    });

    await fixture.whenStable();
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('.card__title')?.textContent).toContain('Angular Signals');
    expect(element.querySelector('.card__meta')?.textContent).toContain('alice');
    expect(element.querySelector('a')?.getAttribute('href')).toBe('/articles/7');
  });
});
