package com.sksamuel.kotest.tests.json

import beJsonArray
import beJsonObject
import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import shouldBeJsonArray
import shouldBeJsonObject

class BeJsonTypeTests : FunSpec(
   {
      context("beJsonArray") {
         test("basic") {
            "[]".shouldBeJsonArray()
            "[1]".shouldBeJsonArray()
            """[{"null": [] }]""".shouldBeJsonArray()
            "[{}, {}]".shouldBeJsonArray()
         }

         test("Path-extractions work") {
            // language=JSON
            """
               { "first": { "second": [] } }
            """.shouldBeJsonArray("$.first.second")

            // language=JSON
            """
               [ {}, { "second": [] } ]
            """.shouldBeJsonArray("$[1].second")
         }
      }

      context("beJsonObject") {
         test("basic") {
            shouldFail {
               "[]".shouldBeJsonObject()
               "[1]".shouldBeJsonObject()
               """[{"null": [] }]""".shouldBeJsonObject()
               "[{}, {}]".shouldBeJsonObject()
            }.message shouldBe """
               Was not a JSON object
            """.trimIndent()

            "{}".shouldBeJsonObject()
            """{ "prop1": "hello" }""".shouldBeJsonObject()
            """{ "x": [] }""".shouldBeJsonObject()
         }

         test("Path-extractions work") {
            // language=JSON
            val json = """{ "first": { "second": [] } }"""
            json.shouldBeJsonObject()
            json.shouldBeJsonObject("$")
            json.shouldBeJsonObject("$.first")

            shouldFail {
               json.shouldBeJsonObject("$.first.second")
            }

            // language=JSON
            val array = """[ {}, { "second": [] } ]"""
            array.shouldBeJsonObject("$[0]")
            array.shouldBeJsonObject("$[1]")
            shouldFail { array.shouldBeJsonObject() }
         }
      }
   }
)
