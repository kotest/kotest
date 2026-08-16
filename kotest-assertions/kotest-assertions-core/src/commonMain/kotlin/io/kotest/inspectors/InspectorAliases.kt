package io.kotest.inspectors

/** Alias for [Sequence.forAll] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)

/** Alias for [Array.forAll] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)

/** Alias for [Collection.forAll] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForAll(fn: (T) -> Unit) = forAll(fn)


/** Alias for [Sequence.forOne] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)

/** Alias for [Array.forOne] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)

/** Alias for [Collection.forOne] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForOne(fn: (T) -> Unit) = forOne(fn)


/** Alias for [Sequence.forExactly] */
@IgnorableReturnValue
inline fun <T> Sequence<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)

/** Alias for [Array.forExactly] */
@IgnorableReturnValue
inline fun <T> Array<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)

/** Alias for [Collection.forExactly] */
@IgnorableReturnValue
inline fun <T> Collection<T>.shouldForExactly(k: Int, fn: (T) -> Unit) = forExactly(k, fn)


/** Alias for [Sequence.forSome] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)

/** Alias for [Array.forSome] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)

/** Alias for [Collection.forSome] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForSome(fn: (T) -> Unit) = forSome(fn)


/** Alias for [Sequence.forAny] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)

/** Alias for [Array.forAny] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)

/** Alias for [Collection.forAny] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForAny(fn: (T) -> Unit) = forAny(fn)


/** Alias for [Sequence.forAtLeastOne] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)

/** Alias for [Array.forAtLeastOne] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)

/** Alias for [Collection.forAtLeastOne] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForAtLeastOne(fn: (T) -> Unit) = forAtLeastOne(fn)


/** Alias for [Sequence.forAtLeast] */
@IgnorableReturnValue
inline fun <T> Sequence<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)

/** Alias for [Array.forAtLeast] */
@IgnorableReturnValue
inline fun <T> Array<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)

/** Alias for [Collection.forAtLeast] */
@IgnorableReturnValue
inline fun <T> Collection<T>.shouldForAtLeast(k: Int, fn: (T) -> Unit) = forAtLeast(k, fn)


/** Alias for [Sequence.forAtMostOne] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)

/** Alias for [Array.forAtMostOne] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)

/** Alias for [Collection.forAtMostOne] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForAtMostOne(fn: (T) -> Unit) = forAtMostOne(fn)


/** Alias for [Sequence.forAtMost] */
@IgnorableReturnValue
inline fun <T> Sequence<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)

/** Alias for [Array.forAtMost] */
@IgnorableReturnValue
inline fun <T> Array<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)

/** Alias for [Collection.forAtMost] */
@IgnorableReturnValue
inline fun <T> Collection<T>.shouldForAtMost(k: Int, fn: (T) -> Unit) = forAtMost(k, fn)


/** Alias for [Sequence.forNone] */
@IgnorableReturnValue
inline infix fun <T> Sequence<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)

/** Alias for [Array.forNone] */
@IgnorableReturnValue
inline infix fun <T> Array<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)

/** Alias for [Collection.forNone] */
@IgnorableReturnValue
inline infix fun <T> Collection<T>.shouldForNone(fn: (T) -> Unit) = forNone(fn)
