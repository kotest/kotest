package io.kotest.engine.teamcity

import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.teamcity.TeamCityTestNameEscaper
import io.kotest.engine.test.names.DisplayNameFormatting
import kotlin.reflect.KClass

internal class TeamCityPathRenderer(private val formatting: DisplayNameFormatting) {

   companion object {
      const val DELIMITER = " ⇢ "
   }

   private val fqns = mutableMapOf<KClass<*>, String>()

   /**
    * Builds a path to the Spec including FQN.
    */
   fun testPath(ref: SpecRef): String {
      fqns[ref.kclass] = ref.fqn
      return ref.fqn
   }

   /**
    * Builds a path to the Spec including FQN.
    */
   fun testPath(ref: SpecRef, t: Throwable): String {
      fqns[ref.kclass] = ref.fqn
      return ref.fqn + DELIMITER + t::class.simpleName
   }

   /**
    * Builds a full path including FQN to the test case.
    * Contexts are split by the delimiter.
    */
   fun testPath(testCase: TestCase): String {
      val name = TeamCityTestNameEscaper.escape(formatting.format(testCase))
      val fqn = fqns[testCase.spec::class] ?: error("Cannot render test without first rendering the spec")
      return when (val parent = testCase.parent) {
         null -> "$fqn.$name"
         else -> testPath(parent) + DELIMITER + name
      }
   }

   /**
    * Builds a full path with an exception addition, including FQN to the test case.
    * Contexts are split by the delimiter.
    */
   fun testPath(testCase: TestCase, t: Throwable): String {
      return testPath(testCase) + DELIMITER + t::class.simpleName
   }
}
