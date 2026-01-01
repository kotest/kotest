package io.kotest.extensions.testcontainers

import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.string.shouldContain

class ComposeContainerSpecExtensionTest : StringSpec() {
   init {

      val container = install(ComposeContainerSpecExtension.fromResource("docker-compose/docker-compose.yml"))

      "should setup using ComposeContainer" {
         container.getContainerByServiceName("hello_world").shouldBePresent().logs shouldContain "Hello world"
      }
   }
}
