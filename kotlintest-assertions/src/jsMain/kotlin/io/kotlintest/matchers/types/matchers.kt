package io.kotlintest.matchers.types

import kotlin.reflect.KClass

@PublishedApi
internal actual inline fun <reified T : Any> T.isSubclassOf(klass: KClass<*>): Boolean {
  TODO()
}