package io.kotest.engine

import io.kotest.common.reflection.reflection
import kotlin.reflect.KClass

/**
 * Returns an instance of this KClass via a no-arg default constructor or if this kclass
 * is an object, will return the object instance.
 *
 * Note: JVM only
 */
fun <T : Any> KClass<T>.newInstanceNoArgConstructorOrObjectInstance(): T =
   reflection.newInstanceNoArgConstructorOrObjectInstance(this)

/**
 * Returns an instance of this KClass via a no-arg default constructor.
 * Will error if no such constructor exists.
 *
 * Note: JVM only
 */
fun <T : Any> KClass<T>.newInstanceNoArgConstructor(): T = reflection.newInstanceNoArgConstructor(this)
