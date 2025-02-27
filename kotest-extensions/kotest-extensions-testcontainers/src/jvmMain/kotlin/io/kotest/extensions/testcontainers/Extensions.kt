package io.kotest.extensions.testcontainers

import io.kotest.core.TestConfiguration
import org.testcontainers.lifecycle.Startable

fun <T : Startable> T.perTest(): StartablePerTestListener<T> = StartablePerTestListener(this)

fun <T : Startable> T.perSpec(): StartablePerSpecListener<T> = StartablePerSpecListener(this)

fun <T : Startable> T.perProject(): StartablePerProjectListener<T> = StartablePerProjectListener(this)

@Deprecated("use perProject()")
fun <T : Startable> T.perProject(containerName: String): StartablePerProjectListener<T> =
   StartablePerProjectListener<T>(this)

@Deprecated("use perTest")
fun <T : Startable> TestConfiguration.configurePerTest(startable: T): T {
   extension(StartablePerTestListener(startable))
   return startable
}

@Deprecated("use perSpec")
fun <T : Startable> TestConfiguration.configurePerSpec(startable: T): T {
   extension(StartablePerSpecListener(startable))
   return startable
}

@Deprecated("use perProject")
fun <T : Startable> TestConfiguration.configurePerProject(startable: T, containerName: String): T {
   extension(StartablePerProjectListener(startable))
   return startable
}
