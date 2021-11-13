package io.kotest.core.listeners

import io.kotest.core.spec.Spec

interface InstantiationListener {
   suspend fun specInstantiated(spec: Spec)
}
