package io.kotest.core

import io.kotest.core.specs.SpecContainer

actual fun failureFirstSort(classes: List<SpecContainer>): List<SpecContainer> =
   LexicographicSpecExecutionOrder.sort(classes)
