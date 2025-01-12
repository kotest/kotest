package com.sksamuel.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.PackageConfigLoader
import io.kotest.engine.config.PackageConfigLoader.CachedConfig
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class PackageConfigLoaderTest : FunSpec() {
   init {
      test("PackageConfigLoader should cache package config") {
         PackageConfigLoader.configs(PackageConfig::class.java.`package`.name).size shouldBe 1
         val config = PackageConfigLoader.cache[PackageConfig::class.java.`package`.name] as CachedConfig.Config
         config.config.shouldNotBeNull()
      }
      test("PackageConfigLoader should use cached package config") {
         // if this wasn't cached, then the second invocation would error on instantiation
         PackageConfigLoader.configs(PackageConfig::class.java.`package`.name).size shouldBe 1
         PackageConfigLoader.configs(PackageConfig::class.java.`package`.name).size shouldBe 1
      }
      test("should return empty list if no configs") {
         PackageConfigLoader.configs("com.wibble.bar").size shouldBe 0
      }
      test("should cache missing configs") {
         PackageConfigLoader.configs("com.wibble.bar").size shouldBe 0
         listOf("com", "com.wibble", "com.wibble.bar").forEach {
            PackageConfigLoader.cache[it] shouldBe CachedConfig.Null
         }
      }
      test("package chain should have most specific first") {
         PackageConfigLoader.packages("com.sksamuel.kotest.runner.junit5") shouldBe
            listOf(
               "com.sksamuel.kotest.runner.junit5",
               "com.sksamuel.kotest.runner",
               "com.sksamuel.kotest",
               "com.sksamuel",
               "com"
            )
      }
   }
}

class PackageConfig : AbstractPackageConfig() {
   var invoked = false

   init {
      if (invoked) error("Should only be invoked once")
   }
}
