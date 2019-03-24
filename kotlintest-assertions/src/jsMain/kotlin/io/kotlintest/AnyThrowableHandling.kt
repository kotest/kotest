package io.kotlintest

import kotlin.reflect.KClass

@PublishedApi
internal actual val KClass<*>.platformQualifiedName: String
  get() = this.simpleName.toString()