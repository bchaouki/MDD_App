import { Route } from '@angular/router';
import { authGuard, unauthGuard } from './core/guards/auth.guards';
import { routes } from './app.routes';

/** Toutes les routes, enfants compris. */
function flatten(list: Route[]): Route[] {
  return list.flatMap((route) => [route, ...flatten(route.children ?? [])]);
}

describe('app routes', () => {
  const all = flatten(routes);

  it('lazy-loads a component for every page', async () => {
    const lazy = all.filter((route) => route.loadComponent);

    const components = await Promise.all(lazy.map((route) => route.loadComponent!()));

    expect(lazy.map((route) => route.path)).toEqual([
      '',
      'login',
      'register',
      'feed',
      'articles/new',
      'articles/:id',
      'topics',
      'me',
    ]);
    components.forEach((component) => expect(component).toBeTruthy());
  });

  it('keeps public pages for visitors and private pages for members', () => {
    const publicPages = all.filter((route) => route.canActivate?.includes(unauthGuard));
    const privateGroup = all.find((route) => route.canActivateChild?.includes(authGuard));

    expect(publicPages.map((route) => route.path)).toEqual(['', 'login', 'register']);
    expect(privateGroup?.children?.map((route) => route.path)).toEqual([
      'feed',
      'articles/new',
      'articles/:id',
      'topics',
      'me',
    ]);
  });

  it('ends with a not-found page', () => {
    expect(routes.at(-1)?.path).toBe('**');
  });
});
