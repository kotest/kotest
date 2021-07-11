package io.kotest.core.plan

import io.kotest.core.spec.DisplayName
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

actual fun KClass<*>.displayName(): String = annotation<DisplayName>()?.name ?: simpleName ?: this.toString()
