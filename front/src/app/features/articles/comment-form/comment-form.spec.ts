import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommentForm } from './comment-form';

describe('CommentForm', () => {
  let fixture: ComponentFixture<CommentForm>;
  let component: CommentForm;
  let sent: string[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [CommentForm] }).compileComponents();
    fixture = TestBed.createComponent(CommentForm);
    component = fixture.componentInstance;
    sent = [];
    component.send.subscribe((content) => sent.push(content));
    await fixture.whenStable();
  });

  it('emits the trimmed comment', () => {
    component.form.setValue({ content: '  Bravo  ' });

    component.submit();

    expect(sent).toEqual(['Bravo']);
  });

  it('does not emit a blank comment', () => {
    component.form.setValue({ content: '   ' });

    component.submit();

    expect(sent).toEqual([]);
  });

  it('does not emit while a comment is being sent', () => {
    fixture.componentRef.setInput('sending', true);
    component.form.setValue({ content: 'Bravo' });

    component.submit();

    expect(sent).toEqual([]);
  });

  it('clears the field on reset', () => {
    component.form.setValue({ content: 'Bravo' });

    component.reset();

    expect(component.form.getRawValue().content).toBe('');
  });
});
