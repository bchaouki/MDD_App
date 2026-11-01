import { Routes } from '@angular/router';
import { authGuard, unauthGuard } from './core/guards/auth.guards';
import { NotFound } from './pages/not-found/not-found';

export const routes: Routes = [
  // Pages publiques
  {
    path: '',
    pathMatch: 'full',
    canActivate: [unauthGuard],
    loadComponent: () => import('./features/auth/landing/landing').then((m) => m.Landing),
    title: 'MDD',
  },
  {
    path: 'login',
    canActivate: [unauthGuard],
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
    title: 'Se connecter - MDD',
  },
  {
    path: 'register',
    canActivate: [unauthGuard],
    loadComponent: () => import('./features/auth/register/register').then((m) => m.Register),
    title: 'Inscription - MDD',
  },

  // Pages réservées aux utilisateurs connectés
  {
    path: '',
    canActivateChild: [authGuard],
    children: [
      {
        path: 'feed',
        loadComponent: () => import('./features/articles/feed/feed').then((m) => m.Feed),
        title: 'Articles - MDD',
      },
      {
        path: 'articles/new',
        loadComponent: () =>
          import('./features/articles/article-create/article-create').then((m) => m.ArticleCreate),
        title: 'Nouvel article - MDD',
      },
      {
        path: 'articles/:id',
        loadComponent: () =>
          import('./features/articles/article-detail/article-detail').then((m) => m.ArticleDetail),
        title: 'Article - MDD',
      },
      {
        path: 'topics',
        loadComponent: () =>
          import('./features/topics/topic-list/topic-list').then((m) => m.TopicList),
        title: 'Thèmes - MDD',
      },
      {
        path: 'me',
        loadComponent: () => import('./features/profile/profile/profile').then((m) => m.Profile),
        title: 'Profil - MDD',
      },
    ],
  },

  { path: '**', component: NotFound, title: 'Page introuvable - MDD' },
];
