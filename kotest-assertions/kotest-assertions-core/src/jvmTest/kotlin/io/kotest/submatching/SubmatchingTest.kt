package io.kotest.submatching

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

class SubmatchingTest: WordSpec() {
   init {
      "findPartialMatches" should {
         "find nothing" {
            findPartialMatches("apple", "orange", minLength = 3).shouldBeEmpty()
         }
         "match end of one string to beginning of another" {
            findPartialMatches("broom", "roommate", minLength = 4) shouldBe listOf(
               PartialCollectionMatch(MatchedCollectionElement(1, 0), 4, "broom".toList()))
         }
         "match two middles" {
            findPartialMatches("room", "boot", minLength = 2) shouldBe listOf(
               PartialCollectionMatch(MatchedCollectionElement(1, 1), 2, "room".toList()))
         }
         "find common end" {
            findPartialMatches("river", "driver", minLength = 3) shouldBe listOf(
               PartialCollectionMatch(MatchedCollectionElement(0, 1), 5, "river".toList()))
         }
         "find two common substrings in same order" {
            findPartialMatches("roommate", "room-mate", minLength = 3) shouldBe listOf(
               PartialCollectionMatch(MatchedCollectionElement(0, 0), 4, "roommate".toList()),
               PartialCollectionMatch(MatchedCollectionElement(4, 5), 4, "roommate".toList()),
            )
         }
         "find two common substrings in opposite order" {
            findPartialMatches("downsize", "size down", minLength = 3) shouldBe listOf(
               PartialCollectionMatch(MatchedCollectionElement(0, 5), 4, "downsize".toList()),
               PartialCollectionMatch(MatchedCollectionElement(4, 0), 4, "downsize".toList()),
            )
         }
         "maintain performance".config(timeout = 1.seconds) {
            val target = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
            val value = target.substring(5, target.length - 10)
            val partialMatches = findPartialMatches(value, target, target.length / 2)
            partialMatches.size shouldBe 1
            partialMatches[0].length shouldBe target.length - 15
         }
         "work for Int" {
            val value = listOf(1, 2, 3, 4)
            findPartialMatches (
               value = value,
               target = listOf(0, 1, 2, 3, 4, 3, 6),
               minLength = 4
            ) shouldBe listOf(PartialCollectionMatch(MatchedCollectionElement(0, 1), 4, value),
            )
         }
      }
      "matchedElements" should {
         "return empty list if element not in index" {
            matchedElements(indexes = mapOf(
               'p' to listOf(0, 2, 3),
               'u' to listOf(1),
               'y' to listOf(4)
            ),
               elementAtIndex = 4 to 'e'
            ).shouldBeEmpty()
         }
         "return list of one element" {
            matchedElements(indexes = mapOf(
               'p' to listOf(0, 2, 3),
               'u' to listOf(1),
               'y' to listOf(4)
            ),
               elementAtIndex = 3 to 'u'
            ) shouldBe listOf(MatchedCollectionElement(3, 1))
         }
         "return list of several elements" {
            matchedElements(indexes = mapOf(
               'p' to listOf(0, 2, 3),
               'u' to listOf(1),
               'y' to listOf(4)
            ),
               elementAtIndex = 1 to 'p'
            ) shouldBe listOf(
               MatchedCollectionElement(1, 0),
               MatchedCollectionElement(1, 2),
               MatchedCollectionElement(1, 3),
            )
         }
      }
      "extendPartialMatchToRequiredLength" should {
         "return submatch if it has required length" {
            val matchedElement = MatchedCollectionElement(0, 1)
            val value = "table".toList()
            extendPartialMatchToRequiredLength(
               value = value,
               target = "stable".toList(),
               matchedElement = matchedElement,
               minLength = 5
            ) shouldBe PartialCollectionMatch(matchedElement, 5, value)
         }
         "return null if submatch is too short" {
            val matchedElement = MatchedCollectionElement(0, 1)
            val value = "rush".toList()
            extendPartialMatchToRequiredLength(
               value = value,
               target = "brushes".toList(),
               matchedElement = matchedElement,
               minLength = 5
            ) shouldBe null
         }
      }
      "removeShorterMatchesWithSameEnd" should {
         "leave matches as is when there is nothing to remove" {
            val matches = listOf(
               PartialCollectionMatch(MatchedCollectionElement(0, 5), 4, "down".toList()),
               PartialCollectionMatch(MatchedCollectionElement(4, 0), 4, "size".toList()),
            )
            removeShorterMatchesWithSameEnd(matches) shouldBe matches
         }
         "remove shorter matches that are inside longer ones" {
            val matches = listOf(
               PartialCollectionMatch(MatchedCollectionElement(0, 5), 4, "down".toList()),
               PartialCollectionMatch(MatchedCollectionElement(4, 0), 4, "size".toList()),
               PartialCollectionMatch(MatchedCollectionElement(1, 6), 3, "own".toList()),
               PartialCollectionMatch(MatchedCollectionElement(5, 1), 3, "ize".toList()),
            )
            removeShorterMatchesWithSameEnd(matches) shouldBe matches.filter { it.length == 4}
         }
      }
      "toCharIndex" should {
         "count" {
            toCharIndex("apple".toList()) shouldBe mapOf(
               'a' to listOf(0),
               'p' to listOf(1, 2),
               'l' to listOf(3),
               'e' to listOf(4)
            )
         }
      }
      "lengthOfMatch" should {
         "return 0 at mismatch" {
            (0..3).forEach { start ->
               withClue("Matching target at index $start") {
                  lengthOfMatch(
                     value = "bug".toList(),
                     target = "feature".toList(),
                     matchedElement = MatchedCollectionElement(0, start)
                  ) shouldBe 0
               }
            }
         }
         "mismatch on first char" {
            lengthOfMatch(
               value = "prone".toList(),
               target = "drone".toList(),
               matchedElement = MatchedCollectionElement(0, 0)
            ) shouldBe 0
         }
         "skip some chars at start" {
            lengthOfMatch(
               value = "prone".toList(),
               target = "drone".toList(),
               matchedElement = MatchedCollectionElement(1, 1)
            ) shouldBe 4
         }
         "find common start" {
            lengthOfMatch(
               value = "car".toList(),
               target = "cartoon".toList(),
               matchedElement = MatchedCollectionElement(0, 0)
            ) shouldBe 3
         }
         "stop at shorter substring in the middle of a loger one" {
            lengthOfMatch(
               value = "rip".toList(),
               target = "tripod".toList(),
               matchedElement = MatchedCollectionElement(0, 1)
            ) shouldBe 3
         }
         "find common end" {
            lengthOfMatch(
               value = "come".toList(),
               target = "outcome".toList(),
               matchedElement = MatchedCollectionElement(0, 3)
            ) shouldBe 4
         }
      }
   }
}

