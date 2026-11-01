export type SortDirection = 'asc' | 'desc';

export interface ArticleSummary {
  id: number;
  title: string;
  excerpt: string;
  authorUsername: string;
  topicTitle: string;
  createdAt: string;
}

export interface Article {
  id: number;
  title: string;
  content: string;
  authorUsername: string;
  topicId: number;
  topicTitle: string;
  createdAt: string;
}

export interface CreateArticleRequest {
  topicId: number;
  title: string;
  content: string;
}
