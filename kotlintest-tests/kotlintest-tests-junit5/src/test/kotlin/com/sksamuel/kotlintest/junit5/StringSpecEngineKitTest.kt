package com.sksamuel.kotlintest.junit5

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit
import org.junit.platform.testkit.engine.EventType

class StringSpecEngineKitTest : FunSpec({

  test("verify container stats") {
    EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(StringSpecTestCase::class.java))
        .execute()
        .containers()
        .assertStatistics { it.started(2).succeeded(2) }
  }

  test("verify test stats") {
    EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(StringSpecTestCase::class.java))
        .execute()
        .tests()
        .assertStatistics { it.skipped(1).started(3).succeeded(1).aborted(0).failed(2).finished(3) }
  }

  test("exception in initializer") {

    val results = EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(StringSpecExceptionInInit::class.java))
        .execute()

    results.all().list().apply {
      assertSoftly {
        size shouldBe 5

        this[0].type shouldBe EventType.STARTED
        this[1].type shouldBe EventType.DYNAMIC_TEST_REGISTERED
        this[2].type shouldBe EventType.STARTED
        this[3].type shouldBe EventType.FINISHED
        this[4].type shouldBe EventType.FINISHED

        this[0].testDescriptor.displayName shouldBe "KotlinTest"
        this[1].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInInit"
        this[2].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInInit"
        this[3].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInInit"
        this[4].testDescriptor.displayName shouldBe "KotlinTest"
      }
    }

    results.all().failed().list().apply {
      assertSoftly {
        size shouldBe 1
        this[0].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInInit"
      }
    }

    results.all().succeeded().list().apply {
      assertSoftly {
        size shouldBe 1
        this[0].testDescriptor.displayName shouldBe "KotlinTest"
      }
    }
  }

  test("exception in before spec") {

    val results = EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(StringSpecExceptionInBeforeSpec::class.java))
        .execute()

    results.all().list().apply {
      assertSoftly {
        size shouldBe 5

        this[0].type shouldBe EventType.STARTED
        this[1].type shouldBe EventType.DYNAMIC_TEST_REGISTERED
        this[2].type shouldBe EventType.STARTED
        this[3].type shouldBe EventType.FINISHED
        this[4].type shouldBe EventType.FINISHED

        this[0].testDescriptor.displayName shouldBe "KotlinTest"
        this[1].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInBeforeSpec"
        this[2].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInBeforeSpec"
        this[3].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInBeforeSpec"
        this[4].testDescriptor.displayName shouldBe "KotlinTest"
      }
    }

    results.all().failed().list().apply {
      assertSoftly {
        size shouldBe 1
        this[0].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInBeforeSpec"
      }
    }

    results.all().succeeded().list().apply {
      assertSoftly {
        size shouldBe 1
        this[0].type shouldBe EventType.FINISHED
        this[0].testDescriptor.displayName shouldBe "KotlinTest"
      }
    }
  }

  test("exception in after spec") {

    val results = EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(StringSpecExceptionInAfterSpec::class.java))
        .execute()

    results.all().list().apply {
      assertSoftly {
        size shouldBe 11

        this[0].type shouldBe EventType.STARTED
        this[1].type shouldBe EventType.DYNAMIC_TEST_REGISTERED
        this[2].type shouldBe EventType.STARTED
        this[3].type shouldBe EventType.DYNAMIC_TEST_REGISTERED
        this[4].type shouldBe EventType.STARTED
        this[5].type shouldBe EventType.DYNAMIC_TEST_REGISTERED
        this[6].type shouldBe EventType.STARTED
        this[7].type shouldBe EventType.FINISHED
        this[8].type shouldBe EventType.FINISHED
        this[9].type shouldBe EventType.FINISHED
        this[10].type shouldBe EventType.FINISHED

        this[0].testDescriptor.displayName shouldBe "KotlinTest"
        this[1].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInAfterSpec"
        this[2].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInAfterSpec"
        this[3].testDescriptor.displayName shouldBe "a failing test"
        this[4].testDescriptor.displayName shouldBe "a failing test"
        this[5].testDescriptor.displayName shouldBe "a passing test"
        this[6].testDescriptor.displayName shouldBe "a passing test"
        this[7].testDescriptor.displayName shouldBe "a passing test"
        this[8].testDescriptor.displayName shouldBe "a failing test"
        this[9].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInAfterSpec"
        this[10].testDescriptor.displayName shouldBe "KotlinTest"
      }
    }

    results.all().failed().list().apply {
      assertSoftly {
        size shouldBe 2
        this[0].testDescriptor.displayName shouldBe "a failing test"
        this[1].testDescriptor.displayName shouldBe "com.sksamuel.kotlintest.junit5.StringSpecExceptionInAfterSpec"
      }
    }

    results.all().succeeded().list().apply {
      assertSoftly {
        size shouldBe 2
        this[0].testDescriptor.displayName shouldBe "a passing test"
        this[1].testDescriptor.displayName shouldBe "KotlinTest"
      }
    }
  }
})