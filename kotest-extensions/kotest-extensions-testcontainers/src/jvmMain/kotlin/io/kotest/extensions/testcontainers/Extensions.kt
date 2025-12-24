package io.kotest.extensions.testcontainers

import io.kotest.core.TestConfiguration
import org.testcontainers.lifecycle.Startable

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> T.perTest(): StartablePerTestListener<T> = StartablePerTestListener(this)

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> T.perSpec(): StartablePerSpecListener<T> = StartablePerSpecListener(this)

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> T.perProject(): StartablePerProjectListener<T> = StartablePerProjectListener(this)

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> T.perProject(containerName: String): StartablePerProjectListener<T> =
   StartablePerProjectListener<T>(this)

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> TestConfiguration.configurePerTest(startable: T): T {
   extension(StartablePerTestListener(startable))
   return startable
}

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> TestConfiguration.configurePerSpec(startable: T): T {
   extension(StartablePerSpecListener(startable))
   return startable
}

@Deprecated("Use TestContainerProjectExtension or TestContainerSpecExtension instead. Will be removed in 6.2")
fun <T : Startable> TestConfiguration.configurePerProject(startable: T, containerName: String): T {
   extension(StartablePerProjectListener(startable))
   return startable
}
