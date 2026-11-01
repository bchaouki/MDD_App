// Remplacé par environment.prod.ts lors d'un `ng build` (voir `fileReplacements` dans angular.json).
export const environment = {
  production: false,
  // Relayé vers l'API par proxy.conf.json (pas de CORS en dev).
  apiUrl: '/api',
};
