package io.kotest.assertions.json

import io.kotest.assertions.json.comparisons.compare
import io.kotest.matchers.MatcherResult

/**
 * Custom comparison for a JSON subtree, registered in [CompareJsonOptions.overrideMatchers].
 *
 * The matcher receives both the expected and the actual subtree and decides whether they match;
 * when it matches, structural comparison of the subtree is skipped entirely.
 */
fun interface JsonSubtreeMatcher {

   /**
    * Compares the [expected] and [actual] subtrees.
    *
    * @return a [MatcherResult]; [MatcherResult.passed] decides the outcome and
    * [MatcherResult.failureMessage] is rendered, prefixed with the subtree path,
    * when the overall assertion fails.
    */
   fun assert(expected: JsonNode, actual: JsonNode): MatcherResult
}

/**
 * Renders a comparison path — a list of object keys and bracketed array indexes — in the
 * dotted form used as the key of [CompareJsonOptions.overrideMatchers].
 *
 * E.g. `listOf("users", "[0]", "name")` becomes `users[0].name`; the empty path becomes "".
 */
internal fun List<String>.toJsonPath(): String = buildString {
   for (segment in this@toJsonPath) {
      if (segment.startsWith("[")) append(segment) else {
         if (isNotEmpty()) append('.')
         append(segment)
      }
   }
}

/**
 * Returns a [JsonSubtreeMatcher] that passes when every element of the expected array
 * is matched by some element of the actual array, ignoring order and allowing the actual
 * array to contain extra elements.
 *
 * Elements are compared as JSON subtrees with the default [CompareJsonOptions].
 *
 * This is the "partial array match" — the actual array must cover the expected one,
 * but may contain more:
 *
 * ```kotlin
 * """{ "users": [{ "name": "sam" }, { "name": "bob" }] }""" shouldEqualJson {
 *    overrideMatchers = mapOf("users" to matchArraysContainingAll())
 *    """{ "users": [{ "name": "sam" }] }"""
 * }
 * ```
 */
fun matchArraysContainingAll(): JsonSubtreeMatcher = JsonSubtreeMatcher { expected, actual ->
   if (expected !is JsonNode.ArrayNode || actual !is JsonNode.ArrayNode) {
      MatcherResult(
         false,
         { "expected both subtrees to be arrays but expected was ${expected.type()} and actual was ${actual.type()}" },
         { "Expected values to not match" }
      )
   } else {
      val remaining = actual.elements.toMutableList()
      val missing = expected.elements.filter { expectedElement ->
         val index = remaining.indexOfFirst { actualElement ->
            compare(listOf(), expectedElement, actualElement, CompareJsonOptions()).isEmpty()
         }
         if (index == -1) true else {
            remaining.removeAt(index)
            false
         }
      }

      if (missing.isEmpty()) {
         MatcherResult(
            true,
            { "Expected values to not match" },
            { "all elements of ${show(expected)} were found in ${show(actual)}" }
         )
      } else {
         MatcherResult(
            false,
            { "expected element(s) ${missing.joinToString(", ") { show(it) }} not found in ${show(actual)}" },
            { "Expected values to not match" }
         )
      }
   }
}

/**
 * Returns a [JsonSubtreeMatcher] that compares string subtrees ignoring case.
 *
 * ```kotlin
 * """{ "name": "sam" }""" shouldEqualJson {
 *    overrideMatchers = mapOf("name" to matchStringsIgnoringCase())
 *    """{ "name": "SAM" }"""
 * }
 * ```
 */
fun matchStringsIgnoringCase(): JsonSubtreeMatcher = JsonSubtreeMatcher { expected, actual ->
   if (expected !is JsonNode.StringNode || actual !is JsonNode.StringNode) {
      MatcherResult(
         false,
         { "expected both subtrees to be strings but expected was ${expected.type()} and actual was ${actual.type()}" },
         { "Expected values to not match" }
      )
   } else {
      MatcherResult(
         expected.value.equals(actual.value, ignoreCase = true),
         { "expected '${expected.value}' but was '${actual.value}' (ignoring case)" },
         { "Expected values to not match" }
      )
   }
}
