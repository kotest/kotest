package io.kotest.runner.console

import io.kotest.Description
import io.kotest.Spec
import io.kotest.core.TestCaseFilter
import io.kotest.core.TestFilterResult
import io.kotest.core.fromSpecClass
import kotlin.reflect.KClass
import kotlin.reflect.full.allSuperclasses

/**
 * A [TestCaseFilter] that parses the a test path by detecting the type of spec in use.
 */
class SpecAwareTestFilter(testPath: String, spec: KClass<out Spec>) : TestCaseFilter {

  private fun KClass<out Spec>.isSpec(classname: String): Boolean =
      this.allSuperclasses.map { it.qualifiedName }.contains(classname)

  private val parser = spec.run {
    when {
      this.isSpec("io.kotest.specs.BehaviorSpec") -> BehaviorSpecStyleParser
      this.isSpec("io.kotest.specs.DescribeSpec") -> DescribeSpecStyleParser
      this.isSpec("io.kotest.specs.ExpectSpec") -> DelimitedTestPathParser
      this.isSpec("io.kotest.specs.FeatureSpec") -> FeatureSpecStyleParser
      this.isSpec("io.kotest.specs.FreeSpec") -> DelimitedTestPathParser
      this.isSpec("io.kotest.specs.FunSpec") -> DelimitedTestPathParser
      this.isSpec("io.kotest.specs.ShouldSpec") -> ShouldSpecStyleParser
      this.isSpec("io.kotest.specs.StringSpec") -> StringSpecStyleParser
      this.isSpec("io.kotest.specs.WordSpec") -> WordSpecStyleParser
      else -> throw RuntimeException("Could not detect Spec Style for class [$this] with super [${spec.allSuperclasses}]")
    }
  }

  val test = parser.parse(Description.fromSpecClass(spec), testPath)

  override fun filter(description: Description): TestFilterResult =
      if (description == test || test.isAncestorOf(description) || description.isAncestorOf(test)) TestFilterResult.Include else TestFilterResult.Exclude
}
