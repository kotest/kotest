package io.kotest.assertions

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
@Deprecated(message = "Use with(obj) or assertSofty(obj) instead. Deprecated in 5.9. Will be removed in 6.0")
fun <K> inspecting(obj: K, inspector: K.() -> Unit) {
    obj.inspector()
}
