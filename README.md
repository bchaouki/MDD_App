# MDD — Monde de Dév

Réseau social pour développeurs : abonnement à des thèmes, fil d'actualité, articles et commentaires.
L'architecture est décrite dans [ARCHITECTURE.md](ARCHITECTURE.md).

| Dossier | Contenu |
|---|---|
| `back/` | API REST Spring Boot 4.1 / Java 21 (`mdd-api`) |
| `front/` | SPA Angular 21 (standalone, signals, zoneless) + Angular Material |
| `database/` | `schema.sql` (tables) et `data.sql` (thèmes initiaux) |

## Prérequis

- Java 21 (Maven est fourni par le wrapper `back/mvnw`)
- Node.js 20.19+, 22.12+ ou 24+ et npm
- MySQL 8

## 1. Base de données

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql
```

Créer ensuite un utilisateur dédié :

```sql
CREATE USER 'mdd'@'localhost' IDENTIFIED BY 'un-mot-de-passe';
GRANT SELECT, INSERT, UPDATE, DELETE ON mdd.* TO 'mdd'@'localhost';
```

Hibernate tourne en `ddl-auto=validate` : il vérifie le schéma au démarrage mais ne le modifie jamais.

## 2. API (`back/`)

La configuration se fait par variables d'environnement. En local, créer un fichier `back/.env`
(non versionné, lu automatiquement grâce à `spring.config.import`) :

```properties
DB_URL=jdbc:mysql://localhost:3306/mdd
DB_USERNAME=mdd
DB_PASSWORD=un-mot-de-passe
JWT_SECRET=une-chaine-aleatoire-d-au-moins-32-caracteres
```

| Variable | Description |
|---|---|
| `DB_URL` | URL JDBC (défaut : `jdbc:mysql://localhost:3306/mdd`) |
| `DB_USERNAME` / `DB_PASSWORD` | identifiants MySQL |
| `JWT_SECRET` | secret de signature HS256, **au moins 32 caractères** (`openssl rand -base64 48`) |

```bash
cd back
./mvnw spring-boot:run          # Windows : mvnw.cmd spring-boot:run
```

L'API écoute sur `http://localhost:8080/api`.

### Documentation Swagger

| | URL |
|---|---|
| Interface Swagger UI | http://localhost:8080/swagger-ui.html |
| Contrat OpenAPI (JSON) | http://localhost:8080/v3/api-docs |

Ces deux URL sont publiques. Pour tester une route protégée depuis Swagger UI :

1. appeler `POST /api/auth/login` (ou `/register`) et copier le `token` renvoyé ;
2. cliquer sur **Authorize** et coller le jeton (sans le préfixe `Bearer`) ;
3. toutes les requêtes suivantes envoient `Authorization: Bearer <token>`.

### Tests

Les tests utilisent une base H2 en mémoire (profil `test`) : aucune base MySQL n'est nécessaire.

```bash
./mvnw test
```

## 3. Front (`front/`)

```bash
cd front
npm install
npm start       # http://localhost:4200
```

En développement, `ng serve` relaie `/api` vers `http://localhost:8080` (`proxy.conf.json`) :
il n'y a pas de CORS à gérer. L'API doit donc être lancée en parallèle.

```bash
npm run build              # build de production dans dist/front
npx ng test --watch=false  # tests unitaires (Vitest + jsdom, aucun navigateur requis)
npm run test:coverage      # mêmes tests + couverture : front/coverage/front/index.html
npm run cypress:run        # tests end-to-end Cypress (front lancé sur :4200)
```

La couverture est calculée sur **tous** les fichiers de `src/app` (hors `*.spec.ts`),
y compris ceux qu'aucun test ne charge : un composant sans test fait baisser le chiffre.
