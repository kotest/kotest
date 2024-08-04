package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.*

internal fun compareArrays(
  path: List<String>,
  expected: JsonNode.ArrayNode,
  actual: JsonNode.ArrayNode,
  options: CompareJsonOptions,
): JsonError? {

   if (expected.elements.size != actual.elements.size)
      return JsonError.UnequalArrayLength(path, expected.elements.size, actual.elements.size)

   when (options.arrayOrder) {
      ArrayOrder.Strict -> {
         expected.elements.withIndex().zip(actual.elements.withIndex()).forEach { (a, b) ->
            val error = compare(path + "[${a.index}]", a.value, b.value, options)
            if (error != null) return error
         }
      }

      /**
       * In [ArrayOrder.Lenient], we try to allow array contents to be out-of-order.
       * We do this by searching for a match for each element in [actual], in the [expected] array,
       * flagging used matches so they can't be used twice. This will probably be slow for very big arrays.
       */
      ArrayOrder.Lenient -> {

         val consumedIndexes = BooleanArray(expected.elements.size) { false }

         fun availableIndexes() = consumedIndexes
            .mapIndexed { index, isConsumed -> if (!isConsumed) index else null }
            .filterNotNull()

         fun findMatchingIndex(element: JsonNode): Int? {
            for (i in availableIndexes()) {
               // Comparison with no error -> matching element
               val isMatch = compare(path + "[$i]", expected.elements[i], element, options) == null

               if (isMatch) {
                  return i
               }
            }

            return null
         }

         for ((i, element) in actual.elements.withIndex()) {
            val match = findMatchingIndex(element)
               ?: return JsonError.UnequalArrayContent(path + "[$i]", expected, element)

            consumedIndexes[match] = true
         }
      }
   }

   return null
}
