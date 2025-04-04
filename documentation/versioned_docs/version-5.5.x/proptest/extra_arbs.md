---
id: extra_arbs
title: Extra Arbs
slug: property-test-extra-arbs.html
sidebar_label: Extra Arbs
---

If you are looking for a collection of Arbs for general purpose data generation,
then Kotest has such a [collection](https://github.com/kotest/kotest-property-arbs).

:::note
To use, add `io.kotest.extensions:kotest-property-arbs:version` to your build.
:::

[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-property-arbs?label=latest%20release"/>](https://search.maven.org/search?q=g:io.kotest.extensions)


| Arb  	                     | Details  	                                                                                                          |
|----------------------------|---------------------------------------------------------------------------------------------------------------------|
| Arb.firstName()            | Produces random english or hispanic first names                                                                     |
| Arb.lastName()             | Produces random last names based on US census data                                                                  |
| Arb.name()                 | Produces random first and last names                                                                                |
| 	                          | 	                                                                                                                   |
| Arb.stockExchanges()       | Produces random stock exchanges, eg `New York Stock Exchange / NYSE / US`                                           |
| 	                          | 	                                                                                                                   |
| Arb.domain()               | Produces random domain names, eg `www.wibble.co.uk`                                                                 |
| Arb.country()              | Produces random country objects, eg `Botswana / BW / Africa`                                                        |
| Arb.continent()            | Produces random continents from the list of seven                                                                   |
| Arb.zipcode()              | Random zipcodes from 01000 to 99999, without validating they are exant                                              |
| 	                          | 	                                                                                                                   |
| Arb.harryPotterCharacter() | Produces random first and last names from the Harry Potter series                                                   |
| 	                          | 	                                                                                                                   |
| Arb.color()                | Produces random named colours, eg, midnight blue                                                                    |
| Arb.brand()                | Produces random brand names, eg Betty Crocker                                                                       |
| Arb.products()             | Produces random google product categories, eg `Furniture > Office Furniture > Desks`                                |
| 	                          | 	                                                                                                                   |
| Arb.vineyards()	           | Produces random vineyard names, eg `Château Montus Prestige`                                                        |
| Arb.wineRegions()	         | Produces a random wine region, eg `Chassagne-Montrachet`                                                            |
| Arb.wines()                | Combines several wine details producers to return full wine objects                                                 |
| Arb.wineReviews()          | Combines wine producer and adds in random review scores and usernames                                               |
| Arb.iceCreamFlavors()      | Random ice cream flavors such as `Pistachio` or `Grape Escape`                                                      |
| Arb.iceCreams()            | Random ice cream servings with one or more flavors, cone type and size                                              |
| 	                          | 	                                                                                                                   |
| Arb.tubeStation()          | Produces randomly selected London underground tube stations                                                         |
| Arb.tubeJourney()          | Generates random journeys from a randomly selected start and end underground station                                |
| Arb.airport()              | Random real world airport with IATA code                                                                            |
| Arb.airline()              | Random real world airline                                                                                           |
| Arb.airJourney()           | Random airtrips between two airports with an airline and times                                                      |
|                            |                                                                                                                     |
| Arb.cluedoSuspects()       | Clue/Cluedo suspects, eg `Professor Plum`                                                                           |
| Arb.cluedoWeapons()        | Clue/Cluedo weapons, eg `Lead piping`                                                                               |
| Arb.cluedoLocations()      | Clue/Cluedo locations, eg `Ballroom`                                                                                |
| Arb.cluedoAccusation()     | Clue/Cluedo accusations, eg, `Mrs White / Billiards Room / Rope`                                                    |
| Arb.monopolyProperty()     | Random (US version) monopoly property with rent, price and color                                                    |
| Arb.chessPiece()           | Chess piece with points                                                                                             |
| Arb.chessSquare()          | Chesss square with file A-H and rank 1-8                                                                            |
| Arb.chessMove()            | Chess move from square to square with captured piece if any. No validation is performed to check the move is legal. |
|                            |                                                                                                                     |
| Arb.transactions()         | Transactions with a card number, card type, amount and transaction type                                             |
|                            |                                                                                                                     |
| Arb.cars()                 | Random car manufacturers                                                                                            |
