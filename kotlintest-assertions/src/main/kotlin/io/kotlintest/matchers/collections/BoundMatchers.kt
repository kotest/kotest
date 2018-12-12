package io.kotlintest.matchers.collections

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveUpperBound(t: T) = this should haveUpperBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveUpperBound(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
          value.all { it <= t },
          "Collection should have upper bound $t",
          "Collection should not have upper bound $t"
  )
}

infix fun <T : Comparable<T>, C : Collection<T>> C.shouldHaveLowerBound(t: T) = this should haveLowerBound(t)
fun <T : Comparable<T>, C : Collection<T>> haveLowerBound(t: T) = object : Matcher<C> {
  override fun test(value: C) = Result(
          value.all { t <= it },
          "Collection should have lower bound $t",
          "Collection should not have lower bound $t"
  )
}