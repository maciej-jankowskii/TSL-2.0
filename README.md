# TSL 2.0

1. [English version below](#en)
2. [O aplikacji](#o)
3. [Najbliższe zmiany](#najbliższe)
4. [Instalacja](#instalacja)
5. [Działanie](#dzialanie)
6. [Autor](#autor)

## O aplikacji

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
Bazując na tym wszystkim tworzymy zlecenie spedycyjne. Każde zlecenie ma określoną marże która jest przypisywana do spedytora. Spedytor natomiast dostaje bonus do wypłaty w danym miesiącu, 
i bonus ten zależny jest od wysokości marży jaką uzyskał. 

Sekcja transportu działa bardzo podobnie, lecz pomija etap przewoźnika, z racji tego że transport odbywa się własnym taborem. 

Sekcja księgowości to przede wszystkim faktury od przeowoźników, faktury dla klientów za ładunki oraz zlecenia magazynowe.

Opcja dostępna dla każdego to możliwość zostawienia wiadomości dla firmy, jest to symulacja formularza kontaktowego. 

...

## Najbliższe zmiany 

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




## Działanie aplikacji

...




## Autor



...

## EN

