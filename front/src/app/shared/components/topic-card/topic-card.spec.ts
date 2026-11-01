import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TopicCard } from './topic-card';

describe('TopicCard', () => {
  let fixture: ComponentFixture<TopicCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [TopicCard] }).compileComponents();
    fixture = TestBed.createComponent(TopicCard);
    fixture.componentRef.setInput('topic', {
      id: 1,
      title: 'Java',
      description: 'Desc',
      subscribed: false,
    });
    fixture.componentRef.setInput('actionLabel', "S'abonner");
  });

  it('renders the topic and emits on click', async () => {
    let emitted: number | undefined;
    fixture.componentInstance.action.subscribe((topic) => (emitted = topic.id));
    await fixture.whenStable();

    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('.card__title')?.textContent).toContain('Java');
    element.querySelector('button')?.click();
    expect(emitted).toBe(1);
  });

  it('disables the button', async () => {
    fixture.componentRef.setInput('disabled', true);
    await fixture.whenStable();
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('button')?.disabled).toBe(true);
  });
});
