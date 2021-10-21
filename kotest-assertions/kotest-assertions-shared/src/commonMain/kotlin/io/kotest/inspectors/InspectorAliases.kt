package io.kotest.inspectors

/** Alias for [Sequence.forAll] */
fun <T> Sequence<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)
/** Alias for [Array.forAll] */
fun <T> Array<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)
/** Alias for [Collection.forAll] */
fun <T> Collection<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)


/** Alias for [Sequence.forOne] */
fun <T> Sequence<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)
/** Alias for [Array.forOne] */
fun <T> Array<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)
/** Alias for [Collection.forOne] */
fun <T> Collection<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)


/** Alias for [Sequence.forExactly] */
fun <T> Sequence<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)
/** Alias for [Array.forExactly] */
fun <T> Array<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)
/** Alias for [Collection.forExactly] */
fun <T> Collection<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)


/** Alias for [Sequence.forSome] */
fun <T> Sequence<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)
/** Alias for [Array.forSome] */
fun <T> Array<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)
/** Alias for [Collection.forSome] */
fun <T> Collection<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)


/** Alias for [Sequence.forAny] */
fun <T> Sequence<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)
/** Alias for [Array.forAny] */
fun <T> Array<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)
/** Alias for [Collection.forAny] */
fun <T> Collection<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)


/** Alias for [Sequence.forAtLeastOne] */
fun <T> Sequence<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)
/** Alias for [Array.forAtLeastOne] */
fun <T> Array<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)
/** Alias for [Collection.forAtLeastOne] */
fun <T> Collection<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)


/** Alias for [Sequence.forAtLeast] */
fun <T> Sequence<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)
/** Alias for [Array.forAtLeast] */
fun <T> Array<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)
/** Alias for [Collection.forAtLeast] */
fun <T> Collection<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)


/** Alias for [Sequence.forAtMostOne] */
fun <T> Sequence<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)
/** Alias for [Array.forAtMostOne] */
fun <T> Array<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)
/** Alias for [Collection.forAtMostOne] */
fun <T> Collection<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)


/** Alias for [Sequence.forAtMost] */
fun <T> Sequence<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)
/** Alias for [Array.forAtMost] */
fun <T> Array<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)
/** Alias for [Collection.forAtMost] */
fun <T> Collection<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)

/** Alias for [Sequence.forNone] */
fun <T> Sequence<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)
/** Alias for [Array.forNone] */
fun <T> Array<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)
/** Alias for [Collection.forNone] */
fun <T> Collection<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)
