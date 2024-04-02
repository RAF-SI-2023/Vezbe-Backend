Feature: Testiranje korisnickog kontrolera

  Scenario: Pravljenje novog korisnika putem REST API-a
    Given logovali smo se kao administrator
    When naparvimo korisnika "Mika Mikic" sa usernameom "mika" i passwordom "1234567"
    Then mozemo da povucemo korisnika "mika" preko API-a
