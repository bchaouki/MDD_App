-- MDD — thèmes initiaux (la création de thèmes est hors périmètre du MVP)
USE mdd;

INSERT INTO topics (title, description) VALUES
    ('Java', 'Le langage, la JVM et son écosystème : versions LTS, nouveautés, bonnes pratiques.'),
    ('Spring', 'Spring Boot, Spring Security, Spring Data : construire des back-ends robustes.'),
    ('JavaScript', 'Le langage du web, du navigateur à Node.js.'),
    ('TypeScript', 'Typage statique pour JavaScript : types avancés, configuration, outillage.'),
    ('Angular', 'Le framework front-end de Google : composants, RxJS, signaux, architecture.'),
    ('React', 'La bibliothèque d''interfaces de Meta et son écosystème.'),
    ('Python', 'Scripts, data science, back-end : tout ce qui touche à Python.'),
    ('DevOps', 'CI/CD, conteneurs, Kubernetes, observabilité et infrastructure as code.'),
    ('Bases de données', 'SQL, NoSQL, modélisation, performance et migrations.'),
    ('Sécurité', 'OWASP, authentification, cryptographie appliquée et bonnes pratiques.');
