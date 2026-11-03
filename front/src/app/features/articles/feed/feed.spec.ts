import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ArticleSummary } from '../../../core/models/article';
import { ArticleService } from '../article.service';
import { Feed } from './feed';

const ARTICLE: ArticleSummary = {
  id: 1,
  title: 'Angular Signals',
  excerpt: 'Les signals…',
  authorUsername: 'alice',
  topicTitle: 'Angular',
  createdAt: '2026-10-01T10:00:00',
};

describe('Feed', () => {
  const articleService = { getFeed: vi.fn() };

  async function render(): Promise<ComponentFixture<Feed>> {
    await TestBed.configureTestingModule({
      imports: [Feed],
      providers: [provideRouter([]), { provide: ArticleService, useValue: articleService }],
    }).compileComponents();
    const fixture = TestBed.createComponent(Feed);
    await fixture.whenStable();
    return fixture;
  }

  beforeEach(() => vi.clearAllMocks());

  it('displays the articles of the feed, newest first by default', async () => {
    articleService.getFeed.mockReturnValue(of([ARTICLE]));

    const fixture = await render();

    expect(articleService.getFeed).toHaveBeenCalledWith('desc');
    expect(fixture.nativeElement.querySelectorAll('app-article-card').length).toBe(1);
  });

  it('shows an empty state when there is no article', async () => {
    articleService.getFeed.mockReturnValue(of([]));

    const fixture = await render();

    expect(fixture.nativeElement.querySelector('.empty-state')?.textContent).toContain(
      'Aucun article',
    );
  });

  it('reloads the feed in ascending order when the sort is toggled', async () => {
    articleService.getFeed.mockReturnValue(of([ARTICLE]));
    const fixture = await render();

    fixture.componentInstance.toggleSort();
    await fixture.whenStable();

    expect(fixture.componentInstance.sort()).toBe('asc');
    expect(articleService.getFeed).toHaveBeenLastCalledWith('asc');
  });

  it('displays the API error', async () => {
    articleService.getFeed.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 0 })));

    const fixture = await render();

    expect(fixture.nativeElement.querySelector('.form-error')?.textContent).toContain(
      'Le serveur est injoignable',
    );
  });
});
