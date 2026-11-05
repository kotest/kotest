package io.kotest.assertions.json

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.throwable.shouldHaveMessage

class OverrideMatchersTest : FunSpec(

   {
      test("match strings ignoring case at a leaf") {
         """{ "name": "sam" }""" shouldEqualJson {
            overrideMatchers = mapOf("name" to matchStringsIgnoringCase())
            """{ "name": "SAM" }"""
         }
      }

      test("a failing custom matcher reports the path and its message") {
         shouldFail {
            """{ "name": "sam" }""" shouldEqualJson {
               overrideMatchers = mapOf("name" to matchStringsIgnoringCase())
               """{ "name": "BOB" }"""
            }
         }.shouldHaveMessage(
            """
               At 'name' expected 'BOB' but was 'sam' (ignoring case)

               expected:<{
                 "name": "BOB"
               }> but was:<{
                 "name": "sam"
               }>
            """.trimIndent()
         )
      }

      test("matchArraysContainingAll allows extra elements in the actual array") {
         """{ "users": [ { "name": "bob" }, { "name": "sam" } ] }""" shouldEqualJson {
            overrideMatchers = mapOf("users" to matchArraysContainingAll())
            """{ "users": [ { "name": "sam" } ] }"""
         }
      }

      test("matchArraysContainingAll allows shuffled and partial arrays") {
         """{ "users": [ { "name": "jill" }, { "name": "tara" }, { "name": "sam" } ] }""" shouldEqualJson {
            overrideMatchers = mapOf("users" to matchArraysContainingAll())
            """{ "users": [ { "name": "sam" }, { "name": "jill" } ] }"""
         }
      }

      test("matchArraysContainingAll fails when an expected element is missing") {
         shouldFail {
            """{ "users": [ { "name": "sam" }, { "name": "bob" } ] }""" shouldEqualJson {
               overrideMatchers = mapOf("users" to matchArraysContainingAll())
               """{ "users": [ { "name": "sam" }, { "name": "jill" } ] }"""
            }
         }.shouldHaveMessage(
            """
               At 'users' expected element(s) {"name": jill} not found in [{"name": sam},{"name": bob}]

               expected:<{
                 "users": [
                   {
                     "name": "sam"
                   },
                   {
                     "name": "jill"
                   }
                 ]
               }> but was:<{
                 "users": [
                   {
                     "name": "sam"
                   },
                   {
                     "name": "bob"
                   }
                 ]
               }>
            """.trimIndent()
         )
      }

      test("a custom matcher can target a single array element") {
         """{ "users": [ { "name": "sam" } ] }""" shouldEqualJson {
            overrideMatchers = mapOf("users[0].name" to matchStringsIgnoringCase())
            """{ "users": [ { "name": "SAM" } ] }"""
         }
      }

      test("a custom matcher can target the whole document") {
         val anyDocument = JsonSubtreeMatcher { _, _ ->
            MatcherResult(true, { "Expected values to not match" }, { "Expected values to not match" })
         }

         """{ "a": 1 }""" shouldEqualJson {
            overrideMatchers = mapOf("" to anyDocument)
            """{ "a": 2 }"""
         }
      }

      test("custom matchers are honored when lenient arrays look for matching elements") {
         """[ { "name": "sam" } ]""" shouldEqualJson {
            arrayOrder = ArrayOrder.Lenient
            overrideMatchers = mapOf("[0].name" to matchStringsIgnoringCase())
            """[ { "name": "SAM" } ]"""
         }
      }

      test("a custom matcher can compare subtrees with its own logic") {
         val compareVersions = JsonSubtreeMatcher { expected, actual ->
            val passed = expected is JsonNode.StringNode && actual is JsonNode.StringNode &&
               expected.value.toDouble() == actual.value.toDouble()
            MatcherResult(passed, { "expected version ${show(expected)} but was ${show(actual)}" }, { "Expected values to not match" })
         }

         """{ "version": "1.10" }""" shouldEqualJson {
            overrideMatchers = mapOf("version" to compareVersions)
            """{ "version": "1.1" }"""
         }
      }
   }
)
