package io.kotest.assertions.arrow.core

import arrow.core.NonEmptyList
import io.kotest.matchers.collections.shouldBeSorted
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainDuplicates
import io.kotest.matchers.collections.shouldContainNoNulls
import io.kotest.matchers.collections.shouldContainNull
import io.kotest.matchers.collections.shouldContainOnlyNulls
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeSorted
import io.kotest.matchers.collections.shouldNotBeUnique
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.collections.shouldNotContainDuplicates
import io.kotest.matchers.collections.shouldNotContainNoNulls
import io.kotest.matchers.collections.shouldNotContainNull
import io.kotest.matchers.collections.shouldNotContainOnlyNulls
import io.kotest.matchers.collections.shouldNotHaveElementAt
import io.kotest.matchers.collections.shouldNotHaveSingleElement
import io.kotest.matchers.collections.shouldNotHaveSize

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldContainOnlyNulls(): NonEmptyList<A> =
  apply { all.shouldContainOnlyNulls() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotContainOnlyNulls(): NonEmptyList<A> =
  apply { all.shouldNotContainOnlyNulls() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldContainNull(): NonEmptyList<A> =
  apply { all.shouldContainNull() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotContainNull(): NonEmptyList<A> =
  apply { all.shouldNotContainNull() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldHaveElementAt(index: Int, element: A): Unit =
  all.shouldHaveElementAt(index, element)

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotHaveElementAt(index: Int, element: A): Unit =
  all.shouldNotHaveElementAt(index, element)

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldContainNoNulls(): NonEmptyList<A> =
  apply { all.shouldContainNoNulls() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotContainNoNulls(): NonEmptyList<A> =
  apply { all.shouldNotContainNoNulls() }

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldContain(a: A): Unit {
  all.shouldContain(a)
}

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldNotContain(a: A): Unit {
  all.shouldNotContain(a)
}

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldBeUnique(): NonEmptyList<A> =
  apply { all.shouldBeUnique() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotBeUnique(): NonEmptyList<A> =
  apply { all.shouldNotBeUnique() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldContainDuplicates(): NonEmptyList<A> =
  apply { all.shouldContainDuplicates() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotContainDuplicates(): NonEmptyList<A> =
  apply { all.shouldNotContainDuplicates() }

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldContainAll(vararg ts: A): Collection<A> =
  all.shouldContainAll(*ts)

@IgnorableReturnValue
public fun <A> NonEmptyList<A>.shouldNotContainAll(vararg ts: A): Collection<A> =
  all.shouldNotContainAll(*ts)

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldContainAll(ts: List<A>): Collection<A> =
  all.shouldContainAll(ts)

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldNotContainAll(ts: List<A>): Collection<A> =
  all.shouldNotContainAll(ts)

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldHaveSize(size: Int): NonEmptyList<A> =
  apply { all.shouldHaveSize(size) }

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldNotHaveSize(size: Int): NonEmptyList<A> =
  apply { all.shouldNotHaveSize(size) }

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldHaveSingleElement(a: A): NonEmptyList<A> =
  apply { all.shouldHaveSingleElement(a) }

@IgnorableReturnValue
public infix fun <A> NonEmptyList<A>.shouldNotHaveSingleElement(a: A): NonEmptyList<A> =
  apply { all.shouldNotHaveSingleElement(a) }

@IgnorableReturnValue
public fun <A : Comparable<A>> NonEmptyList<A>.shouldBeSorted(): NonEmptyList<A> =
  apply { all.shouldBeSorted() }

@IgnorableReturnValue
public fun <A : Comparable<A>> NonEmptyList<A>.shouldNotBeSorted(): NonEmptyList<A> =
  apply { all.shouldNotBeSorted() }
