package io.kotest.matchers.maps

import io.kotest.assertions.ErrorCollectionMode
import io.kotest.assertions.errorCollector
import io.kotest.assertions.print.print
import io.kotest.assertions.runWithMode
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.string.Diff
import io.kotest.matchers.string.stringify
import io.kotest.similarity.possibleMatchesDescription

fun <K> haveKey(key: K): Matcher<Map<K, Any?>> = object : Matcher<Map<K, Any?>> {
   override fun test(value: Map<K, Any?>): MatcherResult {
      val passed = value.containsKey(key)
      val possibleMatchesDescription = if(passed) "" else
         possibleMatchesForMissingElements(
         setOf(key),
         value.keys,
         "key"
      )

      return MatcherResult(
         passed,
         { "Map should contain key $key$possibleMatchesDescription" },
         { "Map should not contain key $key" }
      )
   }
}

fun <K> haveKeys(vararg keys: K): Matcher<Map<K, Any?>> = object : Matcher<Map<K, Any?>> {
   override fun test(value: Map<K, Any?>): MatcherResult {
      val keysNotPresentInMap = keys.filterNot { value.containsKey(it) }
      val possibleMatchesDescription = possibleMatchesForMissingElements(
         keysNotPresentInMap.toSet(),
         value.keys,
         "keys"
      )

      return MatcherResult(
         keysNotPresentInMap.isEmpty(),
         { "Map did not contain the keys ${keysNotPresentInMap.joinToString(", ")}$possibleMatchesDescription" },
         { "Map should not contain the keys ${keys.filter { value.containsKey(it) }.joinToString(", ")}" }
      )
   }
}

fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
   override fun test(value: Map<*, V>): MatcherResult {
      val passed = value.containsValue(v)
      val possibleMatchesDescription = possibleMatchesForMissingElements(
         setOf(v),
         value.values.toSet(),
         "value"
      )
      return MatcherResult(
         passed,
         { "Map should contain value $v$possibleMatchesDescription" },
         { "Map should not contain value $v" })
   }
}

fun <V> haveValues(vararg values: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
   override fun test(value: Map<*, V>): MatcherResult {
      val valuesNotPresentInMap = values.filterNot { value.containsValue(it) }
      val possibleMatchesDescription = possibleMatchesForMissingElements(
         valuesNotPresentInMap.toSet(),
         value.values.toSet(),
         "values"
      )
      return MatcherResult(
         valuesNotPresentInMap.isEmpty(),
         { "Map did not contain the values ${valuesNotPresentInMap.joinToString(", ")}$possibleMatchesDescription" },
         { "Map should not contain the values ${values.joinToString(", ")}" }
      )
   }
}

fun <K> containAnyKeys(vararg keys: K): Matcher<Map<K, Any?>> = object : Matcher<Map<K, Any?>> {
   override fun test(value: Map<K, Any?>): MatcherResult {
      val passed = keys.any { value.containsKey(it) }
      return MatcherResult(
         passed,
         { "Map did not contain any of the keys ${keys.joinToString(", ")}" },
         { "Map should not contain any of the keys ${keys.joinToString(", ")}" }
      )
   }
}

fun <V> containAnyValues(vararg values: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
   override fun test(value: Map<*, V>): MatcherResult {
      val passed = values.any { value.containsValue(it) }
      return MatcherResult(
         passed,
         { "Map did not contain any of the values ${values.joinToString(", ")}" },
         { "Map should not contain any of the values ${values.joinToString(", ")}" }
      )
   }
}

fun <K, V> contain(key: K, v: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
   override fun test(value: Map<K, V>): MatcherResult {
      val passed = value[key] == v
      val possibleMatches = if(passed) "" else describePossibleMatches(key, v, value)
      return MatcherResult(
         passed,
         { "Map should contain mapping $key=$v but was ${buildActualValue(value)}$possibleMatches" },
         { "Map should not contain mapping $key=$v but was $value" }
      )
   }

   private fun buildActualValue(map: Map<K, V>) = map[key]?.let { "$key=$it" } ?: map

   private fun describePossibleMatches(key: K, v: V, map: Map<K, V>): String {
      val similarKeys = if(key in map) "" else possibleMatchesDescription(map.keys, key)
      val similarOrSameMatches = if(v in map.values) sameValueForOtherKeys(v, map) else similarValues(v, map)
      val matchesToInclude = listOf(similarKeys, similarOrSameMatches).filter { it.isNotEmpty() }
      return if(matchesToInclude.isEmpty()) "" else "\n${matchesToInclude.joinToString("\n")}"
   }

   private fun sameValueForOtherKeys(v: V, map: Map<K, V>): String = prettyPrintIfNotEmpty(
      list = map.entries.filter { it.value == v },
      prefix = "\nSame value found for the following entries: "
   )

   private fun similarValues(v: V, map: Map<K, V>): String = printIfNotEmpty(
      string = possibleMatchesDescription(map.values.toSet(), v),
      prefix = "\nSimilar values found:\n"
   )

}

internal fun printIfNotEmpty(string: String, prefix: String = "", suffix: String = ""): String =
   if(string.isEmpty()) "" else
      "$prefix$string$suffix"

internal fun<T> prettyPrintIfNotEmpty(list: List<T>, prefix: String = "", suffix: String = ""): String =
   if(list.isEmpty()) "" else
      "$prefix${list.print().value}$suffix"

fun <K, V> containAll(expected: Map<K, V>): Matcher<Map<K, V>> =
   MapContainsMatcher(expected, ignoreExtraKeys = true)

fun <K, V> containExactly(expected: Map<K, V>): Matcher<Map<K, V>> =
   MapContainsMatcher(expected)

fun <K, V> containExactly(vararg expected: Pair<K, V>): Matcher<Map<K, V>> =
   MapContainsMatcher(expected.toMap())

fun <K, V> haveSize(size: Int): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
   override fun test(value: Map<K, V>) =
      MatcherResult(
         value.size == size,
         { "Map should have size $size but has size ${value.size}" },
         { "Map should not have size $size" }
      )
}

class MapContainsMatcher<K, V>(
   private val expected: Map<K, V>,
   private val ignoreExtraKeys: Boolean = false
) : Matcher<Map<K, V>> {
   override fun test(value: Map<K, V>): MatcherResult {
      val diff = Diff.create(value, expected, ignoreExtraMapKeys = ignoreExtraKeys)
      val unexpectedKeys = (value.keys - expected.keys)
      val possibleMatchesDescription = possibleMatchesForMissingElements(unexpectedKeys, expected.keys, "keys")
      val (expectMsg, negatedExpectMsg) = if (ignoreExtraKeys) {
         "should contain all of" to "should not contain all of"
      } else {
         "should be equal to" to "should not be equal to"
      }
      val (butMsg, negatedButMsg) = if (ignoreExtraKeys) {
         "but differs by" to "but contains"
      } else {
         "but differs by" to "but equals"
      }
      val failureMsg = """
      |
      |Expected:
      |  ${stringify(value)}
      |$expectMsg:
      |  ${stringify(expected)}
      |$butMsg:
      |${diff.toString(1)}
      |
      """.trimMargin()
      val negatedFailureMsg = """
      |
      |Expected:
      |  ${stringify(value)}
      |$negatedExpectMsg:
      |  ${stringify(expected)}
      |$negatedButMsg
      |
    """.trimMargin()
      return MatcherResult(
         diff.isEmpty(),
         { failureMsg + possibleMatchesDescription },
         {
            negatedFailureMsg
         })
   }
}

internal fun <K> possibleMatchesForMissingElements(
   unexpected: Set<K>,
   expected: Set<K>,
   elementTypeDescription: String
): String {
   val possibleMatches = unexpected.
      map {
         possibleMatchesDescription(expected, it)
      }.filter {
         it.isNotEmpty()
   }.joinToString("\n")
   return if (possibleMatches.isEmpty()) ""
   else "\nPossible matches for missing $elementTypeDescription:\n$possibleMatches"
}


fun <K, V> matchAll(
   vararg matchers: Pair<K, (V) -> Unit>
): Matcher<Map<K, V>> = MapMatchesMatcher(matchers.toMap(), true)

fun <K, V> matchAll(
   expected: Map<K, (V) -> Unit>
): Matcher<Map<K, V>> = MapMatchesMatcher(expected, true)

fun <K, V> matchExactly(
   vararg matchers: Pair<K, (V) -> Unit>
): Matcher<Map<K, V>> = MapMatchesMatcher(matchers.toMap(), false)

fun <K, V> matchExactly(
   expected: Map<K, (V) -> Unit>
): Matcher<Map<K, V>> = MapMatchesMatcher(expected, false)

class MapMatchesMatcher<K, V>(
   private val expected: Map<K, (V) -> Unit>,
   private val ignoreExtraKeys: Boolean = false
) : Matcher<Map<K, V>> {
   override fun test(value: Map<K, V>): MatcherResult {
      val unexpectedKeys = mutableListOf<K>()
      val mismatches = mutableListOf<Pair<K, String?>>()
      val missingKeys = expected.keys - value.keys

      errorCollector.runWithMode(ErrorCollectionMode.Hard) {
         value.forEach { (k, v) ->
            val matcher = expected[k]

            if (matcher == null) {
               unexpectedKeys.add(k)
            } else {
               try {
                  matcher(v)
               } catch (e: AssertionError) {
                  mismatches.add(Pair(k, e.message))
               }
            }
         }
      }

      return MatcherResult(
         missingKeys.isEmpty() && mismatches.isEmpty() && (ignoreExtraKeys || unexpectedKeys.isEmpty()),
         { "Expected map to match all assertions. Missing keys were=$missingKeys, Mismatched values were=$mismatches, Unexpected keys were $unexpectedKeys." },
         { "Expected map to not match all assertions." },
      )
   }
}

