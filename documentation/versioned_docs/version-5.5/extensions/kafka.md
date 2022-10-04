---
id: kafka
title: Embedded Kafka Extension
sidebar_label: Kafka
slug: embedded-kafka.html
---

Kotest offers an extension that spins up an embedded Kafka instance. This can help in situations
where using the kafka docker images are an issue.

To use this extension add the `io.kotest.extensions:kotest-extensions-embedded-kafka` module to your test compile path.


[<img src="https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-embedded-kafka.svg?label=latest%20release"/>](https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-embedded-kafka)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-embedded-kafka.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-embedded-kafka/)


### Getting started:

Register the `embeddedKafkaListener` listener in your test class:

```kotlin
class EmbeddedKafkaListenerTest : FunSpec({
  listener(embeddedKafkaListener)
})
```

or

```kotlin
class EmbeddedKafkaListenerTest : FunSpec() {
  init {
    listener(embeddedKafkaListener)
  }
}
```

And the broker will be started once the spec is created and stopped once the spec completes.

Note: The underlying [embedded kafka library](https://github.com/embeddedkafka/embedded-kafka) uses a global object for state. Do not start multiple kafka instances at the same time.

### Consumer / Producer

To create a consumer and producer we can use convenience methods on the listener:

```kotlin
class EmbeddedKafkaListenerTest : FunSpec({

   listener(embeddedKafkaListener)

   test("send / receive") {

     val producer = embeddedKafkaListener.stringStringProducer()
     producer.send(ProducerRecord("foo", "a"))
     producer.close()

     val consumer = embeddedKafkaListener.stringStringConsumer("foo")
     eventually(10.seconds) {
       consumer.poll(1000).first().value() shouldBe "a"
     }
     consumer.close()
   }

})
```

The `stringStringProducer` and `stringStringConsumer` methods return a producer / consumer that accept strings for the keys and values. Similar methods exist for byte pairs.

Alternatively, you can access the host/port the Kafka instance was deployed on and create the clients yourself:

```kotlin
class EmbeddedKafkaListenerTest : FunSpec({

   listener(embeddedKafkaListener)
   
   val props = Properties().apply {
      put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "${embeddedKafkaListener.host}:${embeddedKafkaListener.port}")
   }
   
   val producer = KafkaProducer<String, String>(props)
   
}
```


### Custom Ports

You can create a new instance of the listener specifying a port and then use that instance rather than
the default instance.

```kotlin
class EmbeddedKafkaCustomPortTest : FunSpec({

   val listener = EmbeddedKafkaListener(5678)
   listener(listener)

   test("send / receive") {

      val producer = listener.stringStringProducer()
      producer.send(ProducerRecord("foo", "a"))
      producer.close()

      val consumer = listener.stringStringConsumer("foo")
      eventually(10.seconds) {
         consumer.poll(1000).first().value() shouldBe "a"
      }
      consumer.close()
   }
})
```

You can also do specify the zookeeper port using an alternative overload.

```
val listener = EmbeddedKafkaListener(kafkaPort = 6005, zookeeperPort = 9005)
```


