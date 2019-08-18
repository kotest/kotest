package io.kotlintest.properties

expect inline fun <reified T> Gen.Companion.default(): Gen<T>
