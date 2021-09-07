package com.sksamuel.kotest.autoscan

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.engine.config.createConfigSummary
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@Isolate
class DumpTest : FunSpec({
   // todo restore test
   test("!dump should pick up auto scanned listeners") {
      configuration.createConfigSummary() shouldBe """-> Parallelism: 1 thread(s)
-> Default test timeout: 600000ms
-> Default test order: Sequential
-> Default isolation mode: SingleInstance
-> Global soft assertations: False
-> Write spec failure file: False
-> Fail on ignored tests: False
-> Spec execution order: LexicographicSpecExecutionOrder
-> Extensions
  - com.sksamuel.kotest.autoscan.AutoScanConstructorExtension
  - com.sksamuel.kotest.autoscan.MyClassProjectListener
  - com.sksamuel.kotest.autoscan.MyObjectProjectListener
  - com.sksamuel.kotest.autoscan.MyTestListener
  - io.kotest.engine.extensions.SystemPropertyTagExtension
  - io.kotest.engine.extensions.RuntimeTagExtension
  - io.kotest.engine.extensions.RuntimeTagExpressionExtension
  - io.kotest.engine.extensions.IgnoredSpecDiscoveryExtension
  - io.kotest.engine.extensions.TagsExcludedDiscoveryExtension
  - io.kotest.engine.extensions.SpecifiedTagsTagExtension
"""
   }
})
