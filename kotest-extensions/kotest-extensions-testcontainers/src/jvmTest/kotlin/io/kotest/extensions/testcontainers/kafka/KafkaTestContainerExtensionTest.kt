package io.kotest.extensions.testcontainers.kafka

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.utils.Bytes
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@EnabledIf(LinuxOnlyGithubCondition::class)
class KafkaTestContainerExtensionTest : FunSpec({

   val container = install(KafkaContainerExtension(DockerImageName.parse("confluentinc/cp-kafka:5.4.3")))

   test("happy path") {

      val producer = container.producer()
      producer.send(ProducerRecord("mytopic", Bytes("hello world".encodeToByteArray())))
      producer.close()

      val consumer = container.consumer()
      consumer.subscribe(listOf("mytopic"))
      consumer.poll(Duration.ofSeconds(10)).toList().single().value().get().decodeToString() shouldBe "hello world"
   }

})
