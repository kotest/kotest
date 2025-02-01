package io.kotest.assertions.arrow.core

import arrow.core.NonEmptyList
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAny
import io.kotest.inspectors.forAtLeast
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forAtMost
import io.kotest.inspectors.forAtMostOne
import io.kotest.inspectors.forExactly
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forSome

@Deprecated(
  "use Kotest's Collection<A>.forAll.",
  ReplaceWith("forAll", "io.kotest.inspectors.forAll")
)
public fun <A> NonEmptyList<A>.forAll(f: (A) -> Unit): Unit {
  all.forAll(f)
}

@Deprecated(
  "use Kotest's Collection<A>.forOne.",
  ReplaceWith("forOne", "io.kotest.inspectors.forOne")
)
public fun <A> NonEmptyList<A>.forOne(f: (A) -> Unit): Unit {
  all.forOne(f)
}

@Deprecated(
  "use Kotest's Collection<A>.forExactly.",
  ReplaceWith("forExactly", "io.kotest.inspectors.forExactly")
)
public fun <A> NonEmptyList<A>.forExactly(k: Int, f: (A) -> Unit): Unit {
  all.forExactly(k, f)
}

@Deprecated(
  "use Kotest's Collection<A>.forSome.",
  ReplaceWith("forSome", "io.kotest.inspectors.forSome")
)
public fun <A> NonEmptyList<A>.forSome(f: (A) -> Unit): Unit {
  all.forSome(f)
}

@Deprecated(
  "use Kotest's Collection<A>.forAny.",
  ReplaceWith("forAny", "io.kotest.inspectors.forAny")
)
public fun <A> NonEmptyList<A>.forAny(f: (A) -> Unit): Unit {
  all.forAny(f)
}

@Deprecated(
  "use Kotest's Collection<A>.forAtLeastOne.",
  ReplaceWith("forAtLeastOne", "io.kotest.inspectors.forAtLeastOne")
)
public fun <A> NonEmptyList<A>.forAtLeastOne(f: (A) -> Unit): Unit {
  all.forAtLeastOne(f)
}

@Deprecated(
  "use Kotest's Collection<A>.forAtLeast.",
  ReplaceWith("forAtLeast", "io.kotest.inspectors.forAtLeast")
)
public fun <A> NonEmptyList<A>.forAtLeast(k: Int, f: (A) -> Unit): Unit {
  all.forAtLeast(k, f)
}

@Deprecated(
  "use Kotest's Collection<A>.forAtMostOne.",
  ReplaceWith("forAtMostOne", "io.kotest.inspectors.forAtMostOne")
)
public fun <A> NonEmptyList<A>.forAtMostOne(f: (A) -> Unit): Unit {
  all.forAtMostOne(f)
}

@Deprecated(
  "use Kotest's Collection<A>.forAtMost.",
  ReplaceWith("forAtMost", "io.kotest.inspectors.forAtMost")
)
public fun <A> NonEmptyList<A>.forAtMost(k: Int, f: (A) -> Unit): Unit {
  all.forAtMost(k, f)
}

@Deprecated(
  "use Kotest's Collection<A>.forNone.",
  ReplaceWith("forNone", "io.kotest.inspectors.forNone")
)
public fun <A> NonEmptyList<A>.forNone(f: (A) -> Unit): Unit {
  all.forNone(f)
}
