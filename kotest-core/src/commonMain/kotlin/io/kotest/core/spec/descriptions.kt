package io.kotest.core.spec

import io.kotest.core.test.toDescription
import kotlin.reflect.KClass

@Deprecated("Use klass.toDescription(); will be removed once plugin is updated")
fun KClass<*>.description() = (this as KClass<out Spec>).toDescription()
