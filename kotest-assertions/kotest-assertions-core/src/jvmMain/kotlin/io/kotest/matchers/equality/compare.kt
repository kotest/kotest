package io.kotest.matchers.equality

import io.kotest.assertions.eq.eq
import io.kotest.assertions.failure
import io.kotest.mpp.bestName
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

fun <T> compareFields(
   actual: T,
   expected: T,
   config: FieldEqualityConfig
): CompareResult {
   val predicates = config.predicates()
   fun <U> compare(actual: U, expected: U, prefix: String?): CompareResult {

      val props1 = actual.fields(predicates)
      val props2 = expected.fields(predicates)

      // types don't have to match but they should at least have the same fields
      if (props1 != props2)
         throw failure("Comparing type ${actual!!::class.jvmName} to ${expected!!::class.jvmName} with mismatched properties")

      return props1.fold(CompareResult(emptyList(), emptyList())) { acc, prop ->
         println("Prop: " + prop.returnType.toString().replace("?", ""))

         val actualValue = prop.getter.call(actual)
         val expectedValue = prop.getter.call(expected)
         val name = (prefix ?: "") + prop.name
         val returnType = prop.returnType.classifier as KClass<*>

         when {

            returnType.isSubclassOf(Collection::class) -> {
               val actualList = actualValue as Collection<*>
               val expectedList = expectedValue as Collection<*>
               if (actualList.size != expectedList.size)
                  throw failure("Collections differ in size: ${actualList.size} != ${expectedList.size}")

               if (actualList.isEmpty()) acc else {
                  val result = actualList.zip(expectedList).withIndex().map { (index, value) ->
                     compare(value.first, value.second, "$prefix[$index]")
                  }.reduce { a, op -> a.reduce(op) }
                  acc.reduce(result)
               }
            }

            returnType.isSubclassOf(Map::class) -> {
               val actualMap = actualValue as Map<*, *>
               val expectedMap = expectedValue as Map<*, *>
               if (actualMap.size != expectedMap.size)
                  throw failure("Maps differ in size ${actualMap.size} != ${expectedMap.size}")

               if (actualMap.isEmpty()) acc else {
                  val result = actualMap.keys.map { key ->
                     val a = actualMap[key]
                     val b = expectedMap[key]
                     compare(a, b, "$prefix[$key]")
                  }.reduce { a, op -> a.reduce(op) }
                  acc.reduce(result)
               }
            }

            useEq(
               actualValue,
               expectedValue,
               returnType,
               config.useDefaultShouldBeForFields
            ) -> {
               println("Using Eq for ${prop.returnType.classifier}")
               val throwable = eq(actualValue, expectedValue)
               if (throwable == null) acc.withMatch(name) else acc.withError(name, throwable)
            }


            else -> acc.reduce(compare(actualValue, expectedValue, prop.name + "."))
         }
      }
   }
   return compare(actual, expected, null)
}

data class CompareResult(
   val fieldsIncluded: List<String>,
   val errors: List<Pair<String, Throwable>>,
) {
   fun withMatch(prop: String) = CompareResult(fieldsIncluded + prop, errors)
   fun withError(prop: String, error: Throwable) = CompareResult(fieldsIncluded + prop, errors + Pair(prop, error))
   fun reduce(other: CompareResult) =
      CompareResult(this.fieldsIncluded + other.fieldsIncluded, this.errors + other.errors)
}

private val builtins = setOf("boolean", "byte", "double", "float", "int", "long", "short")

/**
 * Returns true if we should use an instance of [Eq] for comparison, rather than field by field recursion.
 */
internal fun useEq(
   actual: Any?,
   expected: Any?,
   typeName: KClass<*>,
   useEqs: Collection<KClass<*>>,
): Boolean {
   val expectedOrActualIsNull = actual == null || expected == null
   val typeIsJavaOrKotlinBuiltIn by lazy {
      val bestName = typeName.bestName()
      bestName.startsWith("kotlin") ||
         bestName.startsWith("java") ||
         builtins.contains(bestName)
   }
   val expectedOrActualIsEnum = actual is Enum<*>
      || expected is Enum<*>
      || (actual != null && actual::class.java.isEnum)
      || (expected != null && expected::class.java.isEnum)
   return expectedOrActualIsNull
      || typeIsJavaOrKotlinBuiltIn
      || useEqs.contains(typeName)
      || expectedOrActualIsEnum
}
