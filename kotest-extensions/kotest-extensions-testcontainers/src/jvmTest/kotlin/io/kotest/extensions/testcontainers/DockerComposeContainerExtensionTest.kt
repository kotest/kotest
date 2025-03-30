package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.Ignored
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.optional.shouldBePresent
import io.kotest.matchers.string.shouldContain
import org.testcontainers.containers.DockerComposeContainer
import java.io.File

@Ignored
class DockerComposeContainerExtensionTest : StringSpec() {
   init {

      val container: DockerComposeContainer<Nothing> =
         install(DockerComposeContainerExtension(File("src/test/resources/docker-compose/docker-compose.yml")))

      "should setup using docker-compose" {
         container.getContainerByServiceName("hello_world").shouldBePresent().logs shouldContain "Hello world"
      }
   }
}
