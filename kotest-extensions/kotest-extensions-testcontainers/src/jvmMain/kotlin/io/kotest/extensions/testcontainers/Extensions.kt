package io.kotest.extensions.testcontainers

import io.kotest.core.TestConfiguration
import org.testcontainers.lifecycle.Startable

fun <T : Startable> T.perTest(): StartablePerTestListener<T> = StartablePerTestListener<T>(this)
fun <T : Startable> T.perSpec(): StartablePerSpecListener<T> = StartablePerSpecListener<T>(this)
fun <T : Startable> T.perProject(containerName: String): StartablePerProjectListener<T> = StartablePerProjectListener<T>(this, containerName)

fun <T : Startable> TestConfiguration.configurePerTest(startable: T): T {
   listener(StartablePerTestListener(startable))
   return startable
}

fun <T : Startable> TestConfiguration.configurePerSpec(startable: T): T {
   listener(StartablePerSpecListener(startable))
   return startable
}

fun <T : Startable> TestConfiguration.configurePerProject(startable: T, containerName: String): T {
   listener(StartablePerProjectListener(startable, containerName))
   return startable
}
