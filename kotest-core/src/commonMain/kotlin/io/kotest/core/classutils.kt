package io.kotest.core

import io.kotest.fp.Option
import io.kotest.fp.getOrElse
import io.kotest.fp.orElse
import io.kotest.fp.toOption
import kotlin.reflect.KClass

/**
 * Returns the longest possible name available for this class.
 * That is, in order, the FQN, the name, or <none>.
 */
fun KClass<*>.bestName(): String = fqn().orElse(simpleName.toOption()).getOrElse("<none>")

/**
 * Returns the fully qualified name for this class, or none.
 */
expect fun KClass<*>.fqn(): Option<String>
