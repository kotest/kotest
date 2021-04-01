package io.kotest.assertions

import io.kotest.fp.Try
import io.kotest.matchers.shouldBe

infix fun <A, B> A.orThis(that: B): Pair<A, B> = this to that

infix fun <T, A : T, B : T> T.shouldBeEither(either: Pair<A, B>) {
   val previous = errorCollector.getCollectionMode()
   errorCollector.setCollectionMode(ErrorCollectionMode.Hard)

   val l = Try { this shouldBe either.first }
   val r = Try { this shouldBe either.second }

   errorCollector.setCollectionMode(previous)

   if (l.isFailure() && r.isFailure()) {
      val combined = MultiAssertionError(listOfNotNull(l.errorOrNull(), r.errorOrNull()))
      errorCollector.collectOrThrow(combined)
   } else {
      errorCollector.clear()
   }
}


infix fun <T, A : T> T.shouldBeThis(thing: A): Pair<T, A> = this to thing

infix fun <T, A : T, B : T> Pair<T, A>.orThat(that: B) = this.first.shouldBeEither(this.second to that)
