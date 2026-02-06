package com.sksamuel.kotest.engine.names

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.PackageUtils
import io.kotest.matchers.shouldBe

class PackageHierarchyTest : FunSpec() {
   init {
      test("should return all parent packages for multi-level package") {
         PackageUtils.parentPackages("org.package.service") shouldBe setOf(
            "org.package.service",
            "org.package",
            "org"
         )
      }

      test("should return package and parent for two-level package") {
         PackageUtils.parentPackages("com.example") shouldBe setOf(
            "com.example",
            "com"
         )
      }

      test("should return only the package for single-level package") {
         PackageUtils.parentPackages("org") shouldBe setOf("org")
      }

      test("should return empty set for empty package name") {
         PackageUtils.parentPackages("") shouldBe emptySet()
      }

      test("should handle deeply nested packages") {
         PackageUtils.parentPackages("io.kotest.engine.spec.runner.test") shouldBe setOf(
            "io.kotest.engine.spec.runner.test",
            "io.kotest.engine.spec.runner",
            "io.kotest.engine.spec",
            "io.kotest.engine",
            "io.kotest",
            "io"
         )
      }

      test("should handle packages with numbers and underscores") {
         PackageUtils.parentPackages("com.example_1.v2.api") shouldBe setOf(
            "com.example_1.v2.api",
            "com.example_1.v2",
            "com.example_1",
            "com"
         )
      }
   }
}
