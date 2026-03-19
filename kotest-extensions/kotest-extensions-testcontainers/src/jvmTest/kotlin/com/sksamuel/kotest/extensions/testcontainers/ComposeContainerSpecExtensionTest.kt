package com.sksamuel.kotest.extensions.testcontainers

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.testcontainers.ComposeContainerSpecExtension
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.string.shouldContain
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class ComposeContainerSpecExtensionTest : StringSpec() {
   init {

      val container = install(ComposeContainerSpecExtension.fromResource("docker-compose/docker-compose.yml"))

      "should setup using ComposeContainer" {
         eventually(5.seconds) {
            container.getContainerByServiceName("helloworld").shouldBePresent().logs shouldContain "Hello World"
         }
      }
   }
}
