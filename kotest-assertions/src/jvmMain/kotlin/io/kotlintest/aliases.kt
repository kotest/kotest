package io.kotlintest

import io.kotest.shouldBe
import io.kotest.shouldNotBe

infix fun <T, U : T> T.shouldBe(any: U?) = this shouldBe any
infix fun <T> T.shouldNotBe(any: Any?) = this shouldNotBe any
