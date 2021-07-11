package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

actual fun failureFirstSort(classes: List<KClass<out Spec>>): List<KClass<out Spec>> = classes
