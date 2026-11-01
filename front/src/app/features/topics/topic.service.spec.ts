import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TopicService } from './topic.service';

describe('TopicService', () => {
  let service: TopicService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(TopicService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('subscribes and unsubscribes', () => {
    service.subscribe(3).subscribe();
    expect(controller.expectOne('/api/topics/3/subscribe').request.method).toBe('POST');

    service.unsubscribe(3).subscribe();
    expect(controller.expectOne('/api/topics/3/subscribe').request.method).toBe('DELETE');
  });

  it('lists the user subscriptions', () => {
    service.getSubscriptions().subscribe();
    expect(controller.expectOne('/api/users/me/subscriptions').request.method).toBe('GET');
  });
});
