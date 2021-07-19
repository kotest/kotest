package io.kotest.core.script

import io.kotest.common.ExperimentalKotest
import io.kotest.core.plan.Descriptor
import io.kotest.core.plan.DisplayName
import io.kotest.core.plan.TestName
import io.kotest.core.source
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.mpp.log

@ExperimentalKotest
class ScriptSpec : FunSpec()

/**
 * This is a global that scripts can use to gain access to configuration at runtime.
 */
@ExperimentalKotest
object ScriptRuntime {

   private var spec = ScriptSpec()

   /**
    * Stores root level tests added by a script.
    * Should be cleared after each script ScriptInstantiationExceptionhas completed.
    */
   private val rootTests = mutableListOf<TestCase>()

   /**
    * Adds a new top level [TestCase] with the given name and type.
    *
    * @param xdisabled if true then this test has been disabled by using an xKeyword method.
    */
   fun registerRootTest(
      name: TestName,
      xdisabled: Boolean,
      type: TestType,
      test: suspend (TestContext) -> Unit
   ) {
      log { "ScriptRuntime: registerRootTest $name" }
      val config = if (xdisabled) TestCaseConfig().copy(enabled = false) else TestCaseConfig()
      rootTests.add(
         TestCase(
            Descriptor.SpecDescriptor(spec).append(name, DisplayName(name.testName), type),
            spec = spec,
            parent = null,
            test = test,
            type = type,
            source = source(),
            config = config,
            factoryId = null,
         )
      )
   }

   fun reset() {
      rootTests.clear()
      spec = ScriptSpec()
   }

   fun materializeRootTests(): List<TestCase> {
      // the test cases will have been registered with a placeholder spec description, since we don't know
      // what that is until runtime. So now we must replace that.
      return rootTests.toList().map {
//         it.copy(descriptor = it.descriptor.copy(parent = parent))
         it
      }
   }
}
