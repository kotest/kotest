package io.kotest.extensions.testcontainers

import io.kotest.engine.spec.TestSuite
import org.testcontainers.lifecycle.Startable

fun <T : Startable> T.perTest(): StartablePerTestListener<T> = StartablePerTestListener<T>(this)
fun <T : Startable> T.perSpec(): StartablePerSpecListener<T> = StartablePerSpecListener<T>(this)

fun <T : Startable> TestSuite.configurePerTest(startable: T): T {
   listener(StartablePerTestListener(startable))
   return startable
}

fun <T : Startable> TestSuite.configurePerSpec(startable: T): T {
   listener(StartablePerSpecListener(startable))
   return startable
}
