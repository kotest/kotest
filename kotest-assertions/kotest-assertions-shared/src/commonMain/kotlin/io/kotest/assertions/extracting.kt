package io.kotest.assertions

import io.kotest.inspectors.forOne

/**
 * `extracting` pulls property values out of a list of objects for _typed_ bulk assertions on properties.
 *
 * The **simple example** shows how `extracting` helps with disjunct collection assertions:
 * ```
 * extracting(persons){ name }
 *   .shouldContainAll("John Doe", "Samantha Roes")
 * ```
 *
 * This is similar to using multiple [forOne] however allows for a more concise notation.
 * ```
 * forOne(persons){ it.name shouldBe "John Doe" }
 * forOne(persons){ it.name shouldBe "Samantha Rose" }
 * ```
 *
 * `extracting` also allows to define complex return types shown in this **elaborate example**:
 * ```
 * extracting(persons){ Pair(name, age) }
 *   .shouldContainAll(
 *     Pair("John Doe", 20),
 *     Pair("Samantha Roes", 19)
 *   )
 * ```
 * @param col the collection of objects from which to extract the properties
 * @param extractor the extractor that defines _which_ properties are returned
 * @author Hannes Thaller
 */
fun <K, T> extracting(col: Collection<K>, extractor: K.() -> T): List<T> {
   return col.map(extractor)
}
