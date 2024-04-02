Feature: Testiranje kreiranja i brisanja korisnika

  Scenario: Kreiranje korisnika
    When napravimo korisnika "Marko Markovic" sa username-om "marko" i passwordom "123456"
    And izlistamo sve korisnike
    Then dobicemo korisnika koji ima username "marko"

  Scenario: Brisanje korisnika koji je prethodno napravljen
    When obrisemo korisnika "marko"
    Then povlacanje korisnika "marko" baca exception
