package com.sksamuel.kotest.junit5

import io.kotest.Project
import io.kotest.specs.FunSpec
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.testkit.engine.EngineTestKit

class FunSpecEngineKitTest : FunSpec({

  test("verify container stats") {
    EngineTestKit
        .engine("kotest")
        .selectors(selectClass(FunSpecTestCase::class.java))
        .execute()
        .containers()
        .assertStatistics { it.started(2).succeeded(2).failed(0) }
  }

  test("verify test stats") {
    EngineTestKit
        .engine("kotest")
        .selectors(selectClass(FunSpecTestCase::class.java))
        .execute()
        .tests()
        .assertStatistics { it.skipped(1).started(3).succeeded(1).aborted(0).failed(2).finished(3) }
  }

  test("mark spec as failed if failOnIgnoredTests is set") {
    Project.failOnIgnoredTests = true
    EngineTestKit
        .engine("kotest")
        .selectors(selectClass(FailOnIgnoreTestCase::class.java))
        .execute()
        .containers()
        .assertStatistics { it.started(2).failed(1) }
    Project.failOnIgnoredTests = false
  }
})

class FailOnIgnoreTestCase : FunSpec() {
  init {
    test("ignored").config(enabled = false) {

    }
  }
}