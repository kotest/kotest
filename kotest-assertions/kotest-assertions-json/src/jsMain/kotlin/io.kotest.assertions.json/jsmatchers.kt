@file:Suppress("unused")

package io.kotest.assertions.json

import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

/**
 * Verifies that the [expected] string is valid json, and that it matches this string.
 *
 * This matcher will consider two json strings matched if they have the same key-values pairs,
 * regardless of order.
 *
 */
actual fun String.shouldEqualJson(expected: String, mode: CompareMode, order: CompareOrder) {
   val (e, a) = parse(expected, this)
   a should equalJson(e, mode, order)
}

actual fun String.shouldNotEqualJson(expected: String, mode: CompareMode, order: CompareOrder) {
   val (e, a) = parse(expected, this)
   a shouldNot equalJson(e, mode, order)
}

internal fun parse(expected: String, actual: String): Pair<JsonTree, JsonTree> {
   val enode = toJsonNode(JSON.parse(expected))
   val anode = toJsonNode(JSON.parse(actual))
   val e = JsonTree(enode, JSON.stringify(JSON.parse(expected), space = 2))
   val a = JsonTree(anode, JSON.stringify(JSON.parse(actual), space = 2))
   return Pair(e, a)
}
