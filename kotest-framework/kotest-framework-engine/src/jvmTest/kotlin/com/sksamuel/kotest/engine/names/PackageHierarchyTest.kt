package com.sksamuel.kotest.engine.names

import io.kotest.engine.names.PackageHierarchy
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PackageHierarchyTest : FunSpec() {
   init {
      test("should return all parent packages for multi-level package") {
         PackageHierarchy.parentPackages("org.package.service") shouldBe setOf(
            "org.package.service",
            "org.package",
            "org"
         )
      }

      test("should return package and parent for two-level package") {
         PackageHierarchy.parentPackages("com.example") shouldBe setOf(
            "com.example",
            "com"
         )
      }

      test("should return only the package for single-level package") {
         PackageHierarchy.parentPackages("org") shouldBe setOf("org")
      }

      test("should return empty set for empty package name") {
         PackageHierarchy.parentPackages("") shouldBe emptySet()
      }

      test("should handle deeply nested packages") {
         PackageHierarchy.parentPackages("io.kotest.engine.spec.runner.test") shouldBe setOf(
            "io.kotest.engine.spec.runner.test",
            "io.kotest.engine.spec.runner",
            "io.kotest.engine.spec",
            "io.kotest.engine",
            "io.kotest",
            "io"
         )
      }

      test("should handle packages with numbers and underscores") {
         PackageHierarchy.parentPackages("com.example_1.v2.api") shouldBe setOf(
            "com.example_1.v2.api",
            "com.example_1.v2",
            "com.example_1",
            "com"
         )
      }
   }
}
