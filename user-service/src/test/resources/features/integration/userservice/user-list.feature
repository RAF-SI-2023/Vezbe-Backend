Feature: Testiranje listanja korisnika

  Scenario: Listanje svih korisnika
    Given imamo administratorskog korisnika sa username-om "admin"
    When izlistamo sve korisnike
    Then dobicemo korisnika koji ima username "admin"

  Scenario: Pretraga korisnika Pera koji ne postoji
    When izlistamo sve korisnike
    Then necemo dobiti korisnika koji ima username "pera"
