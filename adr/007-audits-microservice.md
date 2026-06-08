# 7. Microservice dédié à la gestion des audits

## Statut
Proposé

## Contexte
Actuellement, la gestion des audits est intégrée au service principal de gestion des **assets/items**. Cette intégration pose plusieurs problèmes :
- **Complexité accrue** : Les règles métier des audits (planification, historique, conformité) contaminent la logique des assets.
- **Couplage fort** : Les modifications sur les audits impactent directement le cœur de métiers (assets/items).
- **Évolutivité limitée** : Impossible de faire évoluer les audits indépendamment (ex : ajouter des workflows spécifiques, des notifications, ou des intégrations tierces).
- **Performances** : Les requêtes lourdes sur les audits (ex : rapports historiques) ralentissent les opérations sur les assets.

Les audits ont des **besoins distincts** :
- Cycle de vie différent (planifié, exécuté, archivé).
- Données temporelles (état à un instant T).
- Exigences de conformité et de traçabilité strictes.

## Décision
**Créer un microservice dédié aux audits**, séparé du service de gestion des assets/items.

### Architecture
```
└── API Gateway
    ├── Assets Service (items, configurations, relations)
    └── Audits Service (audits, historiques, rapports, conformité)
```

### Intégration
- **Communication asynchrone** via événements (ex : Kafka, RabbitMQ) :
  - `AssetCreated`, `AssetUpdated`, `AssetDeleted` → déclenchent la création/mise à jour des audits.
- **API REST/GraphQL** pour :
  - CRUD des audits.
  - Requêtes spécifiques (historique, rapport de conformité).
- **Base de données dédiée** : Optimisée pour les requêtes temporelles (ex : TimescaleDB pour PostgreSQL).

### Périmètre du microservice
| Fonctionnalité               | Service concerné       |
|-----------------------------|-------------------------|
| CRUD assets/items           | Assets Service          |
| CRUD audits                 | **Audits Service**      |
| Historique des modifications | **Audits Service**    |
| Rapports de conformité      | **Audits Service**      |
| Planification des audits    | **Audits Service**      |
| Notifications (ex : échéance)| **Audits Service**      |

## Conséquences

### Positives
✅ **Découplage total** : Les assets et les audits évoluent indépendamment.
✅ **Simplification** : Chaque service a une seule responsabilité (Single Responsibility Principle).
✅ **Évolutivité** : Possibilité d'ajouter des features spécifiques aux audits (ex : workflows de validation, intégration avec des outils tiers comme Jira ou ServiceNow).
✅ **Performances** : Optimisation des requêtes lourdes (ex : agrégation de données historiques) sans impacter les assets.
✅ **Maintenabilité** : Code plus simple à comprendre et à tester.

### Négatives
⚠ **Complexité distribuée** : Gestion des transactions distribuées (ex : saga pattern pour la cohérence).
⚠ **Latence réseau** : Appels inter-services via le réseau (à atténuer avec du caching).
⚠ **Déploiement** : Coordination entre les services pour les releases (utilisation de contrats d'API versionnés).

## Alternatives considérées

| Alternative               | Avantages                          | Inconvénients                          |
|---------------------------|------------------------------------|----------------------------------------|
| **Module dans Assets Service** | Pas de latence réseau, transactions locales. | Couplage fort, complexité accrue. |
| **BFF (Backend for Frontend)** | Agrège les données pour le frontend. | Ne résout pas le couplage métier. |
| **CQRS + Event Sourcing** | Historique natif, auditabilité. | Complexité technique accrue. |

## Validation
- [ ] Valider le format des événements avec l'équipe Assets.
- [ ] Définir les contrats d'API (OpenAPI/Swagger).
- [ ] Choisir la technologie (ex : Spring Boot, NestJS, FastAPI).
- [ ] Planifier la migration des données existantes (si applicable).

---
*Date : 2026-06-08*
*Décideurs : À compléter*
*Référence : [MADR](https://adr.github.io/madr/)*
