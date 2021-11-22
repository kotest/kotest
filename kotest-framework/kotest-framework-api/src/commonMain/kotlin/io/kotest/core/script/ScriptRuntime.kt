//package io.kotest.core.script
//
//import io.kotest.common.ExperimentalKotest
//import io.kotest.core.plan.Descriptor
//import io.kotest.core.plan.DisplayName
//import io.kotest.core.plan.Name
//import io.kotest.core.Source
//import io.kotest.core.source.sourceRef
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.core.test.DescriptionName
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.config.TestCaseConfig
//import io.kotest.core.test.testScope
//import io.kotest.core.test.TestType
//import io.kotest.mpp.log
//
//@ExperimentalKotest
//class ScriptSpec : FunSpec()
//
///**
// * This is a global that scripts can use to gain access to configuration at runtime.
// */
//@ExperimentalKotest
//object ScriptRuntime {
//
//   private var spec = ScriptSpec()
//
//   /**
//    * Stores root level tests added by a script.
//    * Should be cleared after each script ScriptInstantiationExceptionhas completed.
//    */
//   private val rootTests = mutableListOf<TestCase>()
//
//   /**
//    * Adds a new root [TestCase] with the given name and type.
//    *
//    * @param xdisabled if true then this test has been disabled by using an xKeyword method.
//    */
//   fun registerRootTest(
//      name: DescriptionName.TestName,
//      xdisabled: Boolean,
//      type: TestType,
//      test: suspend (testScope) -> Unit
//   ) {
//      log { "ScriptRuntime: registerRootTest $name" }
//      val config = if (xdisabled) TestCaseConfig().copy(enabled = false) else TestCaseConfig()
//      val description = spec.description().append(name, type)
//      rootTests.add(
//         TestCase(
//            descriptor = description,
//            spec = spec,
//            test = test,
//            source = sourceRef(),
//            type = type,
//            config = config,
//            factoryId = null,
//            descriptor = Descriptor.fromScriptClass(ScriptSpec::class).append(
//               Name(description.name.name),
//               DisplayName(description.name.displayName),
//               TestType.Test,
//               Source.TestSource(sourceRef().fileName, sourceRef().lineNumber),
//            ),
//            parent = null,
//         )
//      )
//   }
//
//   fun reset() {
//      rootTests.clear()
//      spec = ScriptSpec()
//   }
//
//   fun materializeRootTsssests(parent: Descriptor.SpecDescriptor): List<TestCase> {
//      // the test cases will have been registered with a placeholder spec description, since we don't know
//      // what that is until runtime. So now we must replace that.
//      return rootTests.toList().map {
//         it.copy(descriptor = it.descriptor!!.copy(parent = parent))
//      }
//   }
//}
