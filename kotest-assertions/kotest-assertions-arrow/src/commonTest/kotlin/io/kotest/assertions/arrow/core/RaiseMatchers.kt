package io.kotest.assertions.arrow.core

import arrow.core.raise.Raise
import io.kotest.assertions.arrow.shouldBe
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.StringSpec

class RaiseMatchers : StringSpec({
  "shouldRaise: specific type" {
    val raised = shouldRaise<String> {
      raise("failed")
    }
    raised shouldBe "failed"
  }

  "shouldRaise: subtype" {
    val raised = shouldRaise<CharSequence> {
      raise("failed")
    }
    raised shouldBe "failed"
  }

  "shouldRaise: nullable type" {
    val raised = shouldRaise<String?> {
      raise(null)
    }
    raised shouldBe null
  }

  "shouldRaise: fail if null is raised when not expected" {
    shouldThrowWithMessage<AssertionError>("Expected to raise String but <null> was raised instead.") {
      shouldRaise<String> {
        raise(null)
      }
    }
  }

  "shouldRaise: fail if expected raise type differs from actual" {
    shouldThrowWithMessage<AssertionError>("Expected to raise Int but String was raised instead.") {
      shouldRaise<Int> {
        raise("failed")
      }
    }
  }

  "shouldRaise: fail if nothing is raised" {
    shouldThrowWithMessage<AssertionError>("Expected to raise Int but nothing was raised.") {
      shouldRaise<Int> {
        42
      }
    }
  }

  "shouldNotRaise" {
    val res = shouldNotRaise {
      42
    }
    res shouldBe 42
  }

  "shouldNotRaise: fail if something is raised" {
    shouldThrowWithMessage<AssertionError>("No raise expected, but \"failed\" was raised.") {
      shouldNotRaise {
        raise("failed")
      }
    }
  }

  "shouldNotRaise: fail if null was raised" {
    shouldThrowWithMessage<AssertionError>("No raise expected, but <null> was raised.") {
      shouldNotRaise {
        raise(null)
      }
    }
  }

  "shouldNotRaise: allows suspend call in block" {
    val res = shouldNotRaise {
      suspend { 42 }()
    }
    res shouldBe 42
  }

  "shouldRaise: allows suspend call in block" {
    val res = shouldRaise<Int> {
      raise(suspend { 42 }())
    }
    res shouldBe 42
  }

  "shouldNotRaise: callable from non-suspend" {
    fun test() = shouldNotRaise { "success" }
    test() shouldBe "success"
  }

  "shouldRaise: callable from non-suspend" {
    fun test() = shouldRaise<String> { raise("failed") }
    test() shouldBe "failed"
  }
})
