package io.kotest.fp

data class NonEmptyList<out A>(val head: A, val tail: List<A>) {
   companion object {
      fun <A> of(head: A, vararg rest: A): NonEmptyList<A> = NonEmptyList(head, rest.toList())
      fun <A> fromList(list: List<A>): Option<NonEmptyList<A>> = list.firstOrNone().fold(
         { Option.None },
         { first -> NonEmptyList(first, list.drop(1)).some() }
      )
   }

   val all: List<A> by lazy { listOf(head) + tail }

   fun <B> map(fn: (A) -> B): NonEmptyList<B> = NonEmptyList(fn(head), tail.map(fn))

   fun <B> flatMap(fn: (A) -> NonEmptyList<B>) = fromList(this.all.flatMap { fn(it).all }).orNull()!!

   operator fun plus(other: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
      NonEmptyList(this.head, (this.tail + other.all))

   operator fun plus(other: List<@UnsafeVariance A>): NonEmptyList<A> = NonEmptyList(head, tail + other)

   operator fun plus(other: Option<@UnsafeVariance A>): NonEmptyList<A> = NonEmptyList(head, tail + other.toList())

   operator fun plus(other: @UnsafeVariance A): NonEmptyList<A> = NonEmptyList(head, tail + other)

   fun <B> fold(acc: B, fn: (acc: B, next: A) -> B): B = this.tail.fold(fn(acc, this.head), fn)

   fun reduce(fn: (first: A, second: A) -> @UnsafeVariance A): A = this.tail.fold(head, fn)

   override fun toString(): String = all.toString()
}

fun <A> A.nel(): NonEmptyList<A> = NonEmptyList.of(this)
