package io.kotest.runner.console

import io.kotest.core.filters.TestCaseFilter
import io.kotest.core.filters.TestFilterResult
import io.kotest.core.spec.Spec
import io.kotest.core.spec.description
import io.kotest.core.test.Description
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

/**
 * A [TestCaseFilter] that parses the a test path by detecting the type of spec in use.
 */
class SpecAwareTestFilter(testPath: String, spec: KClass<out Spec>) : TestCaseFilter {

   private fun KClass<*>.isSpec(classname: String): Boolean =
      this.allSuperclasses.map { it.qualifiedName }.contains(classname)

   private val parser = spec.run {
      when {
         this.isSpec("io.kotest.core.spec.style.BehaviorSpec") -> BehaviorSpecStyleParser
         this.isSpec("io.kotest.core.spec.style.DescribeSpec") -> DescribeSpecStyleParser
         this.isSpec("io.kotest.core.spec.style.ExpectSpec") -> DelimitedTestPathParser
         this.isSpec("io.kotest.core.spec.style.FeatureSpec") -> FeatureSpecStyleParser
         this.isSpec("io.kotest.core.spec.style.FreeSpec") -> DelimitedTestPathParser
         this.isSpec("io.kotest.core.spec.style.FunSpec") -> DelimitedTestPathParser
         this.isSpec("io.kotest.core.spec.style.ShouldSpec") -> ShouldSpecStyleParser
         this.isSpec("io.kotest.core.spec.style.StringSpec") -> StringSpecStyleParser
         this.isSpec("io.kotest.core.spec.style.WordSpec") -> WordSpecStyleParser
         else -> throw RuntimeException("Could not detect Spec Style for class [$this] with super [${spec.allSuperclasses}]")
      }
   }

   val test = parser.parse(spec.description(), testPath)

   override fun filter(description: Description): TestFilterResult =
      if (description == test || test.isAncestorOf(description) || description.isAncestorOf(test)) TestFilterResult.Include else TestFilterResult.Exclude
}
