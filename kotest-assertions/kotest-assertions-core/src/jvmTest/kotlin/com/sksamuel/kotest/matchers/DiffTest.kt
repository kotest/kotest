package com.sksamuel.kotest.matchers

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.string.Diff
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

class DiffTest : WordSpec() {
  init {
    "diff" should {
      "test diff representation of basic types" {
        Diff.create('a', "a").toString() shouldBe """
          |expected:
          |  "a"
          |but was:
          |  'a'
        """.trimMargin()
        Diff.create(1, 1L).toString() shouldBe """
          |expected:
          |  1L
          |but was:
          |  1
        """.trimMargin()
        Diff.create(1.0, 1.0F).toString() shouldBe """
          |expected:
          |  1.0F
          |but was:
          |  1.0
        """.trimMargin()
        Diff.create(1.toByte(), 1.toShort()).toString() shouldBe """
          |expected:
          |  1.toShort()
          |but was:
          |  1.toByte()
        """.trimMargin()
        Diff.create(Any(), null).toString().shouldStartWith("""
          |expected:
          |  null
          |but was:
          |  java.lang.Object@
        """.trimMargin())
        Diff.create(emptyList<Any>(), emptySet<Any>()).toString() shouldBe """
          |expected:
          |  setOf()
          |but was:
          |  listOf()
        """.trimMargin()
        Diff.create(byteArrayOf(1, 2), shortArrayOf(1, 2)).toString() shouldBe """
          |expected:
          |  shortArrayOf(1.toShort(), 2.toShort())
          |but was:
          |  byteArrayOf(1.toByte(), 2.toByte())
        """.trimMargin()
        Diff.create(intArrayOf(1, 2), longArrayOf(1, 2)).toString() shouldBe """
          |expected:
          |  longArrayOf(1L, 2L)
          |but was:
          |  intArrayOf(1, 2)
        """.trimMargin()
        Diff.create(charArrayOf('a', 'b'), arrayOf('a', 'b')).toString() shouldBe """
          |expected:
          |  arrayOf('a', 'b')
          |but was:
          |  charArrayOf('a', 'b')
        """.trimMargin()
      }

      "test diff with nested maps" {
        val nestedMaps1 = mapOf(
            "nested" to mapOf(
                "a" to 1,
                "one more level" to mapOf(
                    "b" to 2,
                    "c" to listOf<Any>(1, 2, 3L),
                    "z" to 99
                )
            )
        )
        val nestedMaps2 = mapOf(
            "nested" to mapOf(
                "one more level" to mapOf(
                    "b" to 2,
                    "c" to listOf(1, 2, 3),
                    "d" to 4
                )
            )
        )
        Diff.create(nestedMaps1, nestedMaps2).toString() shouldBe """
          |different values:
          |  "nested":
          |    extra keys:
          |      "a"
          |    different values:
          |      "one more level":
          |        missing keys:
          |          "d"
          |        extra keys:
          |          "z"
          |        different values:
          |          "c":
          |            expected:
          |              listOf(1, 2, 3)
          |            but was:
          |              listOf(1, 2, 3L)
        """.trimMargin()
        Diff.create(nestedMaps1, nestedMaps2, ignoreExtraMapKeys = true).toString() shouldBe """
          |different values:
          |  "nested":
          |    different values:
          |      "one more level":
          |        missing keys:
          |          "d"
          |        different values:
          |          "c":
          |            expected:
          |              listOf(1, 2, 3)
          |            but was:
          |              listOf(1, 2, 3L)
        """.trimMargin()
      }
    }
  }
}
