package io.kotlintest.runner.console

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.core.TestCaseFilter
import io.kotlintest.core.TestFilterResult
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
      this.isSpec("io.kotlintest.specs.BehaviorSpec") -> BehaviorSpecStyleParser
      this.isSpec("io.kotlintest.specs.DescribeSpec") -> DescribeSpecStyleParser
      this.isSpec("io.kotlintest.specs.ExpectSpec") -> DelimitedTestPathParser
      this.isSpec("io.kotlintest.specs.FeatureSpec") -> FeatureSpecStyleParser
      this.isSpec("io.kotlintest.specs.FreeSpec") -> DelimitedTestPathParser
      this.isSpec("io.kotlintest.specs.FunSpec") -> DelimitedTestPathParser
      this.isSpec("io.kotlintest.specs.ShouldSpec") -> ShouldSpecStyleParser
      this.isSpec("io.kotlintest.specs.StringSpec") -> StringSpecStyleParser
      this.isSpec("io.kotlintest.specs.WordSpec") -> WordSpecStyleParser
      else -> throw RuntimeException("Could not detect Spec Style for class [$this] with super [${spec.allSuperclasses}]")
    }
  }

  val test = parser.parse(Description.spec(spec), testPath)

  override fun filter(description: Description): TestFilterResult =
      if (description == test || test.isAncestorOf(description) || description.isAncestorOf(test)) TestFilterResult.Include else TestFilterResult.Exclude
}
