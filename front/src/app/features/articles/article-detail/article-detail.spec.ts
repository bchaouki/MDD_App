import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Article } from '../../../core/models/article';
import { Comment } from '../../../core/models/comment';
import { ArticleService } from '../article.service';
import { CommentForm } from '../comment-form/comment-form';
import { CommentService } from '../comment.service';
import { ArticleDetail } from './article-detail';

const ARTICLE: Article = {
  id: 3,
  title: 'Spring Boot 4',
  content: 'Contenu complet',
  authorUsername: 'alice',
  topicId: 1,
  topicTitle: 'Java',
  createdAt: '2026-10-01T10:00:00',
};
const COMMENT: Comment = {
  id: 9,
  content: 'Bravo',
  authorUsername: 'bob',
  createdAt: '2026-10-02T10:00:00',
};

describe('ArticleDetail', () => {
  const articleService = { getById: vi.fn() };
  const commentService = { getByArticle: vi.fn(), create: vi.fn() };
  const snackBar = { open: vi.fn() };

  async function render(): Promise<ComponentFixture<ArticleDetail>> {
    await TestBed.configureTestingModule({
      imports: [ArticleDetail],
      providers: [
        provideRouter([]),
        { provide: ArticleService, useValue: articleService },
        { provide: CommentService, useValue: commentService },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(ArticleDetail);
    fixture.componentRef.setInput('id', 3);
    await fixture.whenStable();
    return fixture;
  }

  beforeEach(() => {
    vi.clearAllMocks();
    articleService.getById.mockReturnValue(of(ARTICLE));
    commentService.getByArticle.mockReturnValue(of([COMMENT]));
  });

  it('displays the article and its comments', async () => {
    const fixture = await render();
    const element: HTMLElement = fixture.nativeElement;

    expect(articleService.getById).toHaveBeenCalledWith(3);
    expect(element.querySelector('h1')?.textContent).toContain('Spring Boot 4');
    expect(element.querySelectorAll('.comment').length).toBe(1);
  });

  it('shows a message when the article does not exist', async () => {
    articleService.getById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    const fixture = await render();

    const emptyState = fixture.nativeElement.querySelector('.empty-state');
    expect(emptyState?.querySelector('h1')?.textContent).toContain('Article introuvable');
    expect(emptyState?.textContent).toContain("Cet article n'existe pas");
  });

  it('shows the API message for other errors', async () => {
    articleService.getById.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500, error: { message: 'Erreur serveur' } })),
    );

    const fixture = await render();

    const emptyState = fixture.nativeElement.querySelector('.empty-state');
    expect(emptyState?.querySelector('h1')).toBeNull();
    expect(emptyState?.textContent).toContain('Erreur serveur');
  });

  it('adds a comment, resets the form and reloads the comments', async () => {
    const fixture = await render();
    const form = { reset: vi.fn() } as unknown as CommentForm;
    commentService.create.mockReturnValue(of(COMMENT));

    fixture.componentInstance.addComment('Bravo', form);
    await fixture.whenStable();

    expect(commentService.create).toHaveBeenCalledWith(3, 'Bravo');
    expect(form.reset).toHaveBeenCalled();
    expect(commentService.getByArticle).toHaveBeenCalledTimes(2);
    expect(fixture.componentInstance.sendingComment()).toBe(false);
  });

  it('notifies when the comment cannot be sent', async () => {
    const fixture = await render();
    const form = { reset: vi.fn() } as unknown as CommentForm;
    commentService.create.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500, error: { message: 'Erreur' } })),
    );

    fixture.componentInstance.addComment('Bravo', form);

    expect(snackBar.open).toHaveBeenCalledWith('Erreur', 'OK', { duration: 4000 });
    expect(form.reset).not.toHaveBeenCalled();
    expect(fixture.componentInstance.sendingComment()).toBe(false);
  });
});
