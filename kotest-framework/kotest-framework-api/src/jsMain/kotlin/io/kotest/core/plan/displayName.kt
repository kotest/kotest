package io.kotest.core.plan

import io.kotest.mpp.bestName
import kotlin.reflect.KClass

actual fun KClass<*>.displayName(): DisplayName = DisplayName(bestName())
