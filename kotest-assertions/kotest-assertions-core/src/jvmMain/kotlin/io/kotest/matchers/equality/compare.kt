package io.kotest.matchers.equality

import io.kotest.assertions.eq.eq
import io.kotest.assertions.failure
import io.kotest.assertions.print.print
import io.kotest.mpp.bestName
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

fun <T> compareUsingFields(
   actual: T,
   expected: T,
   config: FieldEqualityConfig,
): CompareResult {
   return when {
      actual == null -> throw failure("Expected ${expected.print().value} but actual was null")
      expected == null -> throw failure("Expected null but actual was ${actual.print().value}")
      else -> compareFields(actual, expected, null, config)
   }
}

private fun compareFields(actual: Any?, expected: Any?, field: String?, config: FieldEqualityConfig): CompareResult {

   val props1 = actual.fields(config.predicates())
   val props2 = expected.fields(config.predicates())

   // types don't have to match but they should at least have the same fields
   if (props1 != props2)
      throw failure("Comparing type ${actual!!::class.jvmName} to ${expected!!::class.jvmName} with mismatched properties")

   return props1.fold(CompareResult(emptyList(), emptyMap())) { acc, prop ->
      println("Prop: " + prop.returnType.toString().replace("?", ""))
      val actualValue = prop.getter.call(actual)
      val expectedValue = prop.getter.call(expected)
      val name = if (field == null) prop.name else field + "." + prop.name
      val returnType = prop.returnType.classifier as KClass<*>
      acc.reduce(compareValue(actualValue, expectedValue, returnType, name, config))
   }
}

private fun compareValue(
   actual: Any?,
   expected: Any?,
   type: KClass<*>,
   field: String,
   config: FieldEqualityConfig
): CompareResult {
   println("Compare value $type from $actual $expected")

   return when {
      type.isSubclassOf(Collection::class) -> {
         val actualCollection = actual as Collection<*>
         val expectedCollection = expected as Collection<*>
         compareCollections(actualCollection, expectedCollection, field, config)
      }

      type.isSubclassOf(Map::class) -> {
         val actualMap = actual as Map<*, *>
         val expectedMap = expected as Map<*, *>
         compareMaps(actualMap, expectedMap, field, config)
      }

      useEq(
         actual,
         expected,
         type,
         config.useDefaultShouldBeForFields
      ) -> {
         val throwable = eq(actual, expected)
         if (throwable == null) CompareResult.match(field) else CompareResult.single(field, throwable)
      }

      else -> compareFields(actual, expected, field, config)
   }
}

private fun compareCollections(
   actual: Collection<*>,
   expected: Collection<*>,
   field: String,
   config: FieldEqualityConfig
): CompareResult {

   return if (actual.size != expected.size)
      CompareResult.single(field, failure("Collections differ in size: ${actual.size} != ${expected.size}"))
   else if (actual.isEmpty())
      CompareResult.empty
   else {
      actual.zip(expected).withIndex().map { (index, value) ->
         // other element should be an instance of this element or vice version
         val elementName = "$field[$index]"
         when {
            value.first == null && value.second == null -> CompareResult.empty
            value.first == null -> CompareResult.single(
               elementName,
               failure("Expected ${value.second.print().value} but actual was null")
            )

            value.second == null -> CompareResult.single(
               elementName,
               failure("Expected null but actual was ${value.first.print().value}")
            )

            else -> compareValue(value.first, value.second, value.first!!::class, elementName, config)
         }
      }.reduce { a, op -> a.reduce(op) }
   }
}

private fun compareMaps(
   actual: Map<*, *>,
   expected: Map<*, *>,
   field: String,
   config: FieldEqualityConfig
): CompareResult {

   return if (actual.size != expected.size)
      CompareResult.single(field, failure("Maps differ in size: ${actual.size} != ${expected.size}"))
   else if (actual.isEmpty())
      CompareResult.empty
   else {
      actual.keys.map { key ->
         val a = actual[key]
         val b = expected[key]
         compareValue(a, b, a!!::class, "$field[$key]", config)
      }.reduce { a, op -> a.reduce(op) }
   }
}


data class CompareResult(
   val fields: List<String>,
   val errors: Map<String, Throwable>,
) {

   companion object {
      val empty: CompareResult = CompareResult(emptyList(), emptyMap())
      fun single(field: String, error: Throwable): CompareResult = CompareResult(listOf(field), mapOf(field to error))
      fun match(field: String): CompareResult = CompareResult(listOf(field), emptyMap())
   }

   fun withMatch(field: String) = CompareResult(fields + field, errors)
   fun withError(field: String, error: Throwable) = CompareResult(fields + field, errors + Pair(field, error))
   fun reduce(other: CompareResult) =
      CompareResult(this.fields + other.fields, this.errors + other.errors)
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
