package io.kotest.extensions.testcontainers

import org.testcontainers.lifecycle.Startable

fun <T : Startable> T.perTest(): StartablePerTestListener<T> = StartablePerTestListener<T>(this)
fun <T : Startable> T.perSpec(): StartablePerSpecListener<T> = StartablePerSpecListener<T>(this)
