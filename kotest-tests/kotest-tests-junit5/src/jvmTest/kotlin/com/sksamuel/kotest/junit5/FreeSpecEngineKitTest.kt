package com.sksamuel.kotest.junit5

import io.kotest.specs.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class FreeSpecEngineKitTest : FunSpec({

  test("verify container stats") {
    EngineTestKit
        .engine("kotest")
        .selectors(selectClass(FreeSpecTestCase::class.java))
        .execute()
        .containers()
        .assertStatistics { it.started(9).succeeded(6) }
  }

  test("verify test stats") {
    EngineTestKit
        .engine("kotest")
        .selectors(selectClass(FreeSpecTestCase::class.java))
        .execute()
        .tests()
        .assertStatistics { it.skipped(3).started(11).succeeded(5).aborted(0).failed(6).finished(11) }
  }

})