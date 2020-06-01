package io.kotest.extensions.testcontainers

import io.kotest.core.listeners.TestListener
import org.testcontainers.lifecycle.Startable

fun Startable.perTest(): TestListener = StartablePerTestListener(this)
fun Startable.perSpec(): TestListener = StartablePerSpecListener(this)
