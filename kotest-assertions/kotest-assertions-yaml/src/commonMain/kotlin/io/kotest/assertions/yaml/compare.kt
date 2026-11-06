package io.kotest.assertions.yaml

import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlNull
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.YamlTaggedNode

/**
 * Compares two YAML trees, returning a detailed list of differences.
 *
 * The comparison semantics mirror the JSON matchers' engine (see `kotest-assertions-json`),
 * driven by [CompareYamlOptions]: property order, array order, field comparison strictness
 * and number format leniency.
 */
internal fun compare(
   path: List<String>,
   expectedNode: YamlNode,
   actualNode: YamlNode,
   options: CompareYamlOptions,
): List<YamlError> {
   // tags are metadata only, content equality ignores them
   val expected = if (expectedNode is YamlTaggedNode) expectedNode.innerNode else expectedNode
   val actual = if (actualNode is YamlTaggedNode) actualNode.innerNode else actualNode

   return when {
      expected is YamlMap -> when (actual) {
         is YamlMap -> compareYamlMaps(path, expected, actual, options)
         else -> listOf(YamlError.ExpectedMap(path, actual))
      }

      expected is YamlList -> when (actual) {
         is YamlList -> compareYamlLists(path, expected, actual, options)
         else -> listOf(YamlError.ExpectedList(path, actual))
      }

      expected is YamlNull -> if (actual is YamlNull) emptyList() else listOf(YamlError.ExpectedNull(path, actual))
      expected is YamlScalar && actual is YamlScalar -> compareYamlScalars(path, expected, actual, options)

      else -> listOf(YamlError.IncompatibleTypes(path, expected.type(), actual.type()))
   }
}

private fun compareYamlScalars(
   path: List<String>,
   expected: YamlScalar,
   actual: YamlScalar,
   options: CompareYamlOptions,
): List<YamlError> {
   val equal = when (options.numberFormat) {
      NumberFormat.Lenient -> lenientScalarEquals(expected.content, actual.content)
      NumberFormat.Strict -> expected.content == actual.content
   }
   return if (equal) emptyList() else listOf(YamlError.UnequalValues(path, expected.content, actual.content))
}

private fun compareYamlLists(
   path: List<String>,
   expected: YamlList,
   actual: YamlList,
   options: CompareYamlOptions,
): List<YamlError> {

   if (expected.items.size != actual.items.size)
      return listOf(YamlError.UnequalArrayLength(path, expected.items.size, actual.items.size))

   return buildList {
      when (options.arrayOrder) {
         ArrayOrder.Strict -> {
            expected.items.withIndex().zip(actual.items.withIndex()).forEach { (a, b) ->
               addAll(compare(path + "[${a.index}]", a.value, b.value, options))
            }
         }

         /**
          * In [ArrayOrder.Lenient], we try to allow array contents to be out-of-order.
          * We do this by searching for a match for each element in [actual], in the [expected] array,
          * flagging used matches so they can't be used twice. This will probably be slow for very big arrays.
          */
         ArrayOrder.Lenient -> {

            val consumedIndexes = BooleanArray(expected.items.size) { false }

            fun availableIndexes() = consumedIndexes
               .mapIndexed { index, isConsumed -> if (!isConsumed) index else null }
               .filterNotNull()

            fun findMatchingIndex(element: YamlNode): Int? {
               for (i in availableIndexes()) {
                  // Comparison with no error -> matching element
                  val isMatch = compare(path + "[$i]", expected.items[i], element, options).isEmpty()

                  if (isMatch) {
                     return i
                  }
               }

               return null
            }

            for ((i, element) in actual.items.withIndex()) {
               val match = findMatchingIndex(element)

               when (match) {
                  null -> add(YamlError.UnequalArrayContent(path + "[$i]", expected, element))
                  else -> consumedIndexes[match] = true
               }
            }
         }
      }
   }
}

private fun compareYamlMaps(
   path: List<String>,
   expected: YamlMap,
   actual: YamlMap,
   options: CompareYamlOptions,
): List<YamlError> {

   // kaml keys are YamlScalars but duplicate key contents are rejected by kaml on parse,
   // so mapping key contents to values is lossless
   val expectedEntries: List<Pair<String, YamlNode>> = expected.entries.entries.map { it.key.content to it.value }
   val actualEntries: List<Pair<String, YamlNode>> = actual.entries.entries.map { it.key.content to it.value }
   val actualEntriesByKey: Map<String, YamlNode> = actualEntries.toMap()

   return buildList {
      if (FieldComparison.Strict == options.fieldComparison) {
         val expectedKeys = expectedEntries.map { it.first }.toSet()
         val actualKeys = actualEntries.map { it.first }.toSet()
         val extraKeys = actualKeys - expectedKeys
         val missingKeys = expectedKeys - actualKeys

         when {
            (extraKeys.isNotEmpty() && missingKeys.isNotEmpty()) -> {
               add(YamlError.ObjectExtraAndMissingKeys(path, extraKeys, missingKeys))
            }
            (extraKeys.isNotEmpty()) -> {
               add(YamlError.ObjectExtraKeys(path, extraKeys))
            }
            (missingKeys.isNotEmpty()) -> {
               add(YamlError.ObjectMissingKeys(path, missingKeys))
            }
         }
      }

      // when using strict order mode, the order of properties in the mapping matters, normally, we don't care
      when (options.propertyOrder) {
         PropertyOrder.Strict -> {
            if (expectedEntries.map { it.first } == actualEntries.map { it.first }) {
               expectedEntries.zip(actualEntries).forEach { (e, a) ->
                  addAll(compare(path + e.first, e.second, a.second, options))
               }
            } else {
               // The key sets (or their order) differ. The key-set differences are already reported
               // above when FieldComparison.Strict is in effect. We still report positional name-order
               // differences and, crucially, compare the values of keys present in both mappings so that
               // genuine value mismatches are not hidden by the key-order / key-set error.
               expectedEntries.withIndex().zip(actualEntries).forEach { (e, a) ->
                  if (a.first != e.value.first)
                     add(YamlError.NameOrderDiff(path, e.index, e.value.first, a.first))
               }
               expectedEntries.forEach { (key, e) ->
                  actualEntriesByKey[key]?.let { a ->
                     addAll(compare(path + key, e, a, options))
                  }
               }
            }
         }

         PropertyOrder.Lenient -> {
            expectedEntries.forEach { (key, e) ->
               val a = actualEntriesByKey[key]
               when (a) {
                  null -> {
                     if (FieldComparison.Strict != options.fieldComparison) {
                        add(YamlError.ObjectMissingKeys(path, setOf(key)))
                     }
                  }
                  else -> {
                     addAll(compare(path + key, e, a, options))
                  }
               }
            }
         }
      }
   }
}

/**
 * Returns a simple type name for a YAML node, used in error messages.
 */
internal fun YamlNode.type(): String = when (this) {
   is YamlNull -> "null"
   is YamlScalar -> "scalar"
   is YamlList -> "list"
   is YamlMap -> "map"
   is YamlTaggedNode -> this.innerNode.type()
}

internal fun show(node: YamlNode): String = when (node) {
   is YamlNull -> "null"
   is YamlScalar -> node.content
   is YamlList -> "[" + node.items.joinToString(", ") { show(it) } + "]"
   is YamlMap -> "{" + node.entries.entries.joinToString(", ") { show(it.key) + ": " + show(it.value) } + "}"
   is YamlTaggedNode -> show(node.innerNode)
}

private val numberRegex = """-?([1-9]\d*|0)(\.\d+)?([eE][+-]?\d+)?""".toRegex()
private val exponentRegex = """.+[eE][+-]?\d+""".toRegex()
private val fractionalZeroesRegex = """(\.\d*)0+$""".toRegex()

/**
 * Lenient scalar comparison, mirroring the JSON matchers' `NumberFormat.Lenient`:
 * contents that both parse as JSON-style numbers are compared by parsed value,
 * everything else by exact content (kaml does not retain whether a scalar was quoted).
 */
internal fun lenientScalarEquals(expected: String, actual: String): Boolean {

   if (expected == actual) return true

   if (!expected.matches(numberRegex) || !actual.matches(numberRegex)) return false

   val thisDouble = expected.toDouble()
   val otherDouble = actual.toDouble()
   if (
      (thisDouble == 0.0 || thisDouble == -0.0) &&
      (otherDouble == 0.0 || otherDouble == -0.0)
   ) return true

   // if one or the other uses exponent notation, we must compare by parsed value
   if (expected.matches(exponentRegex) xor actual.matches(exponentRegex)) {
      return thisDouble == otherDouble
   }

   /**
    * Removes insignificant part of a number. e.g. 1.0 -> 1 or 3.1400 -> 3.14
    */
   fun trimInsignificant(value: String): String =
      value.replace(fractionalZeroesRegex) { it.groupValues[1].trimEnd('0') }
         .trimEnd('.')

   return trimInsignificant(expected) == trimInsignificant(actual)
}
