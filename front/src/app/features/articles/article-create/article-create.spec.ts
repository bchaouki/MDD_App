import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Article } from '../../../core/models/article';
import { TopicService } from '../../topics/topic.service';
import { ArticleService } from '../article.service';
import { ArticleCreate } from './article-create';

describe('ArticleCreate', () => {
  let fixture: ComponentFixture<ArticleCreate>;
  let component: ArticleCreate;
  const articleService = { create: vi.fn() };
  const topicService = { getAll: vi.fn() };
  const snackBar = { open: vi.fn() };

  beforeEach(async () => {
    vi.clearAllMocks();
    topicService.getAll.mockReturnValue(
      of([{ id: 1, title: 'Java', description: 'JVM', subscribed: false }]),
    );
    await TestBed.configureTestingModule({
      imports: [ArticleCreate],
      providers: [
        provideRouter([]),
        { provide: ArticleService, useValue: articleService },
        { provide: TopicService, useValue: topicService },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCreate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('loads the topics for the select', () => {
    expect(component.topics()).toHaveLength(1);
  });

  it('does not call the API when the form is invalid', () => {
    component.submit();

    expect(articleService.create).not.toHaveBeenCalled();
    expect(component.form.touched).toBe(true);
  });

  it('creates the article, notifies and goes back to the feed', () => {
    const navigate = vi.spyOn(TestBed.inject(Router), 'navigateByUrl').mockResolvedValue(true);
    articleService.create.mockReturnValue(of({} as Article));

    component.form.setValue({ topicId: 1, title: '  Titre  ', content: 'Contenu' });
    component.submit();

    expect(articleService.create).toHaveBeenCalledWith({
      topicId: 1,
      title: 'Titre',
      content: 'Contenu',
    });
    expect(snackBar.open).toHaveBeenCalledWith('Article publié', 'OK', { duration: 3000 });
    expect(navigate).toHaveBeenCalledWith('/feed');
    expect(component.submitting()).toBe(false);
  });

  it('displays the API error', () => {
    articleService.create.mockReturnValue(
      throwError(
        () => new HttpErrorResponse({ status: 404, error: { message: 'Thème introuvable' } }),
      ),
    );

    component.form.setValue({ topicId: 1, title: 'Titre', content: 'Contenu' });
    component.submit();

    expect(component.error()).toBe('Thème introuvable');
    expect(component.submitting()).toBe(false);
  });
});
