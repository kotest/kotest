package io.kotest.core.plan

import io.kotest.core.spec.DisplayNameAnno
import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

actual fun KClass<*>.displayName(): DisplayName =
   DisplayName(annotation<DisplayNameAnno>()?.name ?: bestName())
