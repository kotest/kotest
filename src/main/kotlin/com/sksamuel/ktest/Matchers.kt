package com.sksamuel.ktest

import kotlin.collections.collectionSizeOrDefault
import kotlin.reflect.KClass
import kotlin.text.*

interface Matchers {

  public infix fun Any.shouldEqual(any: Any): Unit = shouldBe(any)

  public infix fun Any.shouldBe(any: Any): Unit {
    if (!this.equals(any)) throw TestFailedException(this.toString() + " did not equal $any")
  }

  public infix fun Any.should(be: BeWord): TypeMatchers = TypeMatchers(this)

  public fun fail(msg: String) = throw TestFailedException(msg)

  interface BeWord

  public object be : BeWord

  interface HaveWord

  public object have : HaveWord

  interface StartWord

  public object start : StartWord

  interface EndWord

  public object end : EndWord

  public infix fun <T> Iterable<T>.should(have: HaveWord) = IterableMatchers(this)
  public infix fun String.should(have: HaveWord) = SubstringMatcher(this)
  public infix fun String.should(start: StartWord) = StartStringMatcher(this)
  public infix fun String.should(end: EndWord) = EndStringMatcher(this)
}

class IntMatchers(val int: Int) {

  public infix fun gt(other: Int): Unit {
    if (int <= other) {
      throw TestFailedException("$int is not greater than $other")
    }
  }

  public infix fun lt(other: Int): Unit {
    if (int >= other) {
      throw TestFailedException("$int is not less than $other")
    }
  }

  public infix fun gte(other: Int): Unit {
    if (int <= other) {
      throw TestFailedException("$int is not greater than or equal to $other")
    }
  }

  public infix fun lte(other: Int): Unit {
    if (int >= other) {
      throw TestFailedException("$int is not less than or equal to $other")
    }
  }
}

class LongMatchers(val long: Long) {

  public infix fun gt(other: Long): Unit {
    if (long <= other) {
      throw TestFailedException("$long is not greater than $other")
    }
  }

  public infix fun lt(other: Long): Unit {
    if (long >= other) {
      throw TestFailedException("$long is not less than $other")
    }
  }

  public infix fun gte(other: Long): Unit {
    if (long <= other) {
      throw TestFailedException("$long is not greater than or equal to $other")
    }
  }

  public infix fun lte(other: Long): Unit {
    if (long >= other) {
      throw TestFailedException("$long is not less than or equal to $other")
    }
  }
}

class TypeMatchers(val any: Any) {
  public infix fun a(expected: KClass<*>): Unit = an(expected)
  public infix fun an(expected: KClass<*>) {
    if (!expected.java.isAssignableFrom(any.javaClass)) {
      throw TestFailedException("Value is not of type $expected")
    }
  }
}

class IterableMatchers<T>(val iterable: kotlin.Iterable<T>) {
  public infix fun size(k: Int): Unit {
    val size = iterable.collectionSizeOrDefault(0)
    if (size != k) throw TestFailedException("Iterable was expected to have size $k but had size $size")
  }
}

class SubstringMatcher(val string: String) {
  public infix fun substring(substr: String): Unit {
    if (!string.contains(substr))
      throw TestFailedException("String does not have substring $substr")
  }
}

class StartStringMatcher(val string: String) {
  public infix fun with(prefix: String): Unit {
    if (!string.startsWith(prefix))
      throw TestFailedException("String does not start with $prefix but with ${string.take(prefix.length)}")
  }
}

class EndStringMatcher(val string: String) {
  public infix fun with(suffix: String): Unit {
    if (!string.endsWith(suffix))
      throw TestFailedException("String does not end with $suffix but with ${string.takeLast(suffix.length)}")
  }
}
