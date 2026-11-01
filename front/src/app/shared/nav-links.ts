export interface NavLink {
  label: string;
  path: string;
}

export const NAV_LINKS: readonly NavLink[] = [
  { label: 'Articles', path: '/feed' },
  { label: 'Thèmes', path: '/topics' },
];
