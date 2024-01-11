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



## Zmiany 

TSL jest cały czas rozwijany, chciałbym aby opisane powyżej funkcjonalności były tylko początkiem rozwoju tej aplikacji.
W najbliższym czasie chciałbym dodać:
- symulacja czatu między pracownikami firmy
- możliwość tworzenia raportów ( np. rentowność danego pracownika )
- limity salda dla klienta
- połączenie z API NBP w celu pobierania kursów walut ( dla klientów zagranicznych, aby ułatwić obliczanie wartości ładunku/faktury jeżeli jest w obcej walucie )
- zaimplementowanie możliwości obliczania tzw. SKONTA, czyli szybszej płatności faktury,
- modyfikacja bazy danych o nową tabele - wypłaty pracowników
- poprawki w testach



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
Wyświetlanie wszystkich zlecen dostępne jest pod adresem:


```http
GET http://localhost:8080/forwarding-orders
```

Co istotne, spedytor widzi <b>tylko swoje zlecenia.</b>

Pozostałe możliwości dostępne w sekcji spedycji to np:

- edycja zlecenia spedycyjnego
```http
PATCH http://localhost:8080/forwarding-orders/{id}
```
- anunlacja zlecenia spedycynego
```http
PATCH http://localhost:8080/forwarding-orders/{id}/cancel
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

W miejsce <b>{id}</b> wskazać należy id danego zasobu. Edycje elementów wiążą się z pewnymi ograniczeniami i zabezpieczeniami, które wprowadziłem i które poznasz podczas testowania aplikacj.
Przeszliśmy tym samym przez sekcje spedycji i przejść możemy do sekcji transportu. Jest to część aplikacji, która będzie obsługiwała funkcjonalności związane z transportem własnym taborem. 

W pierwszej kolejności należy przelogować sie na konto planisty transportowego, gdyż spedytor nie ma dostępu do tej sekcji. 
Analogicznie do sytuacji na samym początku, możesz jako administrator zarejestrować nowego planiste.
Nim to jednak zrobisz powinieneś dodać nową ciężarówkę która będzie przypisana do danego parocwnika. 
Oto jak to zrobic: 


```http
POST http://localhost:8080/admin/trucks
```
```
{
        "brand": "Test3",
        "model": "Test3",
        "type": "TAUTLINER",
        "plates": "ABC123/CBA421",
        "technicalInspectionDate": "2024-02-01",
        "insuranceDate": "2024-12-31"
}
```

```http
POST http://localhost:8080/admin/planners/register
```
```
{
    "firstName": "Nowy planner",
    "lastName": "Test",
    "email": "test@example.com",
    "password": "hard",
    "telephone": "+123456789",
    "addressId": 3,
    "basicGrossSalary": 9000.00,
    "dateOfEmployment": "2023-01-01",
    "formOfEmployment": "CONTRACT_EMPLOYMENT_FULL_TIME",
    "contractExpiryDate": "2024-01-01",
    "truckIds": [1]
}
```

Zasada pracy planisy jest podoba do pracy spedytora. Również przyjmujemy ładunki od klientów. Zatem możesz wykorzystać zasoby które utowrzyłes wcześniej lub wrócić i ponownie dodać nowego klienta i nowe ładunki. Pracownik ten jednak tworzy zlecenie które jest realizowane przez firmową cieżarówkę. 

Dane które mogą być przydatne przy utworzeniu takiego zlecenia to np. dostępni kierowcy czy ciężarówki


```http
GET http://localhost:8080/drivers
```
```http
GET http://localhost:8080/trucks
```
```http
GET http://localhost:8080/transport-orders
```

Podobnie jak wcześniej możesz również sortować wyniki.
Utworzenie nowego zlecenia dostępne jest pod adresem:


```http
POST http://localhost:8080/transport-orders
```

Przykładowe zlecenie:

```
    {
        "orderNumber": "TSL001",
        "cargoId": 2,
        "price": 2000.00,
        "currency": "PLN",
        "transportPlannerId": 7,
        "truckId": 1
    }
```

Pamiętaj aby ładunek był wolny - nie przypisany do innych zleceń, aby waluta zgadzała się z walutą ładunku oraz aby ciężarówka miała przypisanego kierowce i miała aktualne badania techniczne i ubezpieczenie. 
Pozostałe istotne możliwości to:


- edycja zlecenia transportowego
```http
PATCH http://localhost:8080/transport-orders/{id}
```
- anunlacja zlecenia transportowego
```http
PATCH http://localhost:8080/transport-orders/{id}/cancel
```


Przechodzimy teraz do sekcji magazynowania. 
Jako administrator możemy podobnie jak poprzednio dodawać pracowników, w tym wypadku będą to pracownicy magazynu. 
Wszystkie dostępne funkcjonalności możemy tu realizowac jako administrator, spedytor lub planista, a więc nie ma znaczenia na którym koncie jestesmy aktualnie zalogowani.

```http
POST http://localhost:8080/admin/warehouse-workers/register
```


```
{
        "firstName": "Worker",
        "lastName": "Worker",
        "telephone": "+123456789",
        "addressId": 10,
        "basicGrossSalary": 3000.00,
        "dateOfEmployment": "2023-01-01",
        "formOfEmployment": "CONTRACT_EMPLOYMENT_FULL_TIME",
        "contractExpiryDate": "2024-01-01",
        "warehouseId": 1,
        "permissionsForklift": true,
        "permissionsCrane": false

}
```

Wyświetlenie wszystkich pracowników odbywa się poprzez wyłasnie żądania:

```http
GET http://localhost:8080/admin/warehouse-workers
```


Aby zacząć swiadczyć usługi magazynowania, oprócz pracowników należy dodać przede wszystkim magazyn, ta opcja również dostępna jest tylko do administratora:


```http
POST http://localhost:8080/warehouses
```

```
{
    "typeOfGoods": "ADR_GOODS",
    "addressId": 7,
    "crane": true,
    "forklift": true,
    "costPer100SquareMeters": 220.0,
    "availableArea": 18000.0
}
```


Wyświetlanie, edycja i usuwanie magazynów:
```http
GET http://localhost:8080/warehouses
```
```http
PATCH http://localhost:8080/warehouses/{id}
```
```http
DELETE http://localhost:8080/warehouses/{id}
```

W tym momencie możemy przyjmować pierwsze towary. Aby to zrobić wysyłamy żądanie
```http
POST http://localhost:8080/warehouses/goods
```


Tworzymy na podstawie tego zlecenie magazynowe. Należy pamiętać że dane zlecenie magazynowe może posiadać tylko towary tego samego typu i o unikalnych etykietach. 

```http
POST http://localhost:8080/warehouses/orders
```


Pozostałe przypdate żądania z tej sekcji to np.

- wyświetlanie nie przypisanych do żadnego zlecenia towarów
```http
GET http://localhost:8080/warehouses/goods/not-assigned
```
-edycja towaru
```http
PATCH http://localhost:8080/warehouses/goods/{id}
```
-usunięcie towaru
```http
PATCH http://localhost:8080/warehouses/goods/{id}
```
- wyświetlenie wszystkich zleceń
```http
GET http://localhost:8080/warehouses/orders
```
- wyświetlenie zlecenie nie zakończonych
```http
GET http://localhost:8080/warehouses/orders/not-completed
```
- zakończenie zlecenia
```http
PATCH http://localhost:8080/warehouses/orders/complete/{id}
```
- edycja zlecenia
```http
PATCH http://localhost:8080/warehouses/orders/{id}
```

Przebrneliśmy przez trzy bardzo ważne sekcje w branży TSL. Teraz pora na równie istotny element czyli księgowość. 
W przypadku księgowości możesz korzystać z konta administratora lub zalogowac się na przygotowane przeze mnie konto księgowej, oraz oczwiście mozesz zarejestrować nowego pracownika.


Dodanie pracownika
```http
POST http://localhost:8080/admin/accountants/register
```

Przykładowe dane:

```
{
    "firstName": "Księgowa",
    "lastName": "Test",
    "email": "ksiegowosc@example.com",
    "password": "hard",
    "telephone": "+123456789",
    "addressId": 10,
    "basicGrossSalary": 2500.00,
    "dateOfEmployment": "2023-01-01",
    "formOfEmployment": "CONTRACT_EMPLOYMENT_FULL_TIME",
    "contractExpiryDate": "2024-01-01",
    "typeOfAccounting": "INVOICES"
}
```

Gotowe konto księgowej:

```
{
    "email": "acc@example.com",
    "password": "hard"
}
```

Lub tak jak wspomniałem wyżej zostań na koncie administratora. 
W tej sekcji dodajemy faktury od przewoźników oraz tworzymy faktury dla klientów. Vat Calculator oblicza za nas wartości faktur w zależności od tego czy klient jest z Polski czy zza granicy.
Osobna klasa oblicza termin płatności. 

Poniżej tylko przykład dodawania faktury dla klienta

```http
POST http://localhost:8080/invoices/customer
```


```
{
    "invoiceNumber": "Numer",
    "cargoId": 1,
    "customerId": 1
}
```

Wysyłając odpowiednie żądania możemy wyświetlać faktury, sortować, edytować, oznaczać jako zapłacone, co jest równoznaczne ze zmianą salda klienta. 
Przykładowe żądania:
- wyświetlenie faktur od przewoźników

  
```http
GET http://localhost:8080/invoices/carrier
```
- wyświetlenie faktur dla klientów
```http
GET http://localhost:8080/invoices/customer
```

Oznaczanie faktur jako opłacone odbywa się poprzez żądanie
```http
PATCH http://localhost:8080/invoices/customer/{id}/paid
```
```http
PATCH http://localhost:8080/invoices/carrier/{id}/paid
```

Anaglogicznie możemy postępowac z fakturami za zlecenia magazynowe. 

Jako administrator mamy równiez możliwośc obliczenia wypłaty danego pracownika. W przypadku spedytorów i planistów wypłata to podstawa + prowizja, dla każdego z nich obliczana inaczej. 
Spedytor dostaje premie w zależności od sumy marzy jaką uzyska w danym miesiącu da którego obliczamy pensje. Planista natomiast dostaje premie w zależności od przypisanych do niego ciężarówek.
Obliczenia możemy wykonać wysyłając zadanie pod adres:


```http
GET http://localhost:8080/salary/{id}
```

Gdzie {id} to id danego pracownika. 

Dostępna jest jeszcze opcja formularza kontaktowego. 
Na potrzeby tej funkcjonalności został utworzony adres e-mail na który przychodzą wysłane przez formularz wiadomości. 


```http
POST http://localhost:8080/contact/send
```

```
{
    "name": "John Doe",
    "subject": "Test",
    "email": "john.doe@example.com",
    "message": "Test."
}
```


____________
<b>Dziękuje za dotrwanie do końca i życzę udanego testowania aplikacji</b> 👋
____________





## Autor

#### Maciej Jankowski
#### Linkedin
[![Linkedin](https://img.shields.io/badge/LinkedIn-0A66C2.svg?style=for-the-badge&logo=LinkedIn&logoColor=white)](https://www.linkedin.com/in/maciej-jankowskii/)




## EN

