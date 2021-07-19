package io.kotest.engine.spec

import io.kotest.core.spec.DisplayNameAnno
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

actual fun KClass<*>.displayName(): String? = annotation<DisplayNameAnno>()?.name
