package io.kotest.core.script

import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.mpp.log

class ScriptSpec : FunSpec()

/**
 * This is a global that scripts can use to gain access to configuration at runtime.
 */
object ScriptRuntime {

   private var spec = ScriptSpec()

   /**
    * Stores root level tests added by a script.
    * Should be cleared after each script ScriptInstantiationExceptionhas completed.
    */
   private val rootTests = mutableListOf<TestCase>()

   /**
    * Adds a new root [TestCase] with the given name and type.
    *
    * @param xdisabled if true then this test has been disabled by using an xKeyword method.
    */
   fun registerRootTest(
      name: DescriptionName.TestName,
      xdisabled: Boolean,
      type: TestType,
      test: suspend (TestContext) -> Unit
   ) {
      log("ScriptRuntime: registerRootTest $name")
      val config = if (xdisabled) TestCaseConfig().copy(enabled = false) else TestCaseConfig()
      rootTests.add(
         TestCase(
            spec.description().append(name, type),
            spec,
            test,
            sourceRef(),
            type,
            config,
            null,
            null
         )
      )
   }

   fun reset() {
      rootTests.clear()
      spec = ScriptSpec()
   }

   fun materializeRootTests(): List<TestCase> {
      return rootTests.toList()
   }
}
