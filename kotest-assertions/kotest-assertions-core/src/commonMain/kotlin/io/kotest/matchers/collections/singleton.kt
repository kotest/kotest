package io.kotest.matchers.collections

/**
 * Verifies this collection contains only one element
 *
 * This assertion is an alias to `collection shouldHaveSize 1`. This will pass if the collection have exactly one element
 * (definition of a Singleton Collection)
 *
 * ```
 * listOf(1).shouldBeSingleton()    // Assertion passes
 * listOf(1, 2).shouldBeSingleton() // Assertion fails
 * ```
 *
 * @see [shouldHaveSize]
 * @see [shouldNotBeSingleton]
 * @see [shouldHaveSingleElement]
 */
@IgnorableReturnValue
fun <T, C : Collection<T>> C.shouldBeSingleton(): C {
   this shouldHaveSize 1
   return this
}

@IgnorableReturnValue
fun <T, I : Iterable<T>> I.shouldBeSingleton(): I {
   toList().shouldBeSingleton()
   return this
}

@IgnorableReturnValue
fun <T> Array<T>.shouldBeSingleton(): Array<T> {
   asList().shouldBeSingleton()
   return this
}


/**
 * Verifies this collection doesn't contain only one element
 *
 * This assertion is an alias to `collection shouldNotHaveSize 1`. This will pass if the collection doesn't have exactly one element
 * (definition of a Singleton Collection)
 *
 * ```
 * listOf(1, 2).shouldNotBeSingleton()    // Assertion passes
 * listOf<Int>().shouldNotBeSingleton()   // Assertion passes
 * listOf(1).shouldNotBeSingleton()       // Assertion fails
 * ```
 *
 * @see [shouldNotHaveSize]
 * @see [shouldBeSingleton]
 * @see [shouldNotHaveSingleElement]
 */
@IgnorableReturnValue
fun <T, C : Collection<T>> C.shouldNotBeSingleton(): C {
   this shouldNotHaveSize 1
   return this
}

@IgnorableReturnValue
fun <T, I : Iterable<T>> I.shouldNotBeSingleton(): I {
   toList().shouldNotBeSingleton()
   return this

}

@IgnorableReturnValue
fun <T> Array<T>.shouldNotBeSingleton(): Array<T> {
   asList().shouldNotBeSingleton()
   return this
}


/**
 * Verifies this collection contains exactly one element and returns it.
 *
 * Analogous to the standard library's [Collection.single], but raises an
 * assertion error rather than a [NoSuchElementException] / [IllegalArgumentException]
 * when the collection is empty or contains more than one element.
 *
 * ```
 * val only = listOf(1).shouldBeSingle() // returns 1
 * listOf(1, 2).shouldBeSingle()         // Assertion fails
 * ```
 *
 * @see [shouldBeSingleton]
 */
@IgnorableReturnValue
fun <T, C : Collection<T>> C.shouldBeSingle(): T {
   this shouldHaveSize 1
   return this.single()
}

@IgnorableReturnValue
fun <T, I : Iterable<T>> I.shouldBeSingle(): T = toList().shouldBeSingle()

@IgnorableReturnValue
fun <T> Array<T>.shouldBeSingle(): T = asList().shouldBeSingle()


/**
 * Verifies this collection does not contain exactly one element.
 *
 * This will pass for empty collections and for collections with two or more elements.
 * Mirrors the negated form of [shouldBeSingle]; returns the receiver to allow chaining.
 *
 * ```
 * listOf<Int>().shouldNotBeSingle()   // Assertion passes
 * listOf(1, 2).shouldNotBeSingle()    // Assertion passes
 * listOf(1).shouldNotBeSingle()       // Assertion fails
 * ```
 *
 * @see [shouldBeSingle]
 * @see [shouldNotBeSingleton]
 */
@IgnorableReturnValue
fun <T, C : Collection<T>> C.shouldNotBeSingle(): C {
   this shouldNotHaveSize 1
   return this
}

@IgnorableReturnValue
fun <T, I : Iterable<T>> I.shouldNotBeSingle(): I {
   toList().shouldNotBeSingle()
   return this
}

@IgnorableReturnValue
fun <T> Array<T>.shouldNotBeSingle(): Array<T> {
   asList().shouldNotBeSingle()
   return this
}


/**
 * Verifies this collection contains only one element and executes the given lambda against that element.
 */
@IgnorableReturnValue
inline fun <T, C : Collection<T>> C.shouldBeSingleton(fn: (T) -> Unit): C {
   this.shouldBeSingleton()
   fn(this.first())
   return this
}

/**
 * Verifies this collection contains only one element and executes the given lambda against that element.
 */
@IgnorableReturnValue
inline fun <T, I : Iterable<T>> I.shouldBeSingleton(fn: (T) -> Unit): I {
   toList().shouldBeSingleton(fn)
   return this
}

/**
 * Verifies this collection contains only one element and executes the given lambda against that element.
 */
@IgnorableReturnValue
inline fun <T> Array<T>.shouldBeSingleton(fn: (T) -> Unit): Array<T> {
   asList().shouldBeSingleton(fn)
   return this
}



