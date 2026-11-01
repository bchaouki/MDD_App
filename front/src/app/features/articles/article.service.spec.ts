import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ArticleService } from './article.service';
import { CommentService } from './comment.service';

describe('ArticleService & CommentService', () => {
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('requests the feed with the sort direction', () => {
    TestBed.inject(ArticleService).getFeed('asc').subscribe();
    const req = controller.expectOne((r) => r.url === '/api/feed');
    expect(req.request.params.get('sort')).toBe('asc');
  });

  it('creates an article', () => {
    TestBed.inject(ArticleService).create({ topicId: 1, title: 'T', content: 'C' }).subscribe();
    const req = controller.expectOne('/api/articles');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ topicId: 1, title: 'T', content: 'C' });
  });

  it('posts a comment on an article', () => {
    TestBed.inject(CommentService).create(4, 'Bravo').subscribe();
    const req = controller.expectOne('/api/articles/4/comments');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ content: 'Bravo' });
  });
});
