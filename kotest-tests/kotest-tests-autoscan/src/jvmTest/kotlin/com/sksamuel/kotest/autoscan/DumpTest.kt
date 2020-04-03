package com.sksamuel.kotest.autoscan

import io.kotest.core.config.Project
import io.kotest.core.config.createConfigSummary
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DumpTest : FunSpec({
   test("dump should pick up auto scanned listeners") {
      Project.createConfigSummary() shouldBe """-> Parallelism: 1 thread(s)
-> Default test timeout: 600000ms
-> Default test order: TestCaseOrder
-> Default isolation mode: IsolationMode
-> Global soft assertations: False
-> Write spec failure file: False
-> Fail on ignored tests: False
-> Spec execution order: LexicographicSpecExecutionOrder
-> Extensions
  - com.sksamuel.kotest.autoscan.AutoScanConstructorExtension
  - io.kotest.core.extensions.SystemPropertyTagExtension
  - io.kotest.core.extensions.RuntimeTagExtension
  - io.kotest.core.extensions.IgnoredSpecDiscoveryExtension
  - io.kotest.core.extensions.TagFilteredDiscoveryExtension
-> Listeners
  - com.sksamuel.kotest.autoscan.MyClassProjectListener
  - com.sksamuel.kotest.autoscan.MyObjectProjectListener
  - com.sksamuel.kotest.autoscan.MyTestListener
"""
   }
})
