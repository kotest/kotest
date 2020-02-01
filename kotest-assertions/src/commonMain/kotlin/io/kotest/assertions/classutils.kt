package io.kotest.assertions

import kotlin.reflect.KClass

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the name, or <none>.
 */
fun KClass<*>.bestName(): String = fqn() ?: simpleName ?: "<none>"

/**
 * Returns the fully qualified name for this class, or null
 */
expect fun KClass<*>.fqn(): String?
