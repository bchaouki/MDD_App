import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { Topic } from '../../../core/models/topic';
import { TopicService } from '../topic.service';
import { TopicList } from './topic-list';

const JAVA: Topic = { id: 1, title: 'Java', description: 'JVM', subscribed: false };
const ANGULAR: Topic = { id: 2, title: 'Angular', description: 'Front', subscribed: true };

describe('TopicList', () => {
  const topicService = { getAll: vi.fn(), subscribe: vi.fn() };
  const snackBar = { open: vi.fn() };

  async function render(): Promise<ComponentFixture<TopicList>> {
    await TestBed.configureTestingModule({
      imports: [TopicList],
      providers: [
        { provide: TopicService, useValue: topicService },
        { provide: MatSnackBar, useValue: snackBar },
      ],
    }).compileComponents();
    const fixture = TestBed.createComponent(TopicList);
    await fixture.whenStable();
    return fixture;
  }

  beforeEach(() => {
    vi.clearAllMocks();
    topicService.getAll.mockReturnValue(of([JAVA, ANGULAR]));
  });

  it('shows every topic and marks the followed ones', async () => {
    const fixture = await render();
    const buttons: HTMLButtonElement[] = Array.from(
      fixture.nativeElement.querySelectorAll('app-topic-card button'),
    );

    expect(buttons.map((button) => button.textContent?.trim())).toEqual([
      "S'abonner",
      'Déjà abonné',
    ]);
    expect(buttons[1].disabled).toBe(true);
  });

  it('subscribes to a topic and reloads the list', async () => {
    const fixture = await render();
    topicService.subscribe.mockReturnValue(of(undefined));

    fixture.componentInstance.subscribe(JAVA);
    await fixture.whenStable();

    expect(topicService.subscribe).toHaveBeenCalledWith(1);
    expect(snackBar.open).toHaveBeenCalledWith('Abonné au thème Java', 'OK', { duration: 3000 });
    expect(topicService.getAll).toHaveBeenCalledTimes(2);
    expect(fixture.componentInstance.pending().has(1)).toBe(false);
  });

  it('notifies when the subscription fails', async () => {
    const fixture = await render();
    topicService.subscribe.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 500, error: { message: 'Erreur' } })),
    );

    fixture.componentInstance.subscribe(JAVA);

    expect(snackBar.open).toHaveBeenCalledWith('Erreur', 'OK', { duration: 4000 });
  });

  it('displays the error and an empty state when the topics cannot be loaded', async () => {
    topicService.getAll.mockReturnValue(throwError(() => new HttpErrorResponse({ status: 0 })));

    const fixture = await render();
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('.form-error')?.textContent).toContain(
      'Le serveur est injoignable',
    );
    expect(element.querySelector('.empty-state')).not.toBeNull();
  });
});
