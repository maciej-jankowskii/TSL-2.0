# TSL 2.0

1. [English version below](#en)
2. [Opis](#opis)
3. [Zmiany](#zmiany)
4. [Instalacja](#instalacja)
5. [Działanie](#działanie)
6. [Autor](#autor)

## Opis

Przy tworzeniu tej aplikacji wykorzystałem swoje doświadczenie zawodowe. Od ponad 8 lat pracuje w branży transportowej. 
Bardzo chciałem stworzyć aplikacje, która mogłaby śmiało posłużyć mi na co dzień w pracy i ułatwić codzienne obowiązki.

TSL dzieli się na kilka sekcji. Sekcja <b>administratora</b> pozwala rejestrować nowych pracowników firmy, takich jak:
- spedytorzy,
- planiści transportowi,
- pracownicy księgowości,
- kierowcy,
- pracownicy magazynów.

Możemy dodawać nowe firmowe ciężarówki i przypisywać do nich kierowców. Możemy również sprawdzić więcej informacji niedostępnych dla pracowników o kontrahentach, takich jak np. ich aktualne saldo.
Administrator ma możliwość również obliczenia wypłaty pracownika w danym miesiącu. 

Druga sekcja to sekcja <b>magazynowa.</b> Jako pracownicy możemy dodawać nowe magazyny, towary, oraz na ich podstawie tworzyć zlecenia magazynowe. 
Każdy magazyn ma określony typ, powierzchnie i koszty składowania towaru. Towary natomiast mają swoje unikalne etykiety, bazując na nich dodajemy zlecenia. 

Kolejna sekcja to sekcja <b>spedycji.</b> Tutaj mamy możliwość dodawać klientów którzy zlecają nam transport towaru, możemy dodawać ładunki oraz przewoźników którzy będą realizować transport.
Bazując na tym wszystkim tworzymy zlecenie spedycyjne. Każde zlecenie ma określoną marże która jest przypisywana do spedytora.

Sekcja <b>transportu</b> działa bardzo podobnie, lecz pomija etap przewoźnika, z racji tego że transport odbywa się własnym taborem. 

Sekcja <b>księgowości</b> to przede wszystkim faktury od przewoźników, faktury dla klientów za ładunki oraz zlecenia magazynowe.

Opcja dostępna dla każdego to możliwość zostawienia wiadomości dla firmy, jest to symulacja formularza kontaktowego. 


W aplikacji użyłem przede wszystkim: Java, Spring Boot, Spring Data, Spring Security, MySQL, Liquibase, Hibernate, Lombok. Do testów użyłem jUnit i Mockito. 
TSL jest zabezpieczone przy użyciu tokena JWT. 



## Zmiany 

TSL jest cały czas rozwijany, chciałbym aby opisane powyżej funkcjonalności były tylko początkiem rozwoju tej aplikacji.
W najbliższym czasie chciałbym dodać przede wszystkim:

- symulacje czatu między pracownikami firmy
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
Należy pamiętać o utworzeniu bazy danych lokalnie na swoim komputerze, i odpowiednim skonfigurowaniu pliku application.yml
Do łatwiejszego testowania aplikacji polecam pobranie Postmana. 
Można to zrobić z oficjalnej strony: 

```bash
  https://www.postman.com/
```


## Działanie


Aby rozpocząć testowanie aplikacji należy w pierwszej kolejności się zalogować. Możemy to zrobić jako administrator i następnie zarejestrować nowego pracownika lub zalogować się od razu na przygotowane przeze mnie dane konkretnych pracowników. Na potrzeby testów przeprowadzę Cię przez dwie opisane wyżej drogi. Zaczniemy od testowania sekcji spedycji. 

Uruchom program Postman oraz wyślij żądanie <b>POST</b> pod wskazany adres. Użyj do tego przygotowanych przeze mnie danych, jest to konto administratora, szczegóły poniżej:

```http
POST http://localhost:8080/auth/login
```
```
{
    "email": "john@example.com",
    "password": "hard"
}
```

W odpowiedzi aplikacja wygeneruje dla Ciebie <b>token</b>, który będzie Ci potrzebny do dalszego działania. Pamiętaj aby używać tego tokena z każdym kolejnym wysłanym żądaniem. Więcej informacji jak to zrobić znajdziesz poniżej. 
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
}
```

Pamiętaj aby wcześniej przekazać token. Screen poniżej:


<img src="https://github.com/maciej-jankowskii/TSL-2.0/blob/85d690b017aa0a92b8fd438a3c54b4f735f26eba/src/main/resources/static/auth.png" alt="project-screenshot" width="760" height="320/">

<b>Token musisz przekazywać praktycznie z każdym żądaniem z wyjątkiem logowania i formularza kontaktowego we wskazany wyżej sposób.</b>

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
Klient oprócz danych podstawowych ma swój adres oraz osobę kontaktową. Dodajmy zatem niezbędne dane. 

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
    "firstName": "John",
    "lastName": "Doe",
    "email": "doe@example.pl",
    "telephone": "775341222"
}
```

Jeżeli zmienisz typ żądania na <b>GET</b> to wysyłając go dokładnie pod ten sam adres otrzymasz aktualną liste adresów oraz osób kontaktowych.
Dodawanie klienta:

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
W odpowiedzi powinieneś otrzymać teraz kod 201 Created. W ten sposób utworzyłeś klienta z którym będziesz współpracował. 
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


Utworzyliśmy właśnie nowy ładunek. W tym momencie saldo klienta zmieniło się o wskazaną cene + VAT jeżeli to klient z Polski. 
Wysyłając dokładnie te same żądania tylko metodą GET otrzymamy listę klientów i listę ładunków. 
Jeżeli w naszej bazie ładunków będzie więcej możemy je sortować lub wyświetlać tylko te które nie są jeszcze przypisane do żadnego zlecenia spedycyjnego.


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

W ten sposób utworzyliśmy zlecenie, zarabiając różnice pomiędzy ceną ładunku a ceną zlecenia, i przypisując tą różnice do konta danego spedytora. Ułatwi nam to w dalszych krokach obliczanie całkowitej wypłaty pracownika. 
Wyświetlanie wszystkich zleceń dostępne jest pod adresem:


```http
GET http://localhost:8080/forwarding-orders
```

Co istotne, spedytor widzi <b>tylko swoje zlecenia.</b>

Pozostałe możliwości dostępne w sekcji spedycji to np:

- edycja zlecenia spedycyjnego
```http
PATCH http://localhost:8080/forwarding-orders/{id}
```
- anulacja zlecenia spedycyjnego
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

W miejsce <b>{id}</b> wskazać należy id danego zasobu. Edycje elementów wiążą się z pewnymi ograniczeniami i zabezpieczeniami, które wprowadziłem i które poznasz podczas testowania aplikacji.
Przeszliśmy tym samym przez sekcje spedycji i przejść możemy do sekcji transportu. Jest to część aplikacji, która będzie obsługiwała funkcjonalności związane z transportem własnym taborem. 

W pierwszej kolejności należy przelogować się na konto planisty transportowego, gdyż spedytor nie ma dostępu do tej sekcji. 
Analogicznie do sytuacji na samym początku, możesz jako administrator zarejestrować nowego planistę.


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
    "truckIds": [0]
}
```

Jeżeli chcesz od razu do planisty przypisać ciężarówke, dodaj ją, a następnie przy rejestracji pracownika w polu 'truckIds' wpisz ID ciężarówki.
Dodawanie auta możemy zrobić w następujący sposób:


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

Pamiętaj że zlecenia można realizować autami które mają przypisanych kierowców. Funkcje przypisywania możesz wykonać jako administrator w poniższy sposób. 
Przypisanie planisty do auta:

```http
POST http://localhost:8080/admin/planner/7/assignTruck/2
```

Przypisanie kierowcy do auta:

```http
POST http://localhost:8080/admin/driver/6/assignTruck/1
```

Pamiętaj aby zastąpić ID tymi które są u Ciebie dostępne.


Przed dalszymi testami należy przelogować się na utworzone konto planisty lub wykorzystaj moje dane:

```http
POST http://localhost:8080/auth/login
```

```
{
    "email": "planner@example.com",
    "password": "hard"
}
```

<i>Mimo że administrator ma dostęp do każdej sekcji, jest to niezbędne gdyż podczas tworzenia zlecenia czy to spedycyjnego czy transportowego aplikacja przypisuje zalogowanego użytkownika do zlecenia.</i>

Wróćmy do testowania funkcjonalności sekcji transportu. Zasada pracy planisty jest podoba do pracy spedytora. Również przyjmujemy ładunki od klientów. Zatem możesz wykorzystać zasoby które utworzyłeś wcześniej lub wrócić i ponownie dodać nowego klienta i nowe ładunki. Pracownik ten jednak tworzy zlecenie które jest realizowane przez firmową ciężąrówkę. 

Dane które mogą być przydatne przy tworzeniu takiego zlecenia to np. dostępni kierowcy czy ciężarówki


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
        "truckId": 1
    }
```

Pamiętaj aby ładunek był wolny - nie przypisany do innych zleceń, aby waluta zgadzała się z walutą ładunku oraz aby ciężarówka miała przypisanego kierowcę i miała aktualne badania techniczne i ubezpieczenie. 
Pozostałe istotne możliwości to:


- edycja zlecenia transportowego
```http
PATCH http://localhost:8080/transport-orders/{id}
```
- anulacja zlecenia transportowego
```http
PATCH http://localhost:8080/transport-orders/{id}/cancel
```


Przechodzimy teraz do sekcji magazynowania. 
Jako administrator możemy podobnie jak poprzednio dodawać pracowników, w tym wypadku będą to pracownicy magazynu. 
Wszystkie dostępne funkcjonalności możemy tu realizować jako administrator, spedytor lub planista, a więc nie ma znaczenia na którym koncie jesteśmy aktualnie zalogowani.

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

Wyświetlenie wszystkich pracowników odbywa się poprzez wysłanie żądania:

```http
GET http://localhost:8080/admin/warehouse-workers
```


Aby zacząć świadczyć usługi magazynowania, oprócz pracowników należy dodać przede wszystkim magazyn, ta opcja również dostępna jest tylko do administratora:


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


Pozostałe przydatne żądania z tej sekcji to np.

- wyświetlanie nie przypisanych do żadnego zlecenia towarów
```http
GET http://localhost:8080/warehouses/goods/not-assigned
```
- edycja towaru
```http
PATCH http://localhost:8080/warehouses/goods/{id}
```
- usunięcie towaru
```http
PATCH http://localhost:8080/warehouses/goods/{id}
```
- wyświetlenie wszystkich zleceń
```http
GET http://localhost:8080/warehouses/orders
```
- wyświetlenie zleceń nie zakończonych
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

Przebrnęliśmy  przez trzy bardzo ważne sekcje w branży TSL. Teraz pora na równie istotny element czyli księgowość. 
W przypadku księgowości możesz korzystać z konta administratora lub zalogować się na przygotowane przeze mnie konto księgowej, oraz oczywiście możesz zarejestrować nowego pracownika.


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

<b>Wysyłając odpowiednie żądania możemy wyświetlać faktury, sortować, edytować, oznaczać jako zapłacone, co jest równoznaczne ze zmianą salda klienta. </b>

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

Analogicznie możemy postępować z fakturami za zlecenia magazynowe. 

Jako administrator mamy również możliwość obliczenia wypłaty danego pracownika. W przypadku spedytorów i planistów wypłata to podstawa + prowizja, dla każdego z nich obliczana inaczej. 
Spedytor dostaje premie w zależności od sumy marży jaką uzyska w danym miesiącu da którego obliczamy pensje. Planista natomiast dostaje premie w zależności od przypisanych do niego ciężarówek.
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



## EN


## Description

During the creation of this application, I utilized my professional experience. I have been working in the transportation industry for over 8 years. I really wanted to create an application that could confidently serve me in my daily work and streamline everyday tasks.

The Transport Management System (TSL) is divided into several sections. The <b>Administrator section</b> allows the registration of new company employees, such as:

- forwarders,
- transport planners,
- accounting staff,
- drivers,
- warehouse employees.
- 
We can add new company trucks and assign drivers to them. We can also access more information about contractors, such as their current balance, not available to regular employees. The administrator also has the ability to calculate an employee's salary for a given month.

The second section is the <b>Warehouse section</b>. As employees, we can add new warehouses, goods, and create warehouse orders based on them. Each warehouse has a specified type, area, and storage costs. Goods, on the other hand, have unique labels, and based on these labels, we create orders.

The next section is the <b>Dispatch section</b>. Here, we can add clients who assign us goods transportation tasks, add cargoes, and carriers who will carry out the transport. Based on all this information, we create dispatch orders. Each order has a specified margin assigned to the dispatcher.

The <b>Transport section</b> functions similarly but skips the carrier stage, as the transport is carried out with the company's own fleet.

The <b>Accounting section</b> primarily deals with invoices from carriers, invoices to clients for cargoes, and warehouse orders.

An option available to everyone is the ability to leave a message for the company, simulating a contact form.

In the application, I primarily used: Java, Spring Boot, Spring Data, Spring Security, MySQL, Liquibase, Hibernate, and Lombok. For testing purposes, I utilized jUnit and Mockito. TSL is secured using JWT tokens.



## Changes

TSL is continuously being developed, and I would like the functionalities described above to be just the beginning of the application's evolution. In the near future, I plan to add:

- chat simulations between company employees
- the ability to create reports (e.g., the profitability of a specific employee)
- balance limits for clients
- integration with the NBP API to retrieve currency exchange rates (for international clients, to facilitate calculation of cargo/invoice values in foreign currencies)
- implementation of the ability to calculate discounts, i.e., faster invoice payments (SKONTO)
- modification of the database with a new table for employee salaries
- corrections to tests


## Installation

Firstly, you need to clone the repository:

```bash
  git clone https://github.com/maciej-jankowskii/TSL-2.0
```

Remember to create a local database on your computer and configure the application.yml file accordingly. 
For easier application testing, I recommend downloading Postman from the official website:

```bash
  https://www.postman.com/
```

## How it works

To begin testing the application, you first need to log in. You can do this as an administrator and then register a new employee or log in directly using the credentials I have prepared for specific employees. For testing purposes, I will guide you through two paths described above. Let's start by testing the dispatch section.

Launch the Postman program and send a <b>POST</b> request to the specified address. Use the data provided by me; this is an administrator account, details below:

```http
POST http://localhost:8080/auth/login
```
```
{
    "email": "john@example.com",
    "password": "hard"
}
```


In response, the application will generate a <b>token</b> for you, which you will need for further actions. Remember to use this token with each subsequent request. More information on how to do this can be found below. Now, you can register a new employee account to operate further.

Prepare a new request:

```http
POST http://localhost:8080/admin/forwarders/register
```

Example data: 

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
}
```

Remember to pass the token beforehand. See the screenshot below:

<img src="https://github.com/maciej-jankowskii/TSL-2.0/blob/85d690b017aa0a92b8fd438a3c54b4f735f26eba/src/main/resources/static/auth.png" alt="project-screenshot" width="760" height="320/">


You must pass the token with practically every request, except for logging in and using the contact form, as mentioned above.

If you don't want to register a new forwarder, log in using the credentials I provided earlier.

```http
POST http://localhost:8080/auth/login
```

```
{
    "email": "forw1@example.com",
    "password": "hard"
}
```


We can now start exploring the functionalities provided by the dispatch section. Every dispatcher must first add a client with whom they will cooperate and who will assign us transports. To do this, follow the instructions.

In addition to basic data, a client has their address and a contact person. Let's add the necessary information.

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
    "firstName": "John",
    "lastName": "Doe",
    "email": "doe@example.pl",
    "telephone": "775341222"
}
```


If you change the request type to <b>GET</b> and send it to the exact same address, you will receive the current list of addresses and contact persons. 
Adding a client:

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


Remember to include the token in the <b>AUTHORIZATION</b> section.
In response, you should now receive the status code 201 Created. This way, you have created a client to collaborate with. The client assigns us transport and sends an order with specific goods. Let's create a new cargo.

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

We have just created a new cargo. At this moment, the client's balance has changed by the specified price + VAT if it's a client from Poland. 
By sending the exact same requests but with the GET method, you will receive a list of clients and a list of cargoes. 
If there are more cargoes in our database, we can sort them or display only those that are not yet assigned to any dispatch order.


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

Remember to add, after the "=" sign, based on what criteria the cargo will be sorted (e.g., cargo number, price, date added, loading and unloading dates, etc.).

Now that we have a cargo, the next step is to add a carrier who will carry out the transport.

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

Remember to ensure that the expiration dates of the carrier's license and insurance are current. Otherwise, we won't be able to assign such a carrier to the order we will create shortly.

Similar to before, we can display and sort carriers:

```http
GET http://localhost:8080/carriers
```
```http
GET http://localhost:8080/carriers/sorted?sortBy=
```

Now we can create forwarding order.

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


This way, we have created an order, earning the difference between the cargo price and the order price, and assigning this difference to the dispatcher's account. This will make it easier for us in the next steps to calculate the total employee payout.

Displaying all orders is available at:

```http
GET http://localhost:8080/forwarding-orders
```

Importantly, the dispatcher sees <b>only their own orders.</b>

Other available options in the forwarder section include:

- update forwarding order
```http
PATCH http://localhost:8080/forwarding-orders/{id}
```
- cancel forwarding order
```http
PATCH http://localhost:8080/forwarding-orders/{id}/cancel
```
- update cargo
```http
PATCH http://localhost:8080/cargos/{id}
```
- delete cargo
```http
DELETE http://localhost:8080/cargos/{id}
```
- update customer
```http
PATCH http://localhost:8080/customers/{id}
```
- update carrier
```http
PATCH http://localhost:8080/carriers/{id}
```

In the place of <b>{id}</b>, you should specify the id of the respective resource. 
Editing items is associated with certain limitations and security measures that I have implemented, and you will learn about them during the application testing.

We have now gone through the dispatch section, and we can move on to the transport section. This is the part of the application that will handle functionalities related to transporting goods with our own fleet.

First, you should log in to the transport planner's account, as the dispatcher does not have access to this section. 
Similarly to the situation at the very beginning, you can register a new transport planner as an administrator.

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
    "truckIds": [0]
}
```

If you want to assign a truck to the planner immediately, add it, and then, when registering the employee, enter the truck's ID in the 'truckIds' field.

Adding a truck can be done as follows:

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

Remember that orders can be carried out by trucks that have assigned drivers. You can perform assignment functions as an administrator in the following way.

Assigning a transport planner to a truck:

```http
POST http://localhost:8080/admin/planner/7/assignTruck/2
```

Assigning a driver to truck:

```http
POST http://localhost:8080/admin/driver/6/assignTruck/1
```

Before further testing, you should log in to the created transport planner account or use my provided credentials:

```http
POST http://localhost:8080/auth/login
```

```
{
    "email": "planner@example.com",
    "password": "hard"
}
```

<i>Even though the administrator has access to every section, it is essential because during the creation of an order, whether it is dispatch or transport, 
the application assigns the logged-in user to the order.</i>

Let's go back to testing the functionalities of the transport section. The workflow of a transport planner is similar to that of a dispatcher. 
We also accept cargoes from clients. So, you can use the resources you created earlier or go back and add a new client and new cargoes again. However, this employee creates an order that is executed by a company truck.

Useful information when creating such an order includes available drivers or trucks.

```http
GET http://localhost:8080/drivers
```
```http
GET http://localhost:8080/trucks
```
```http
GET http://localhost:8080/transport-orders
```

Now we can create transport order:


```http
POST http://localhost:8080/transport-orders
```

Example data:

```
    {
        "orderNumber": "TSL001",
        "cargoId": 2,
        "price": 2000.00,
        "currency": "PLN",
        "truckId": 1
    }
```

Remember to ensure that the cargo is free - not assigned to other orders, that the currency matches the cargo currency, and that the truck has an assigned driver with up-to-date technical inspections and insurance.

Other important functionalities include:

- update transport order
```http
PATCH http://localhost:8080/transport-orders/{id}
```
- cancel transport order
```http
PATCH http://localhost:8080/transport-orders/{id}/cancel
```

We are now moving to the warehousing section. As an administrator, we can, similarly to before, add employees, in this case, warehouse employees. All available functionalities can be executed here as an administrator, dispatcher, or transport planner, so it doesn't matter which account we are currently logged into.

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

Displaying all warehouse workers:

```http
GET http://localhost:8080/admin/warehouse-workers
```

Now we can add new Warehouse ( remember, only admin can do it)

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

Displaying, updating and deleting warehouses:

```http
GET http://localhost:8080/warehouses
```
```http
PATCH http://localhost:8080/warehouses/{id}
```
```http
DELETE http://localhost:8080/warehouses/{id}
```

At this point, we can start receiving the first goods. To do this, send a request:

```http
POST http://localhost:8080/warehouses/goods
```

We are creating a warehouse order based on this. Keep in mind that a warehouse order can only contain goods of the same type and with unique labels.

```http
POST http://localhost:8080/warehouses/orders
```

Other requests:

- fisplaying free goods
```http
GET http://localhost:8080/warehouses/goods/not-assigned
```
- update goods
```http
PATCH http://localhost:8080/warehouses/goods/{id}
```
- delete goods
```http
PATCH http://localhost:8080/warehouses/goods/{id}
```
- displaying all orders
```http
GET http://localhost:8080/warehouses/orders
```
- displaying not completed orders
```http
GET http://localhost:8080/warehouses/orders/not-completed
```
- ability to end the order
```http
PATCH http://localhost:8080/warehouses/orders/complete/{id}
```
- update order
```http
PATCH http://localhost:8080/warehouses/orders/{id}
```


We've gone through three very important sections in the TSL industry. Now it's time for an equally essential element, which is accounting.

In the case of accounting, you can use the administrator's account, log in to the accountant's account that I've prepared, or, of course, register a new employee.

Adding an employee:

```http
POST http://localhost:8080/admin/accountants/register
```

Example data:

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

Prepared accountant account:

```
{
    "email": "acc@example.com",
    "password": "hard"
}
```


Or, as mentioned above, stay on the administrator's account.

In this section, we add invoices from carriers and create invoices for clients. The VAT Calculator calculates invoice values for us, depending on whether the client is from Poland or abroad. Another class calculates the payment deadline.

Below is just an example of adding an invoice for a client:

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


<b>By sending the appropriate requests, we can display invoices, sort them, edit them, mark them as paid, which is equivalent to changing the client's balance.</b>

Example requests: 

- displaying carrier invoices
```http
GET http://localhost:8080/invoices/carrier
```
- displaying invoices for customers
```http
GET http://localhost:8080/invoices/customer
```


Marking invoices as paid is done through the request:

```http
PATCH http://localhost:8080/invoices/customer/{id}/paid
```
```http
PATCH http://localhost:8080/invoices/carrier/{id}/paid
```

Similarly, we can proceed with invoices for warehouse orders.

As an administrator, we also have the option to calculate the payout for a specific employee. For dispatchers and transport planners, the payout consists of a base salary plus commission, calculated differently for each of them. A dispatcher receives a bonus based on the sum of the margin obtained in a given month from which we calculate salaries. The transport planner, on the other hand, receives a bonus depending on the trucks assigned to him.

We can perform calculations by sending a request to the address:

```http
GET http://localhost:8080/salary/{id}
```

{id} is id number of employee.


The contact form option is still available. For this functionality, an email address has been created to receive messages sent through the form.

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
<b>Thank you for staying until the end, and I wish you successful testing of the application.</b> 👋
____________




## Autor

#### Maciej Jankowski
#### Linkedin
[![Linkedin](https://img.shields.io/badge/LinkedIn-0A66C2.svg?style=for-the-badge&logo=LinkedIn&logoColor=white)](https://www.linkedin.com/in/maciej-jankowskii/)
