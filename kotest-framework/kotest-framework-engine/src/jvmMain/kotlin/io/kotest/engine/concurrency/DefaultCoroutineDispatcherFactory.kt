package io.kotest.engine.concurrency

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.ProjectConfiguration

internal actual fun defaultCoroutineDispatcherFactory(configuration: ProjectConfiguration): CoroutineDispatcherFactory =
   FixedThreadCoroutineDispatcherFactory(configuration.parallelism, configuration.dispatcherAffinity)
