package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.*

internal fun compareObjects(
  path: List<String>,
  expected: JsonNode.ObjectNode,
  actual: JsonNode.ObjectNode,
  options: CompareJsonOptions,
): JsonError? {

   if (FieldComparison.Strict == options.fieldComparison) {
      val expectedKeys = expected.elements.keys
      val actualKeys = actual.elements.keys

      if (actualKeys.size > expectedKeys.size) {
         val extra = actualKeys - expectedKeys
         return JsonError.ObjectExtraKeys(path, extra)
      }

      if (actualKeys.size < expectedKeys.size) {
         val missing = expectedKeys - actualKeys
         return JsonError.ObjectMissingKeys(path, missing)
      }
   }

   // when using strict order mode, the order of elements in json matters, normally, we don't care
   when (options.propertyOrder) {
      PropertyOrder.Strict ->
         expected.elements.entries.withIndex().zip(actual.elements.entries).forEach { (e, a) ->
            if (a.key != e.value.key) return JsonError.NameOrderDiff(path, e.index, e.value.key, a.key)
            val error = compare(path + a.key, e.value.value, a.value, options)
            if (error != null) return error
         }

      PropertyOrder.Lenient ->
         expected.elements.entries.forEach { (name, e) ->
            val a = actual.elements[name] ?: return JsonError.ObjectMissingKeys(path, setOf(name))
            val error = compare(path + name, e, a, options)
            if (error != null) return error
         }
   }

   return null
}
