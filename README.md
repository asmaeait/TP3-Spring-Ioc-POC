# TP3 — Spring IoC & POC (Principe Ouvert/Fermé)

> **Cours** : Développement JakartaEE — Spring  
> **Objectif** : Mettre en place plusieurs stratégies d'injection et de sélection d'implémentations `IDao` **sans modifier** `MetierImpl` ni la classe d'exécution.

![Java](https://img.shields.io/badge/Java-11-orange)
![Spring](https://img.shields.io/badge/Spring-5.3.30-green)
![Maven](https://img.shields.io/badge/Maven-3.x-red)
![JUnit](https://img.shields.io/badge/JUnit-4.13.2-blue)

---

## Table des matières

1. [Contexte et objectif](#1-contexte-et-objectif)
2. [Structure du projet](#2-structure-du-projet)
3. [Principe OCP appliqué](#3-principe-ocp-appliqué)
4. [Variante principale — Profils Spring](#4-variante-principale--profils-spring)
5. [Variante A — @Primary](#5-variante-a--primary)
6. [Variante B — Alias via @Bean](#6-variante-b--alias-via-bean)
7. [Variante C — Sélection par app.properties](#7-variante-c--sélection-par-appproperties)
8. [Tests JUnit](#8-tests-junit)
9. [Résultats attendus](#9-résultats-attendus)

---

## 1. Contexte et objectif

Ce TP illustre le **principe OCP (Open/Closed Principle)** appliqué avec Spring IoC :

- La classe `MetierImpl` est **fermée à la modification** : elle ne change jamais.
- Le système est **ouvert à l'extension** : on peut brancher une nouvelle implémentation de `IDao` uniquement par la configuration.

Les mécanismes abordés :
- **Profils Spring** (`@Profile`) : `dev`, `prod`, `file`, `api`
- **Priorité** via `@Primary`
- **Alias** via un `@Bean` de configuration
- **Sélection par propriété externe** : `@PropertySource` + `@Value`

---

## 2. Structure du projet

<img width="668" height="825" alt="image" src="https://github.com/user-attachments/assets/c72ffad9-46e3-4a0d-9e91-97c4165ca555" />

---

## 3. Principe OCP appliqué

Le cœur du TP repose sur ce principe :

| Classe | Rôle | Modifiée ? |
|---|---|---|
| `MetierImpl` | Logique métier, utilise `IDao` |  **Jamais** |
| `DaoImpl`, `DaoImpl2`... | Implémentations de `IDao` |  On en ajoute |
| `Presentation2`, configs | Sélection de l'implémentation |  Configuration uniquement |

`MetierImpl` utilise `@Autowired` sans `@Qualifier` — c'est Spring qui décide quelle implémentation injecter selon la configuration active.

---

## 4. Variante principale — Profils Spring

### Principe

Chaque implémentation de `IDao` est annotée avec `@Profile`. Spring ne charge dans le contexte **que le bean dont le profil est actif**.

### Comment tester

Dans `Presentation2.java`, décommenter **une seule ligne** avant `ctx.refresh()` :

```java
ctx.getEnvironment().setActiveProfiles("prod");  // → DaoImpl  (100) → 200.0
ctx.getEnvironment().setActiveProfiles("dev");   // → DaoImpl2 (150) → 300.0
ctx.getEnvironment().setActiveProfiles("file");  // → DaoFile  (180) → 360.0
ctx.getEnvironment().setActiveProfiles("api");   // → DaoApi   (220) → 440.0
```

### Résultats console

**Profil `dev` → DaoImpl2 → 300.0**
<img width="946" height="195" alt="testDev" src="https://github.com/user-attachments/assets/bd2b8200-ecf0-4dab-ac36-018ac0f4aa3a" />


---

**Profil `file` → DaoFile → 360.0**

<img width="902" height="205" alt="testFile" src="https://github.com/user-attachments/assets/dc6026d2-df11-465b-aef4-3c86c64dfa09" />

---

**Profil `api` → DaoApi → 440.0**

<img width="941" height="187" alt="testApi" src="https://github.com/user-attachments/assets/0b8057c7-39c4-4136-a4df-fa4d01cb8fe3" />

---

## 5. Variante A — @Primary

### Principe

Sans profil actif, si plusieurs beans `IDao` coexistent dans le contexte, `@Primary` désigne le bean **par défaut** que Spring injectera automatiquement.

### Ce qui change

- Retirer les `@Profile` de toutes les classes DAO
- Ajouter `@Primary` uniquement sur `DaoImpl2`

### Résultat console

**`@Primary` sur `DaoImpl2` → 300.0**

<img width="940" height="178" alt="PropertyDrivenConfig-IDaoimpl2" src="https://github.com/user-attachments/assets/1641d78f-648c-442d-a1fb-fe55d9ca0c75" />

---

## 6. Variante B — Alias via @Bean

### Principe

Une classe de configuration (`DaoAliasConfig`) expose un `@Bean(name = "dao")` qui retourne l'implémentation voulue. `MetierImpl` trouve ce bean par son nom et par son type — sans aucune modification.

Pour changer d'implémentation : modifier **uniquement** `DaoAliasConfig` en remplaçant la cible.

### Résultats console

**Alias pointant vers `DaoApi` → 440.0**

<img width="930" height="209" alt="avec-DaoAliasConfig_DaoApi" src="https://github.com/user-attachments/assets/6407e7ae-420d-498f-9b7b-3fa4f3709cab" />

---

**Alias pointant vers `DaoFile` → 360.0**

<img width="945" height="185" alt="PropertyDrivenConfig-IDaoFile" src="https://github.com/user-attachments/assets/5dc47813-a746-445e-b0a4-ffa55bd4a78b" />

---

**Alias pointant vers `DaoImpl2` → 300.0**

<img width="931" height="199" alt="DaoAliasConfig-DaoImpl2" src="https://github.com/user-attachments/assets/8f5825cb-b445-4382-865a-cf781aa8df4f" />

---

## 7. Variante C — Sélection par app.properties

### Principe

Le choix de l'implémentation est externalisé dans un fichier `.properties`. `PropertyDrivenConfig` lit la valeur de `dao.target` et construit dynamiquement le bon bean.

`@Profile("!prod & !dev & !file & !api")` garantit que cette config ne s'active que si **aucun profil n'est actif**.

### Fichier `app.properties`

```properties
dao.target=daoApi   # valeurs possibles : daoImpl | dao2 | daoFile | daoApi
```

### Résultats console

**`dao.target=daoApi` → DaoApi → 440.0**

<img width="930" height="209" alt="avec-DaoAliasConfig_DaoApi" src="https://github.com/user-attachments/assets/4bd397b6-912c-4a8f-9cb0-977f3651032c" />

---

**`dao.target=daoFile` → DaoFile → 360.0**

<img width="950" height="182" alt="DaoAliasConfig-DaoFile" src="https://github.com/user-attachments/assets/71111cd4-4dd9-4729-b122-3ea914584dca" />

---

**`dao.target=dao2` → DaoImpl2 → 300.0**

<img width="931" height="199" alt="DaoAliasConfig-DaoImpl2" src="https://github.com/user-attachments/assets/251a2def-4fc6-4418-8cfe-2a5af89aa4f0" />

---

## 8. Tests JUnit

### Objectif des tests

Valider le principe OCP : **`MetierImpl` ne change jamais** et pourtant on obtient des résultats différents selon la configuration.

### Les 3 tests

| Test | Mécanisme | Valeur attendue |
|---|---|---|
| `devProfile_choisitDao2_300` | Profil `dev` actif | `300.0` |
| `prodProfile_choisitDao_200` | Profil `prod` actif | `200.0` |
| `propertyDriven_daoApi_440` | `System.setProperty("dao.target","daoApi")` | `440.0` |

### Résultat — 3 tests verts 

<img width="960" height="237" alt="testJUnit" src="https://github.com/user-attachments/assets/432270ac-24a5-46ef-baff-3edee57e4173" />





### Tableau récapitulatif

| Mécanisme | Implémentation | `getValue()` | `calcul()` |
|---|---|---|---|
| `@Profile("prod")` | `DaoImpl` | `100.0` | **200.0** |
| `@Profile("dev")` | `DaoImpl2` | `150.0` | **300.0** |
| `@Profile("file")` | `DaoFile` | `180.0` | **360.0** |
| `@Profile("api")` | `DaoApi` | `220.0` | **440.0** |

### Tableau des variantes

| Variante | Mécanisme | Avantage |
|---|---|---|
| Profils | `@Profile` | Séparation claire par environnement |
| @Primary | Priorité par défaut | Simple, sans configuration supplémentaire |
| Alias @Bean | `@Bean(name="dao")` | Contrôle explicite dans une classe de config |
| app.properties | `@Value` + `@PropertySource` | Basculement sans recompiler |

### Conclusion

> Dans **toutes** les variantes du TP, `MetierImpl` est resté **strictement identique**.  
> Le basculement d'implémentation s'est fait uniquement par la configuration —  
> c'est la définition concrète du **principe OCP** appliqué avec Spring IoC. 🎯
