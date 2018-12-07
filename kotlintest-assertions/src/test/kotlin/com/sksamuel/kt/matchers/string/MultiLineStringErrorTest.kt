package com.sksamuel.kt.matchers.string

import io.kotlintest.diffLargeString
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class MultiLineStringErrorTest : StringSpec({

  "multi line strings with diff should show snippet of text" {

    val expected = """Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in ten years, Admiral.
      Take the ship into the Neutral Zone
      Besides, you look good in a dress.
      Some days you get the bear, and some days the bear gets you."""

    val actual = """Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in twelve years, Admiral.
      Take the ship into the Neutral Zone
      Besides, you look good in a dress.
      Some days you get the bear, and some days the bear gets you."""

    val (expectedRepr, actualRepr) = diffLargeString(expected, actual, 1)
    expectedRepr shouldBe """[Change at line 2]       Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in ten years, Admiral.
      Take the ship into the Neutral Zone"""

    actualRepr shouldBe """[Change at line 2]       Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in twelve years, Admiral.
      Take the ship into the Neutral Zone"""
  }

  "multi line string mismatch should support multiple errors" {

    val expected = """Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Klingon ship.
      A lot of things can change in ten years, Admiral.
      Take the ship into the Neutral Zone
      Some days you get the bear, and some days the bear gets you."""

    val actual = """Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in twelve years, Admiral.
      Take the ship into the Neutral Zone
      Besides, you look good in a dress.
      Some days you get the bear, and some days the bear gets you."""

    val (expectedRepr, actualRepr) = diffLargeString(expected, actual, 1)
    expectedRepr shouldBe """[Change at line 1] Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Klingon ship.
      A lot of things can change in ten years, Admiral.

[Deletion at line 4]       Take the ship into the Neutral Zone
      Some days you get the bear, and some days the bear gets you."""

    actualRepr shouldBe """[Change at line 1] Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in twelve years, Admiral.

[Deletion at line 4]       Take the ship into the Neutral Zone
      Besides, you look good in a dress.
      Some days you get the bear, and some days the bear gets you."""
  }

  "diff should return input as is if under min size" {

    val expected = """Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Klingon ship.
      A lot of things can change in ten years, Admiral.
      Take the ship into the Neutral Zone
      Some days you get the bear, and some days the bear gets you."""

    val actual = """Our neural pathways have become accustomed to your sensory input patterns.
      Mr. Crusher, ready a collision course with the Borg ship.
      A lot of things can change in twelve years, Admiral.
      Take the ship into the Neutral Zone
      Besides, you look good in a dress.
      Some days you get the bear, and some days the bear gets you."""

    val (expectedRepr, actualRepr) = diffLargeString(expected, actual, 30)
    expected shouldBe expectedRepr
    actual shouldBe actualRepr
  }
})