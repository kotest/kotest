package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.options.ContainerExtensionConfig
import io.kotest.matchers.string.shouldContain
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.OutputFrame
import java.util.function.Consumer

private val container = GenericContainer("redis:5.0.3-alpine").apply {
   startupAttempts = 2
   withExposedPorts(6379)
}

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestContainerLogTest : FunSpec() {
   init {

      var logs = ""
      val consumer = Consumer<OutputFrame> { t -> logs += t.utf8String }
      install(
         TestContainerSpecExtension(
            container,
            ContainerExtensionConfig(logConsumer = consumer)
         )
      )

      test("should capture ").config(retries = 5) {
         logs.shouldContain("Running mode=standalone")
      }
   }
}
