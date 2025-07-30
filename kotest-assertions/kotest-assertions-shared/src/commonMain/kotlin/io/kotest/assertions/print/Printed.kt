package io.kotest.assertions.print

import kotlin.reflect.KClass

/**
 * Represents a value that has been appropriately formatted
 * for display in assertion error messages.
 *
 * For example, a null might be formatted as <null>, whitespace
 * may be escaped and the empty string may be quoted.
 *
 * Optionally, a type can be provided to indicate the original type of the value.
 */
data class Printed(val value: String, val type: KClass<*>?) {
   constructor(value: String) : this(value, null)
}
