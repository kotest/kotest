package io.kotlintest.matchers.boolean

import io.kotlintest.shouldBe

fun Boolean.shouldBeTrue() = this shouldBe true

fun Boolean.shouldBeFalse() = this shouldBe false
