package io.kotlintest

fun <T> forAll(array: Array<T>, fn: (T) -> Unit) = forAll(array.asList(), fn)

fun <T> forAll(col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filter { it.second == null }
  if (passed.size < col.size) {
    val msg = "${passed.size} elements passed but expected ${col.size}"
    buildAssertionError(msg, results)
  }
}

fun <T> forOne(array: Array<T>, fn: (T) -> Unit) = forOne(array.asList(), fn)

fun <T> forOne(col: Collection<T>, f: (T) -> Unit) = forExactly(1, col, f)

fun <T> forExactly(k: Int, array: Array<T>, f: (T) -> Unit) = forExactly(k, array.asList(), f)

fun <T> forExactly(k: Int, col: Collection<T>, fn: (T) -> Unit) {
  val results = runTests(col, fn)
  val passed = results.filter { it.second == null }
  if (passed.size != k) {
    val msg = "${passed.size} elements passed but expected $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forSome(array: Array<T>, f: (T) -> Unit) = forSome(array.asList(), f)

fun <T> forSome(col: Collection<T>, fn: (T) -> Unit) {
  val size = col.size
  val results = runTests(col, fn)
  val passed = results.filter { it.second == null }
  if (passed.isEmpty()) {
    buildAssertionError("No elements passed but expected at least one", results)
  } else if (passed.size == size) {
    buildAssertionError("All elements passed but expected < $size", results)
  }
}

fun <T> forAny(array: Array<T>, f: (T) -> Unit) = forAny(array.asList(), f)

fun <T> forAny(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

fun <T> forAtLeastOne(array: Array<T>, f: (T) -> Unit) = forAtLeastOne(array.asList(), f)

fun <T> forAtLeastOne(col: Collection<T>, f: (T) -> Unit) = forAtLeast(1, col, f)

fun <T> forAtLeast(k: Int, array: Array<T>, f: (T) -> Unit) = forAtLeast(k, array.asList(), f)

fun <T> forAtLeast(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filter { it.second == null }
  if (passed.size < k) {
    val msg = "${passed.size} elements passed but expected at least $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forAtMostOne(array: Array<T>, f: (T) -> Unit) = forAtMost(1, array.asList(), f)

fun <T> forAtMostOne(col: Collection<T>, f: (T) -> Unit) = forAtMost(1, col, f)

fun <T> forAtMost(k: Int, col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filter { it.second == null }
  if (passed.size > k) {
    val msg = "${passed.size} elements passed but expected at most $k"
    buildAssertionError(msg, results)
  }
}

fun <T> forNone(array: Array<T>, f: (T) -> Unit) = forNone(array.asList(), f)

fun <T> forNone(col: Collection<T>, f: (T) -> Unit) {
  val results = runTests(col, f)
  val passed = results.filter { it.second == null }
  if (passed.isNotEmpty()) {
    val msg = "${passed.size} elements passed but expected 0"
    buildAssertionError(msg, results)
  }
}

/**
 * Inspecting allows to assert the properties of an object in a typed fashion providing a proper testing context.
 *
 * The **simple example** shows how inspecting can build up a assertion context making the tests more readable.
 * ```
 * inspecting(person){
 *  name shouldBe "John Doe"
 *  age shouldBe 20
 * }
 * ```
 *
 * The **elaborate example** shows that inspecting can be used in a nested fashion in combination with other inspectors
 * to simplify the property accesses.
 * ```
 * inspecting(person){
 *   name shouldBe "John Doe"
 *   age shouldBe 20
 *   forOne(friends){
 *     inspecting(it){
 *       name shouldBe "Samantha Rose"
 *       age shouldBe 19
 *     }
 *   }
 * }
 * ```
 * @param obj the object that is being inspected
 * @param inspector the inspector in which further assertions and inspections can be done
 * @author Hannes Thaller
 */
fun <K> inspecting(obj: K, inspector: K.() -> Unit) {
    obj.inspector()
}

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

private fun <T> buildAssertionError(msg: String, results: List<Pair<T, String?>>): String {

  val passed = results.filter { it.second == null }
  val failed = results.filter { it.second != null }

  val builder = StringBuilder(msg)
  builder.append("\n\nThe following elements passed:\n")
  if (passed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(passed.map { it.first }.joinToString("\n"))
  }
  builder.append("\n\nThe following elements failed:\n")
  if (failed.isEmpty()) {
    builder.append("--none--")
  } else {
    builder.append(failed.map { it.first.toString() + " => " + it.second }.joinToString("\n"))
  }
  throw AssertionError(builder.toString())
}

private fun <T> runTests(col: Collection<T>, f: (T) -> Unit): List<Pair<T, String?>> {
  return col.map { t ->
    try {
      f(t)
      Pair(t, null)
    } catch (e: Throwable) {
      Pair(t, e.message)
    }
  }
}
