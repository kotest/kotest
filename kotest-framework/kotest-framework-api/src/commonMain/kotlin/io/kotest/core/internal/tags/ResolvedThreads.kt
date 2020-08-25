package io.kotest.core.internal.tags

import io.kotest.core.spec.Spec

fun Spec.resolvedThreads() = this.threads() ?: this.threads ?: 1
