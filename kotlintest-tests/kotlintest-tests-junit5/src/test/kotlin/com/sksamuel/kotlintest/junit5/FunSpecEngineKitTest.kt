package com.sksamuel.kotlintest.junit5

import io.kotlintest.specs.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class FunSpecEngineKitTest : FunSpec({

  test("verify container stats") {
    EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(FunSpecTestCase::class.java))
        .execute()
        .containers()
        .assertStatistics { it.started(2).succeeded(2) }
  }

  test("verify test stats") {
    EngineTestKit
        .engine("kotlintest")
        .selectors(selectClass(FunSpecTestCase::class.java))
        .execute()
        .tests()
        .assertStatistics { it.skipped(1).started(3).succeeded(1).aborted(0).failed(2).finished(3) }
  }

})