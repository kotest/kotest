package io.kotest.engine.launcher

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain

// a concrete, public subclass of Spec - should be discovered by the scanner
class PublicConcreteSpecForScannerTest : FunSpec()

// an abstract subclass of Spec - should not be discovered as it cannot be instantiated
abstract class AbstractSpecForScannerTest : FunSpec()

// a private, concrete subclass of Spec - should not be discovered as the engine cannot instantiate it
private class PrivateConcreteSpecForScannerTest : FunSpec()

@EnabledIf(LinuxOnlyGithubCondition::class)
class SpecScannerTest : FunSpec() {

   // scan the classpath once and reuse the result across all tests
   private val specs = SpecScanner.scan()

   init {

      test("scan should discover concrete, public subclasses of Spec") {
         specs shouldContain PublicConcreteSpecForScannerTest::class
      }

      test("scan should not return abstract subclasses of Spec") {
         specs shouldNotContain AbstractSpecForScannerTest::class
         // the built in spec styles are themselves abstract subclasses of Spec
         specs shouldNotContain FunSpec::class
         specs shouldNotContain Spec::class
      }

      test("scan should not return private subclasses of Spec") {
         specs shouldNotContain PrivateConcreteSpecForScannerTest::class
      }
   }
}
