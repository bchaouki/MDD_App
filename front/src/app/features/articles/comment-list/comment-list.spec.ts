import { TestBed } from '@angular/core/testing';
import { CommentList } from './comment-list';

describe('CommentList', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [CommentList] }).compileComponents();
  });

  it('lists the comments with their author', async () => {
    const fixture = TestBed.createComponent(CommentList);
    fixture.componentRef.setInput('comments', [
      { id: 1, content: 'Bravo', authorUsername: 'bob', createdAt: '2026-10-02T10:00:00' },
    ]);

    await fixture.whenStable();
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelectorAll('.comment').length).toBe(1);
    expect(element.querySelector('.comment__author')?.textContent).toContain('bob');
  });

  it('shows a message when there is no comment', async () => {
    const fixture = TestBed.createComponent(CommentList);

    await fixture.whenStable();

    expect(fixture.nativeElement.querySelector('.comments__empty')?.textContent).toContain(
      'Aucun commentaire',
    );
  });
});
