# Kravspecifikation – Xelin ITSM-system
Dokumentbeteckning: XELIN-KRAV-001

Version: 1.0

Datum: 2026-06-13

Författare: Benjamin Appelberg

Status: Utkast

## 1. Inledning
### 1.1 Syfte
Detta dokument specificerar krav för Xelin, ett internt ITSM- och helpdesksystem avsett för driftsättning vid FOI (Totalförsvarets forskningsinstitut). Systemet ska hantera IT-supportärenden, användarförfrågningar och incidentrapportering inom organisationen.
## 1.2 Omfattning
Xelin omfattar ärendehantering, användarautentisering via befintlig AD-infrastruktur, rollbaserad åtkomstkontroll samt administrativt gränssnitt för IT-personal. Systemet är inte avsett att ersätta klassificerade system eller hantera sekretessbelagd information.
## 1.3 Definitioner
Term, Definition

* Ärende = En registrerad IT-supportförfrågan eller incident
* agent = IT-personal med behörighet att hantera ärenden
* end_user = FOI-anställd som skapar ärenden
* LDAP = Lightweight Directory Access Protocol katalogtjänst

## 2. Systemöversikt
Xelin är en webbapplikation med tre-lagers arkitektur: React-frontend, java-backend och PostgreSQL-databas. Autentisering sker mot FOIs befintliga Active Directory via LDAP. Systemet driftsätts på intern infrastruktur med Docker Compose och exponeras via Nginx.

## 3. Användarroller
### 3.1 Slutanvändare (User)
Anställd vid FOI med tillgång till systemet via AD-konto. Kan skapa, följa och kommentera egna ärenden.
### 3.2 Handläggare (Agent)
IT-supportpersonal. Kan hantera alla ärenden, tilldela, eskalera och stänga ärenden. Tillgång till kövy och statistiköversikt.
### 3.3 Administratör (Admin)
IT-ansvarig. Full systembehörighet inklusive användarkonfiguration, kategorier och granskningslogg.

## 4. Funktionella krav
### 4.1 Autentisering och behörighet
* KR-101: Systemet ska använda Spring Security som autentiseringsramverk och stödja OAuth2/OIDC som autentiseringsflöde. Identiteter ska hämtas från företagets centrala katalogtjänst via identitetsleverantör (t.ex. Microsoft Entra ID kopplad till Active Directory).
* KR-102 Roller: (User, Agent, Admin) ska härledas från AD-gruppmedlemskap och synkroniseras vid inloggning.
* KR-103: Sessioner ska hanteras server-side med säker cookie. JWT ska inte användas.
* KR-104: Inaktiva sessioner ska automatiskt ogiltigförklaras efter 60 minuters inaktivitet.
* KR-105: Systemet ska logga samtliga inloggnings- och utloggningshändelser med tidsstämpel och användar-ID.
### 4.2 Ärendehantering
* KR-201: Slutanvändare ska kunna skapa ärenden med titel, beskrivning, prioritet och kategori.
* KR-202: Systemet ska automatiskt tilldela ett unikt ärende-ID vid skapande.
* KR-203: Följande ärendestatusar ska stödjas: Ny, Tilldelad, Pågående, Väntande, Löst, Stängd.
* KR-204 Handläggare ska kunna tilldela ärenden till sig själva eller annan handläggare.
* KR-205: Alla statusändringar ska loggas med tidsstämpel och ansvarig användare.
* KR-206: Slutanvändare ska kunna följa status på egna ärenden i realtid.
* KR-207: Handläggare och slutanvändare ska kunna lägga till kommentarer på ett ärende. Interna kommentarer ska endast vara synliga för handläggare och administratörer.
* KR-208: Systemet ska stödja filbilagor upp till 10 MB per ärende.
### 4.3 Köhantering och tilldelning
* KR-301: Handläggare ska ha tillgång till en samlad ärendekö filtrerad på status, prioritet, createdAt, assignee, och kategori.
### 4.4 Notifieringar
* KR-401: Systemet ska skicka e-postnotifiering till slutanvändaren vid statusändring på dennes ärende.
* KR-402: Handläggare ska notifieras vid tilldelning av nytt ärende.
### 4.5 Rapportering och statistik
* KR-501: Administratörer och handläggare ska ha tillgång till en statistikvy med ärendevolym, genomsnittlig lösningstid och SLA-uppfyllnad.
* KR-502: Statistik ska kunna filtreras på tidsperiod och kategori.
* KR-503: Granskningslogg (audit log) ska vara tillgänglig för administratör och visa alla systemhändelser.

## 5. Icke-funktionella krav
### 5.1 Prestanda
* KR-601 Svarstid för API-anrop ska understiga 300 ms under normal belastning (upp till 50 samtidiga användare).
KR-602 Gränssnittet ska vara fullt interaktivt inom 2 sekunder efter inloggning.
### 5.2 Tillgänglighet
* KR-701 Systemet ska ha en tillgänglighet på minst 99 % under ordinarie arbetstid (07:00–18:00, måndag–fredag).
* KR-702 Planerat underhåll ska kunna utföras utan förlust av ärendedata.
### 5.3 Säkerhet
* KR-801 All kommunikation ska ske över HTTPS med TLS 1.2 eller senare.
* KR-802 Systemet ska inte exponera interna felmeddelanden eller stackspår mot klienten.
* KR-803 Samtliga databasfrågor ska parametriseras. SQL-injektion ska inte vara möjlig.
* KR-804 Behörighetskontroll ska ske server-side för varje API-anrop. Frontend-kontroller är komplement, inte skydd.
* KR-805 Lösenord och AD-autentiseringsuppgifter ska aldrig lagras i klartext eller loggas.
### 5.4 Underhållbarhet
* KR-901 Källkod ska följa etablerade kodkonventioner för respektive språk (gofmt, ESLint).
* KR-902 Databasmigreringar ska hanteras med Flyway eller motsvarande migreringsverktyg (sqlc-kompatibelt).
* KR-903 Systemet ska vara fullt containeriserat och reproducerbart med ett enda docker compose up-kommando.
* KR-904 Miljökonfiguration ska hanteras via miljövariabler. Inga hårdkodade värden för host, port eller credentials.

## 6. Systemgränser
### 6.1 Ingår i systemet

* Webbgränssnitt för slutanvändare och IT-personal
* REST API (Java Springboot web)
* Ärendedatabas (PostgreSQL)
* LDAP-integration mot företagets AD
* E-postnotifieringar via SMTP
* Granskningslogg

### 6.2 Ingår inte i systemet

* Hantering av klassificerad eller sekretessbelagd information
* Integration med externa ärendesystem
* Mobilapplikation
* Automatiserad incidentdetektering

### 6.3 Externa beroenden
Beroende, Typ, Ansvar 

Active Directory, Autentisering, företags IT-infrastruktur, 

SMTP-server, E-post, företags IT-infrastruktur

Intern DNS, Namnupplösning, företags IT-infrastruktur

## 7. Tekniska krav
7.1 Backend
KR-T101 Backend ska implementeras i Java med springboot som HTTP-router.
KR-T102 Databasåtkomst ska ske via pgx och sqlc. ORM-ramverk ska inte användas.
KR-T103 LDAP-autentisering ska implementeras med go-ldap eller likvärdigt bibliotek.
KR-T104 API:et ska följa REST-konventioner med JSON som datautbytesformat.
KR-T105 Strukturerad loggning ska användas (slog eller zerolog). Loggformat ska vara JSON i produktion.
7.2 Frontend
KR-T201 Frontend ska implementeras i React med TypeScript.
KR-T202 Gränssnittet ska vara responsivt och fungera i moderna webbläsare (Chrome, Firefox, Edge, senaste två versioner).
KR-T203 Frontend ska kommunicera uteslutande via det interna REST API:et. Direkta databasanrop är inte tillåtna.
7.3 Databas
KR-T301 PostgreSQL version 15 eller senare ska användas.
KR-T302 Databasschema ska versionshanteras med migreringar. Manuella schemaändringar i produktion är inte tillåtna.
KR-T303 KänsligaClaude's response was interrupted.Try againYou said: ska vara java springboot i backed.ska vara java springboot i backed. och vi kan köra openldap8:01 AMClaude responded: Uppdaterad prompt:Uppdaterad prompt:

Skriv en detaljerad kravspecifikation på svenska för ett ITSM/helpdesk-system kallat Xelin. Systemet är byggt med Java Spring Boot-backend, React-frontend, PostgreSQL-databas, OpenLDAP-autentisering, och driftsätts med Docker Compose och Nginx. Målmiljön är FOI (Totalförsvarets forskningsinstitut). Inkludera funktionella krav, icke-funktionella krav, systemgränser, användarroller, och tekniska krav. Formatet ska följa svensk teknisk dokumentationsstandard.

Vill du även att jag uppdaterar den faktiska kravspecen med dessa ändringar, eller var prompten det du ville ha?You said: uppdaterauppdatera8:02 AMClaude responded: Kravspecifikation – Xelin ITSM-systemKravspecifikation – Xelin ITSM-system
Dokumentbeteckning: XELIN-KRAV-001

Version: 1.1

Datum: 2026-06-13

Författare: Benjamin Wasim

Status: Utkast

1. Inledning
1.1 Syfte
Detta dokument specificerar krav för Xelin, ett internt ITSM- och helpdesksystem avsett för driftsättning vid FOI (Totalförsvarets forskningsinstitut). Systemet ska hantera IT-supportärenden, användarförfrågningar och incidentrapportering inom organisationen.
1.2 Omfattning
Xelin omfattar ärendehantering, användarautentisering via OpenLDAP, rollbaserad åtkomstkontroll samt administrativt gränssnitt för IT-personal. Systemet är inte avsett att ersätta klassificerade system eller hantera sekretessbelagd information.
1.3 Definitioner
TermDefinitionÄrendeEn registrerad IT-supportförfrågan eller incidentHandläggareIT-personal med behörighet att hantera ärendenSlutanvändareFOI-anställd som skapar ärendenSLAService Level Agreement, överenskommen svarstidLDAPLightweight Directory Access ProtocolOpenLDAPÖppen katalogtjänst som används för autentisering

2. Systemöversikt
Xelin är en webbapplikation med tre-lagers arkitektur: React-frontend, Java Spring Boot-backend och PostgreSQL-databas. Autentisering sker mot OpenLDAP. Systemet driftsätts på intern infrastruktur med Docker Compose och exponeras via Nginx som omvänd proxy.

3. Användarroller
3.1 Slutanvändare (User)
Anställd vid FOI med tillgång till systemet via LDAP-konto. Kan skapa, följa och kommentera egna ärenden.
3.2 Handläggare (Agent)
IT-supportpersonal. Kan hantera alla ärenden, tilldela, eskalera och stänga ärenden. Tillgång till kövy och statistiköversikt.
3.3 Administratör (Admin)
IT-ansvarig. Full systembehörighet inklusive användarkonfiguration, SLA-inställningar, kategorier och granskningslogg.

4. Funktionella krav
4.1 Autentisering och behörighet
KR-101 Systemet ska autentisera användare mot OpenLDAP. Lokala användarkonton ska inte stödjas.
KR-102 Roller (User, Agent, Admin) ska härledas från LDAP-gruppmedlemskap och synkroniseras vid inloggning.
KR-103 Sessioner ska hanteras server-side via Spring Session. JWT ska inte användas.
KR-104 Inaktiva sessioner ska automatiskt ogiltigförklaras efter 60 minuters inaktivitet.
KR-105 Systemet ska logga samtliga inloggnings- och utloggningshändelser med tidsstämpel och användar-ID.
4.2 Ärendehantering
KR-201 Slutanvändare ska kunna skapa ärenden med titel, beskrivning, prioritet och kategori.
KR-202 Systemet ska automatiskt tilldela ett unikt ärende-ID vid skapande.
KR-203 Följande ärendestatusar ska stödjas: Ny, Tilldelad, Pågående, Väntande, Löst, Stängd.
KR-204 Handläggare ska kunna tilldela ärenden till sig själva eller annan handläggare.
KR-205 Handläggare ska kunna eskalera ärenden med motivering.
KR-206 Alla statusändringar ska loggas med tidsstämpel och ansvarig användare.
KR-207 Slutanvändare ska kunna följa status på egna ärenden i realtid.
KR-208 Handläggare och slutanvändare ska kunna lägga till kommentarer på ett ärende. Interna kommentarer ska endast vara synliga för handläggare och administratörer.
KR-209 Systemet ska stödja filbilagor upp till 10 MB per ärende.
4.3 Köhantering och tilldelning
KR-301 Handläggare ska ha tillgång till en samlad ärendekö filtrerad på status, prioritet och kategori.
KR-302 Systemet ska visa SLA-status per ärende med visuell indikering vid risk för överträdelse.
KR-303 Automatisk tilldelning baserad på kategori ska kunna konfigureras av administratör.
4.4 Notifieringar
KR-401 Systemet ska skicka e-postnotifiering till slutanvändaren vid statusändring på dennes ärende.
KR-402 Handläggare ska notifieras vid tilldelning av nytt ärende.
KR-403 Administratör ska notifieras vid SLA-överträdelse.
4.5 Rapportering och statistik
KR-501 Administratörer och handläggare ska ha tillgång till en statistikvy med ärendevolym, genomsnittlig lösningstid och SLA-uppfyllnad.
KR-502 Statistik ska kunna filtreras på tidsperiod och kategori.
KR-503 Granskningslogg ska vara tillgänglig för administratör och visa alla systemhändelser.

5. Icke-funktionella krav
5.1 Prestanda
KR-601 Svarstid för API-anrop ska understiga 300 ms under normal belastning (upp till 50 samtidiga användare).
KR-602 Gränssnittet ska vara fullt interaktivt inom 2 sekunder efter inloggning.
5.2 Tillgänglighet
KR-701 Systemet ska ha en tillgänglighet på minst 99 % under ordinarie arbetstid (07:00–18:00, måndag–fredag).
KR-702 Planerat underhåll ska kunna utföras utan förlust av ärendedata.
5.3 Säkerhet
KR-801 All kommunikation ska ske över HTTPS med TLS 1.2 eller senare.
KR-802 Systemet ska inte exponera interna felmeddelanden eller stackspår mot klienten.
KR-803 Samtliga databasfrågor ska hanteras via Spring Data JPA eller namngivna parametrar. SQL-injektion ska inte vara möjlig.
KR-804 Behörighetskontroll ska ske server-side via Spring Security för varje API-anrop.
KR-805 LDAP-autentiseringsuppgifter ska aldrig lagras i klartext eller loggas.
5.4 Underhållbarhet
KR-901 Källkod ska följa etablerade Java-kodkonventioner (Google Java Style Guide eller likvärdigt).
KR-902 Databasmigreringar ska hanteras med Flyway.
KR-903 Systemet ska vara fullt containeriserat och reproducerbart med ett enda docker compose up-kommando.
KR-904 Miljökonfiguration ska hanteras via Spring-profiler och miljövariabler. Inga hårdkodade värden för host, port eller credentials.

6. Systemgränser
6.1 Ingår i systemet

Webbgränssnitt för slutanvändare och IT-personal
REST API (Spring Boot)
Ärendedatabas (PostgreSQL)
OpenLDAP-integration för autentisering och rollhantering
E-postnotifieringar via SMTP (Spring Mail)
Granskningslogg

6.2 Ingår inte i systemet

Hantering av klassificerad eller sekretessbelagd information
Integration med externa ärendesystem
Mobilapplikation
Automatiserad incidentdetektering
Fakturering eller SLA-avtal mot externa parter

6.3 Externa beroenden
BeroendeTypAnsvarOpenLDAPAutentisering och katalogtjänstFOI IT-infrastrukturSMTP-serverE-postFOI IT-infrastrukturIntern DNSNamnupplösningFOI IT-infrastruktur

7. Tekniska krav
7.1 Backend
KR-T101 Backend ska implementeras i Java 21 med Spring Boot 3.x.
KR-T102 Autentisering och behörighetskontroll ska hanteras via Spring Security med Spring LDAP mot OpenLDAP.
KR-T103 Databasåtkomst ska ske via Spring Data JPA med Hibernate. Flyway används för schemamigrering.
KR-T104 API:et ska följa REST-konventioner med JSON som datautbytesformat.
KR-T105 Strukturerad loggning ska användas via SLF4J/Logback. Loggformat ska vara JSON i produktion.
KR-T106 Byggsystem ska vara Maven eller Gradle.
7.2 Frontend
KR-T201 Frontend ska implementeras i React med TypeScript.
KR-T202 Gränssnittet ska vara responsivt och fungera i moderna webbläsare (Chrome, Firefox, Edge, senaste två versioner).
KR-T203 Frontend ska kommunicera uteslutande via det interna REST API:et.
7.3 Databas
KR-T301 PostgreSQL version 15 eller senare ska användas.
KR-T302 Databasschema ska versionshanteras med Flyway. Manuella schemaändringar i produktion är inte tillåtna.
KR-T303 Känsliga fält ska inte lagras i klartext i databasen.
7.4 Infrastruktur
KR-T401 Systemet ska driftsättas med Docker Compose med separata tjänster för backend, frontend, databas och OpenLDAP.
KR-T402 Nginx ska användas som omvänd proxy och hantera TLS-terminering.
KR-T403 OpenLDAP ska köras som container inom samma Docker Compose-nätverk och inte exponeras externt.
KR-T404 Databasvolymer ska vara persistenta och överleva omstart av containers.

8. Kvalitetskrav och testning
KR-801 Kritiska servicelager ska täckas av enhetstester (JUnit 5, Mockito). Målsatt täckningsgrad: 70 %.
KR-802 API-endpoints ska täckas av integrationstester med Spring Boot Test och MockMvc.
KR-803 LDAP-integration ska testas mot en lokal OpenLDAP-container i CI-miljö.

Dokumentet är ett levande utkast och ska revideras i takt med att systemet utvecklas.