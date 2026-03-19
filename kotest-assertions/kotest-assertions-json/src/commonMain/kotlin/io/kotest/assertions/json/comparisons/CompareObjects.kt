package io.kotest.assertions.json.comparisons

import io.kotest.assertions.json.*

internal fun compareObjects(
  path: List<String>,
  expected: JsonNode.ObjectNode,
  actual: JsonNode.ObjectNode,
  options: CompareJsonOptions,
): List<JsonError> {

   return buildList {
      if (FieldComparison.Strict == options.fieldComparison) {
         val expectedKeys = expected.elements.keys
         val actualKeys = actual.elements.keys
         val extraKeys = actualKeys - expectedKeys
         val missingKeys = expectedKeys - actualKeys

         when {
            (extraKeys.isNotEmpty() && missingKeys.isNotEmpty()) -> {
               add(JsonError.ObjectExtraAndMissingKeys(path, extraKeys, missingKeys))
            }
            (extraKeys.isNotEmpty()) -> {
               add(JsonError.ObjectExtraKeys(path, extraKeys))
            }
            (missingKeys.isNotEmpty()) -> {
               add(JsonError.ObjectMissingKeys(path, missingKeys))
            }
         }
      }

      // when using strict order mode, the order of elements in json matters, normally, we don't care
      when (options.propertyOrder) {
         PropertyOrder.Strict ->
            if(expected.elements.keys == actual.elements.keys) {
               expected.elements.entries.withIndex().zip(actual.elements.entries).forEach { (e, a) ->
                  if (a.key != e.value.key) add(JsonError.NameOrderDiff(path, e.index, e.value.key, a.key))
                  addAll(compare(path + a.key, e.value.value, a.value, options))
               }
            }

         PropertyOrder.Lenient -> {
            expected.elements.entries.forEach { (name, e) ->
               val a = actual.elements[name]
               when (a) {
                  null -> {
                     if (FieldComparison.Strict != options.fieldComparison) {
                        add(JsonError.ObjectMissingKeys(path, setOf(name)))
                     }
                  }
                  else -> {
                     addAll(compare(path + name, e, a, options))
                  }
               }
            }
         }
      }
   }
}
