package io.kotest.properties

expect inline fun <reified T> Gen.Companion.default(): Gen<T>
