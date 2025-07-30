package io.kotest.inspectors

/** Alias for [Sequence.forAll] */
inline fun <T> Sequence<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)

/** Alias for [Array.forAll] */
inline fun <T> Array<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)

/** Alias for [Collection.forAll] */
inline fun <T> Collection<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)


/** Alias for [Sequence.forOne] */
inline fun <T> Sequence<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)

/** Alias for [Array.forOne] */
inline fun <T> Array<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)

/** Alias for [Collection.forOne] */
inline fun <T> Collection<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)


/** Alias for [Sequence.forExactly] */
inline fun <T> Sequence<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)

/** Alias for [Array.forExactly] */
inline fun <T> Array<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)

/** Alias for [Collection.forExactly] */
inline fun <T> Collection<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)


/** Alias for [Sequence.forSome] */
inline fun <T> Sequence<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)

/** Alias for [Array.forSome] */
inline fun <T> Array<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)

/** Alias for [Collection.forSome] */
inline fun <T> Collection<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)


/** Alias for [Sequence.forAny] */
inline fun <T> Sequence<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)

/** Alias for [Array.forAny] */
inline fun <T> Array<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)

/** Alias for [Collection.forAny] */
inline fun <T> Collection<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)


/** Alias for [Sequence.forAtLeastOne] */
inline fun <T> Sequence<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)

/** Alias for [Array.forAtLeastOne] */
inline fun <T> Array<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)

/** Alias for [Collection.forAtLeastOne] */
inline fun <T> Collection<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)


/** Alias for [Sequence.forAtLeast] */
inline fun <T> Sequence<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)

/** Alias for [Array.forAtLeast] */
inline fun <T> Array<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)

/** Alias for [Collection.forAtLeast] */
inline fun <T> Collection<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)


/** Alias for [Sequence.forAtMostOne] */
inline fun <T> Sequence<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)

/** Alias for [Array.forAtMostOne] */
inline fun <T> Array<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)

/** Alias for [Collection.forAtMostOne] */
inline fun <T> Collection<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)


/** Alias for [Sequence.forAtMost] */
inline fun <T> Sequence<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)

/** Alias for [Array.forAtMost] */
inline fun <T> Array<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)

/** Alias for [Collection.forAtMost] */
inline fun <T> Collection<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)


/** Alias for [Sequence.forNone] */
inline fun <T> Sequence<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)

/** Alias for [Array.forNone] */
inline fun <T> Array<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)

/** Alias for [Collection.forNone] */
inline fun <T> Collection<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)
