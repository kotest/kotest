package io.kotest.core.spec

import io.kotest.mpp.annotation
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

/**
 * Returns the [DisplayName] for this kclass.
 *
 * This is the value of the @DisplayName annotation if present, otherwise the full class name,
 * or failing that, the simple class name.
 */
@PublishedApi
internal fun displayName(kClass: KClass<*>): String =
   kClass.annotation<DisplayName>()?.name ?: kClass.bestName()
