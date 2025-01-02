@file:Suppress("DEPRECATION")

package io.kotest.engine.concurrency

import io.kotest.core.annotation.DoNotParallelize
import io.kotest.core.annotation.Isolate
import io.kotest.mpp.IncludingAnnotations
import io.kotest.mpp.IncludingSuperclasses
import io.kotest.mpp.hasAnnotation
import kotlin.reflect.KClass


