@file:JvmName("DataDrivenTestingJvm")
package io.kotlintest.data

import kotlin.reflect.jvm.reflect

internal actual val Function<*>.paramNames
  get() = reflect()?.parameters?.mapNotNull { it.name } ?: emptyList()