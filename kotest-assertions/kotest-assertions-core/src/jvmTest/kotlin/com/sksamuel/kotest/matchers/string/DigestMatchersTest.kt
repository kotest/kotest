package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveDigest
import io.kotest.matchers.string.shouldNotHaveDigest

class DigestMatchersTest : FunSpec({

  context("digest matchers") {
    test("string digest should match") {

      "cool for cats".shouldHaveDigest("md5", "cf6b4f4973077da736b50855f699d005")
      "cool for cats".shouldHaveDigest("SHA-256", "b3de79f07d214050325a260eeb512255243bfdabc1d1695419ae4b530a229bc4")

      "cool for cats".shouldNotHaveDigest("md5", "qwerty")
      "cool for cats".shouldNotHaveDigest("SHA-256", "qwerty")

      "".shouldHaveDigest("md5", "d41d8cd98f00b204e9800998ecf8427e")

    }
    test("return correct error message on failure") {

      shouldThrow<AssertionError> {
        "cool for cats".shouldHaveDigest("md5", "qwerty")
      }.message shouldBe "\"cool for cats\" should have md5 digest \"qwerty\" but was \"cf6b4f4973077da736b50855f699d005\""

      shouldThrow<AssertionError> {
        "cool for cats".shouldNotHaveDigest("md5", "cf6b4f4973077da736b50855f699d005")
      }.message shouldBe "\"cool for cats\" should not have md5 digest \"cf6b4f4973077da736b50855f699d005\""
    }
  }

})
