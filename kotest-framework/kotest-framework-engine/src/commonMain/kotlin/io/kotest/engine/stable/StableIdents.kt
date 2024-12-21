package io.kotest.engine.stable

import io.kotest.core.Platform
import io.kotest.core.platform
import io.kotest.core.names.WithDataTestName
import io.kotest.mpp.bestName
import io.kotest.mpp.hasAnnotation
import io.kotest.mpp.reflection
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Used to generate stable identifiers for data tests and to ensure test names are unique.
 *
 * When using an element with data-testing, the generated name must be consistent across different
 * instances of the same element (stable). Otherwise, when using an isolation mode other than the default,
 * the same element will appear under different test names.
 *
 * For example, given a class like this:
 *
 * class Foo(val value: String) {
 *   override fun toString() = Random.nextInt().toString()
 * }
 *
 * If we used the toString() as the test name, between different instances of the same test, the names
 * would not match up, and Kotest would not be able to know if a given element has been executed or not.
 *
 * class MyTest: FunSpec() {
 *    init {
 *       isolationMode = IsolationMode.InstancePerLeaf
 *
 *       context("my data test") {
 *           withData(
 *              Foo("a"),
 *              Foo("b")
 *           ) { a -> a shouldBe a }
 *       }
 *    }
 * }
 *
 * Therefore, to avoid this, data-testing requires data test elements to be stable.
 */
internal object StableIdents {

   /**
    * Returns a stable identifier for the given object.
    *
    * If the object is null, the string "<null>" is returned.
    * If the object is annotated with [IsStableType] then the toString method is used.
    * If the object is an instance of [WithDataTestName] then the [WithDataTestName.dataTestName] method is used.
    * If the object is considered stable via [isStable] then the toString method is used.
    *
    * Otherwise, the class name is returned.
    */
   fun getStableIdentifier(t: Any?): String {
      return when {
         t == null -> "<null>"
         t::class.hasAnnotation<IsStableType>() || platform != Platform.JVM -> t.toString()
         t is WithDataTestName -> t.dataTestName()
         else -> {
            val psv = platformStableValue(t)
            return when {
               psv != null -> psv
               isStable(t::class, t) -> t.toString()
               else -> t::class.bestName()
            }
         }
      }
   }

   /**
    * Returns true if the given type is a class and stable.
    */
   private fun isStable(type: KType) = when (val classifier = type.classifier) {
      is KClass<*> -> isStable(classifier, null)
      else -> false
   }

   /**
    * Returns true if the given class is a "stable" class, ie one that has a consistent toString().
    *
    *      An instance is considered stable if it is a data class where each parameter is either a data class itself,
    *      one of the [allPlatformStableTypes], or if the type of instance is annotated with [IsStableType].
    *
    *      Note: If the user has overridden `toString()` and the returned value is not stable,
    *      then the behavior of this function is undefined.
    *
    * A stable class is either:
    *  - a Kotlin or Java primitive type or a String
    *  - an enum class
    *  - a data class that only contains stable classes as members
    *  - a stable type on the executing platform (see [isPlatformStable]).
    *  - a collection type where the elements are stable
    */
   internal fun isStable(kclass: KClass<*>, t: Any? = null): Boolean {
      return when {
         isAllPlatformStable(kclass) -> true
         reflection.isEnumClass(kclass) -> true
         reflection.isDataClass(kclass) && hasStableMembers(kclass, t) -> true
         isPlatformStable(kclass) -> true
         else -> {
            println("Warning, type $kclass used in data testing does not have a stable toString()")
            false
         }
      }
   }

   /**
    * Returns true if all members of the given [kclass] are "stable".
    */
   private fun hasStableMembers(kclass: KClass<*>, t: Any? = null) =
      reflection.primaryConstructorMembers(kclass).let { members ->
         members.isNotEmpty() && members.all { getter ->
            val typeIsStable = isStable(getter.type)
            if (t == null) {
               typeIsStable
            } else {
               val valueIsStable = getter.call(t)?.let { memberValue ->
                  isStable(memberValue::class, memberValue)
               } ?: false

               typeIsStable || valueIsStable
            }
         }
      }
}
