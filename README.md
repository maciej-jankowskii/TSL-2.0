# TSL 2.0

1. [English version below](#en)
2. [Opis](#opis)
3. [Zmiany](#zmiany)
4. [Instalacja](#instalacja)
5. [Działanie](#dzialanie)
6. [Autor](#autor)

## Opis

Przy tworzeniu tej aplikacji wykorzystałem swoje doświadczenie zawodowe. Od ponad 8 lat pracuje w branży transportowej. 
Bardzo chciałem stworzyć aplikacje, która mogłaby śmiało posłużyć mi na codzień w pracy i ułatwić codzienne obowiązki.

TSL dzieli się na kilka sekcji. Sekcja administratora pozwala rejestrować nowych pracowników firmy, takich jak:
- spedytorzy,
- planiści transportowi,
- pracownicy księgowości,
- kierowcy,
- pracownicy magazynów.

Możemy dodawać nowe firmowe ciężarówki i przypisywać do nich kierowców. Możemy również sprawdzić więcej informacji o kontrahentach, takich jak np. ich aktualne saldo.
Administrator ma możliwość obliczenia wypłaty pracownika w danym miesiącu. 
Spedytor i planisa oprócz pensji podstawowej otrzymują premie:
- spedytor -> premia zależna od wysokości marży jaką uzyska
- planista -> premia zależna od liczy przypisanych firmowych ciężarówek

Druga sekcja to sekcja magazynowa. Jako pracownicy możemy dodawać nowe magazyny, towary, oraz na ich podstawie tworzyć zlecenia magazynowe. 
Każy magazyn ma określony typ, powierzchnie i koszty składowania towaru. Towary natomiast mają swoje unikalne etykiety, bazując na nich dodajemy zlecenia. 

Kolejna sekcja to sekcja spedycji. Tutaj mamy możliwość dodawać klientów którzy zlecają nam transport towaru, możemy dodawać ładunki oraz przewoźników którzy będą realizować transport.
Bazując na tym wszystkim tworzymy zlecenie spedycyjne. Każde zlecenie ma określoną marże która jest przypisywana do spedytora.

Sekcja transportu działa bardzo podobnie, lecz pomija etap przewoźnika, z racji tego że transport odbywa się własnym taborem. 

Sekcja księgowości to przede wszystkim faktury od przeowoźników, faktury dla klientów za ładunki oraz zlecenia magazynowe.

Opcja dostępna dla każdego to możliwość zostawienia wiadomości dla firmy, jest to symulacja formularza kontaktowego. 

...

## Zmiany 

TSL jest cały czas rozwijany, chciałbym aby opisane powyżej funkcjonalności były tylko początkiem rozwoju tej aplikacji.
W najbliższym czasie chciałbym dodać:
- symulacja czatu między pracownikami firmy
- możliwość tworzenia raportów ( np. rentowność danego pracownika )
- limity salda dla klienta
- połączenie z API NBP w celu pobierania kursów walut ( dla klientów zagranicznych, aby ułatwić obliczanie wartości ładunku/faktury jeżeli jest w obcej walucie )
- zaimplementowanie możliwości obliczania tzw. SKONTA, czyli szybszej płatności faktury,
- modyfikacja bazy danych o nową tabele - wypłaty pracowników 

...

## Instalacja

W pierwszej kolejności należy sklonować repozytorium: 

```bash
  git clone https://github.com/maciej-jankowskii/TSL-2.0
```
Należy pamiętać o utworzeniiu bazy danych lokalnie na swoim komputerze, i odpowiednim skonfigurowaniu pliku application.yml

Do łatwiejszego testowania aplikacji polecam pobranie Postmana. 
Można to zrobić z oficjalnej strony: 

```bash
  https://www.postman.com/
```

## Działanie

Aby rozpocząć testowanie aplikacji należy w pierwszej kolejności się zalogować. Możemy to zrobić jako administrator i następnie zarejestrować nowego pracownika lub zalogować się od razu na przygotowane przeze mnie dane konkretnych pracowników. Na potrzeby testów przeprowadze Cię przez dwie opisane wyżej drogi. Zaczniemy od testowania sekcji spedycji. 

Uruchom program Postman oraz wyślij żądanie <b>POST</b> pod wsakzany adres. Użyj do tego przygotowanych przeze mnie danych, jest to konto administratora, szczegóły poniżej:

```http
POST http://localhost:8080/auth/login
```
```
{
    "email": "john@example.com",
    "password": "hard"
}
```

W odpowiedzi apliakcja wygeneruje dla Ciebie <b>token</b>, który będzie Ci potrzebny do dalszego działania. Pamiętaj aby użwać tego tokena z każdym kolejnym wysłanym żądaniem. Więcej informacji jak to zrobić znajdziesz poniżej. 
Teraz możesz zarejestrować nowe konto pracownika na którym będziesz dalej operował. 

Przygotuj nowe żądanie:

```http
POST http://localhost:8080/admin/forwarders/register
```
W sekcji Body, przykładowe dane które możesz dodać:

```
{
    "firstName": "Imie",
    "lastName": "Nazwisko",
    "email": "test@example.com",
    "password": "hard",
    "telephone": "+123456789",
    "addressId": 1,
    "basicGrossSalary": 5000.00,
    "dateOfEmployment": "2023-01-01",
    "formOfEmployment": "CONTRACT_EMPLOYMENT_FULL_TIME",
    "contractExpiryDate": "2024-01-01",
    "extraPercentage": 10.0
}
```

Pamiętaj aby wcześniej przekazać token. Screen poniżej:


<img src="https://github.com/maciej-jankowskii/TSL-2.0/blob/85d690b017aa0a92b8fd438a3c54b4f735f26eba/src/main/resources/static/auth.png" alt="project-screenshot" width="760" height="320/">

<b>Token musisz przekazywać praktycznie z każdym żądaniem z wyjątkiem logowania i formularza kontaktowego o którym będzie mowa później we wskazany wyżej sposób.</b>

Jeżeli nie chcesz rejestrować nowego spedytora, zaloguj się na dane które przygotowałem wcześniej. 

```http
POST http://localhost:8080/auth/login
```
```
{
    "email": "forw1@example.com",
    "password": "hard"
}
```

Możemy teraz zacząć sprawdzać funkcjonalności jakie daje nam sekcja spedycji. Każdy spedytor w pierwszej kolejności musi dodać klienta z którym będzie współpracował i który to będzie zlecał nam transporty. Aby to zrobić postępuj zgodnie ze wskazówkami. 
Klient oprócz danych podstawowych ma swój adres oraz osobe kontaktową. Dodajmy zatem niezbędne dane. 

```http
POST http://localhost:8080/addresses
```
```
{
    "country": "Kraj",
    "postalCode": "00-000",
    "city": "Miasto",
    "street": "ul. Testowa",
    "homeNo": "10",
    "flatNo": "10"
}
```

```http
POST http://localhost:8080/contact-person
```

```
{
    "firstName": "Jan",
    "lastName": "Kowlski",
    "email": "kowalski@test.pl",
    "telephone": "775341222"
}
```

Jeżeli zmienisz typ żądania na <b>GET</b> to wysyłając go dokładnie pod ten sam adres otrzymasz aktualną liste adresów oraz osób kontaktowych.
Dodawanie klienta

```http
POST http://localhost:8080/customers
```

Przykładowe dane:
```
{
    "fullName": "Spedition",
    "shortName": "SPED",
    "addressId": 1,
    "vatNumber": "PL66533567890",
    "description": "Opis klienta",
    "termOfPayment": 30,
    "contactPersonIds": [1]
}
```

Pamiętaj o przekazywaniu tokena w sekcji <b>AUTHORIZATION</b>.
W odpowiedzi ponieneś otrzymać teraz kod 201 Created. W ten sposób utworzyłeś klienta z którym będziesz współpracował. 
Klient zleca nam transport i wysyła zlecenie z konkretnym towarem. Utwórzmy więc nowy ładunek.

```http
POST http://localhost:8080/cargos
```

```
{
    "cargoNumber": "Numer ładunku",
    "price": "3000",
    "currency": "PLN",
    "loadingDate": "2024-02-01",
    "unloadingDate" : "2024-02-05",
    "loadingAddress": "Szczecin, ul. Nowa, Polska",
    "unloadingAddress": "Warszawa, ul. Stara, Polska",
    "goods": "Neutralny",
    "description": "Test",
    "customerId": 1
}
```

Utworzyliśmy właśnie nowy ładunek. W tym momencie saldo klienta zmieniło się o wskazaną cene + VAT jeżeli to klient z Polski (jego numer VAT rozpoczyna się od PL). 
Wysyłając dokładnie te same żądania tylko metodą GET otrzymamy liste klientów i liste ładunków. 
Jeżeli w naszej bazie ładunków będzie więcej możemy je sortować lub wyślietlać tylko te które nie są jeszcze przypisane do żadnego zlecenia spedycyjnego.

```http
GET http://localhost:8080/cargos
```
```http
GET http://localhost:8080/cargos/not-assigned
```
```http
GET http://localhost:8080/cargos/not-invoiced
```
```http
GET http://localhost:8080/cargos/sorted?sortBy=
```

Pamiętaj aby po znaku "=" dodać na podstawie czego będziemy sortować ładunek (np. numer ładunku, cena, data dodania, daty załadunku i rozładunku itd)

Mamy ładunek, zatem dodać należy teraz przewoźnika, który zrealizuje transport. 

```http
POST http://localhost:8080/carriers
```

```
{
    "fullName": "Przewoźnik",
    "shortName": "PK",
    "addressId": 2,
    "vatNumber": "PL99034567890",
    "description": "Opis klienta",
    "termOfPayment": 60,
    "insuranceExpirationDate": "2024-12-31",
    "licenceExpirationDate": "2024-12-31",
    "contactPersonIds": [2]
}
```

Pamiętaj aby daty ważności licencji i ubezpieczenia przewoźnika były aktualne, w innym wypadku nie uda nam się przypisać takiego przewoźnika do zlecenia które za chwile będziemy tworzyć. 
Podobnie jak wcześniej, możemy wyświetlać i sortować przewoźników:
```http
GET http://localhost:8080/carriers
```
```http
GET http://localhost:8080/carriers/sorted?sortBy=
```

Pora na utworzenie zlecenia spedycyjnego.

```http
POST http://localhost:8080/forwarding-orders
```

```
{
    "orderNumber": "Numer",
    "cargoId": "1",
    "price": 2500,
    "currency": "PLN",
    "carrierId": 1 ,
    "typeOfTruck": "TAUTLINER",
    "truckNumbers": "OK2432/OK7313"
}
```

W ten sposób utworzyliśmy zlecenie, zarabiając różnice pomiedzy ceną ładunku a ceną zlecenia, i przypisując tą różnice do konta danego spedytora. Ułatwi nam to w dalszych krokach obliczanie całkowitej wypłaty pracownika. 

Pozostałe możliwości dostępne w sekcji spedycji to np:
- edycja zlecenia spedycyjnego
```http
PATCH http://localhost:8080/forwarding-orders/{id}
```
- edycja ładunku
```http
PATCH http://localhost:8080/cargos/{id}
```
- usunięcie ładunku
```http
DELETE http://localhost:8080/cargos/{id}
```
- edycja klienta
```http
PATCH http://localhost:8080/customers/{id}
```
- edycja przewoźnika
```http
PATCH http://localhost:8080/carriers/{id}
```

W miejsce {id} wskazać należy id danego zasobu. Edycje elementów wiążą się z pewnymi ograniczeniami i zabezpieczeniami, które wprowadziłem i które poznasz podczas testowania aplikacj.
Przeszliśmy tym samym przez sekcje spedycji i przejść możemy do sekcji transportu. Jest to część aplikacji, która będzie obsługiwała funkcjonalności związane z transportem własnym taborem. 












...




## Autor



...

## EN

