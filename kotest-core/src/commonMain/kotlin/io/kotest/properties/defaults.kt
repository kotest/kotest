package io.kotest.properties

@Deprecated("Deprecated and will be removed in 5.0. Migrate to the new property test classes in 4.0")
expect inline fun <reified T> Gen.Companion.default(): Gen<T>
