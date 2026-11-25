# Music Rating App

Aplikacja do oceny wydań muzycznych, czyli albumów, singli i kompilacji.

Użytkownicy wystawiają wydaniom oceny w skali 1–10 z opcjonalną recenzją. Mogą też prowadzić własną kolekcję nośników i tworzyć listy wydań. System na podstawie ocen generuje rankingi roczne i gatunkowe. Aplikacja udostępnia REST API i prosty interfejs w przeglądarce.

## Technologie

| Warstwa | Technologia |
|---|---|
| Język | Java 17 |
| Framework | Spring Boot 3.3 (Web, Data JPA, Validation) |
| ORM | Hibernate 6 z dialektem `SQLiteDialect` (`hibernate-community-dialects`) |
| Baza danych | SQLite (`sqlite-jdbc`) |
| Frontend | HTML + JavaScript (bez frameworka) |
| Testy | JUnit 5, AssertJ, Mockito, Spring Boot Test |
| Build | Maven |

## Uruchomienie

Wymagania: JDK 17 lub nowszy oraz Maven. Projekt nie ma wrappera `mvnw`, więc potrzebny jest Maven zainstalowany w systemie albo ten wbudowany w IntelliJ IDEA.

Wszystkie polecenia uruchamiaj z katalogu głównego projektu:

```bash
mvn spring-boot:run
```

Po starcie:

- GUI jest dostępne pod adresem http://localhost:8080/index.html,
- API pod adresem http://localhost:8080/api/releases.

Inne polecenia:

```bash
mvn clean package
mvn test
mvn test -Dtest=UserTest
mvn test -Dtest=UserTest#rateReleaseRecalculatesAverageScore
```

Pierwsze buduje plik JAR, drugie uruchamia wszystkie testy, a dwa ostatnie pojedynczą klasę testową i pojedynczy test.

## Baza danych

Dane są w pliku `music_database.db` w katalogu głównym projektu. Ścieżka jest względna, dlatego aplikację trzeba uruchamiać z tego katalogu. W przeciwnym razie powstanie nowa, pusta baza w innym miejscu.

- Schemat tworzy i aktualizuje Hibernate (`spring.jpa.hibernate.ddl-auto=update`). Usunięcie pola z encji nie usuwa kolumny z bazy.
- Przy starcie komponent `DataLoader` wypełnia pustą bazę danymi przykładowymi: 4 użytkowników, gatunek Rock, zespół The Beatles z 15 wydaniami, 4 oceny i 2 rankingi (rok 1969 i gatunek Rock). Jeśli baza zawiera już jakiekolwiek wydania, nic nie jest dodawane.
- Żeby zacząć od czystych danych, usuń plik `music_database.db`. Przy kolejnym uruchomieniu powstanie na nowo.

Plik bazy jest ignorowany przez Git.

## REST API

| Metoda | Ścieżka | Opis | Odpowiedź |
|---|---|---|---|
| `GET` | `/api/releases` | Lista wszystkich wydań | `200` z tablicą `{ id, title, averageRating }` |
| `GET` | `/api/releases/{id}/ratings` | Oceny wybranego wydania | `200` z tablicą `{ score, review, username, date }`. `404`, gdy wydanie nie istnieje |

Przykład:

```bash
curl http://localhost:8080/api/releases/1/ratings
```

```json
[
  { "score": 10, "review": "nicee", "username": "Maciej1", "date": "2026-11-25" },
  { "score": 8,  "review": "aoao",  "username": "Michal2", "date": "2026-11-25" }
]
```

## Interfejs użytkownika

Plik `src/main/resources/static/index.html` to pojedyncza strona serwowana przez Spring Boot jako zasób statyczny. Pokazuje listę wydań ze średnimi ocenami. Po kliknięciu wydania wyświetla jego oceny i recenzje, pobierając je z API. Adres API jest wpisany na stałe jako `http://localhost:8080`.

## Architektura

Kod w pakiecie `pl.s30953.mra` jest podzielony na warstwy:

```
controllers   → REST API (ReleaseController)
services      → logika biznesowa i mapowanie encji na DTO (ReleaseService, RankingService)
repositories  → dostęp do bazy przez Spring Data JPA, z zapytaniami JPQL dla rankingów
models        → encje JPA i typy wyliczeniowe
dtos          → rekordy zwracane przez API (ReleaseDto, RatingDto)
bootstrap     → ładowanie danych przykładowych (DataLoader)
```

API nie zwraca encji bezpośrednio, tylko obiekty DTO. Encje mają asocjacje dwukierunkowe, które przy serializacji do JSON tworzyłyby pętle.

## Model danych

Główne encje: `User`, `Release`, `Rating`, `Artist` (`Band`, `SoloArtist`), `Genre`, `Song`, `CollectionItem`, `UsersList`, `Ranking` (`YearRanking`, `GenreRanking`).

### Rankingi

`RankingService` wybiera wydania przez zapytania z `ReleaseRepository`:

- **ranking roczny** obejmuje wydania z danego roku kalendarzowego,
- **ranking gatunkowy** obejmuje wydania danego gatunku.

Do rankingu trafiają tylko wydania o średniej co najmniej `minimumScore` (domyślnie 5.0) i z liczbą ocen co najmniej `minimumOfRatings` (domyślnie 1). Wyniki są posortowane malejąco po średniej. Oba progi są polami rankingu i są zapisywane w bazie.

### Spójność asocjacji

Metody domenowe pilnują obu stron powiązań:

- `User.rateRelease(...)` dodaje ocenę do użytkownika i do wydania, a potem przelicza średnią,
- `User.deleteRating(...)` odpina ocenę od obu stron i przelicza średnią,
- `User.addToCollection(...)`, `User.createList(...)` i metody `UsersList` zarządzają kolekcją i listami.

Encje `Release`, `Rating` i `Ranking` są porównywane po identyfikatorze z bazy. Dzięki temu dwie instancje tego samego wiersza są traktowane jako ten sam obiekt.

## Testy

```bash
mvn test
```

| Pakiet | Rodzaj | Zakres |
|---|---|---|
| `models` | testy jednostkowe | logika encji: średnie, wiek, oceny, kolekcja, listy, `equals` |
| `services` | Mockito | mapowanie na DTO, generowanie rankingów i przekazywanie progów |
| `controllers` | `@WebMvcTest` | format JSON i kody odpowiedzi HTTP |
| `repositories` | `@DataJpaTest` na SQLite | zapytania rankingowe: zakres dat, progi, sortowanie |
| główny pakiet | `@SpringBootTest` | cała aplikacja z danymi z `DataLoader` i oba endpointy |

Testy bazodanowe używają profilu `test` (`src/test/resources/application-test.properties`). Każdy kontekst ma osobny plik SQLite w katalogu `target/`, więc testy nie zmieniają bazy `music_database.db`.
