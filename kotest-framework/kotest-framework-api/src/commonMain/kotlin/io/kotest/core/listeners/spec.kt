package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

interface BeforeSpecListener : Listener {

   /**
    * This callback is invoked after the Engine instantiates a [Spec]
    * to be used as part of a [TestCase] execution.
    *
    * If a spec is instantiated multiple times - for example, if
    * [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, then this callback will be invoked for each instance
    * created, just before the first test (or only test) is executed for that spec.
    *
    * This callback should be used if you need to perform setup
    * each time a new spec instance is created. If you simply need to
    * perform setup once per class file, then use [prepareSpec].
    *
    * @param spec the [Spec] instance.
    */
   suspend fun beforeSpec(spec: Spec): Unit = Unit
}

interface AfterSpecListener : Listener {

   /**
    * Is invoked after the [TestCase]s that are part of a particular
    * [Spec] instance have completed.
    *
    * If a spec is instantiated multiple times - for example, if
    * [InstancePerTest] or [InstancePerLeaf] isolation modes are used,
    * then this callback will be invoked for each instantiated spec,
    * after the tests that are applicable to that spec instance have
    * returned.
    *
    * This callback should be used if you need to perform cleanup
    * after each individual spec instance. If you simply need to
    * perform cleanup once per class file, then use [finalizeSpec].
    *
    * @param spec the [Spec] instance.
    */
   suspend fun afterSpec(spec: Spec): Unit = Unit
}

interface SkipSpecListner : Listener {

   /**
    * Called once per [Spec] iff all tests in the spec are inactive.
    *
    * @param spec the [Spec] instance.
    * @param results a map of each test case mapped to its skipped result.
    */
   suspend fun skipSpec(spec: Spec, results: Map<TestCase, TestResult>): Unit = Unit
}

interface PrepareSpecListener : Listener {

   /**
    * Called once per [Spec], when the engine is preparing to
    * execute the tests for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once.
    *
    * @param kclass the [Spec] class
    */
   suspend fun prepareSpec(kclass: KClass<out Spec>): Unit = Unit
}

interface FinalizeSpecListener : Listener {

   /**
    * Called once per [Spec], after all tests have completed for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once.
    *
    * The results parameter contains every [TestCase], along with
    * the result of that test, including tests that were ignored (which
    * will have a TestResult that has TestStatus.Ignored).
    *
    * @param kclass the [Spec] class
    * @param results a map of each test case mapped to its result.
    */
   suspend fun finalizeSpec(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>,
   ): Unit = Unit
}

interface SpecInstantiationListener : Listener {
   fun specInstantiated(spec: Spec) {}
   fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {}
}
