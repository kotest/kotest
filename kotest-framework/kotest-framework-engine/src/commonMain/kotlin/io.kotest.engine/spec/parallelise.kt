package io.kotest.engine.spec

import io.kotest.core.spec.DoNotParallelize
import io.kotest.mpp.annotation
import kotlin.reflect.KClass

fun KClass<*>.isDoNotParallelize(): Boolean = annotation<DoNotParallelize>() != null
