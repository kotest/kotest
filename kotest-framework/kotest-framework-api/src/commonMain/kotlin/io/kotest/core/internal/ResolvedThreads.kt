package io.kotest.core.internal

import io.kotest.core.spec.Spec

fun Spec.resolvedThreads() = this.threads() ?: this.threads ?: 1
