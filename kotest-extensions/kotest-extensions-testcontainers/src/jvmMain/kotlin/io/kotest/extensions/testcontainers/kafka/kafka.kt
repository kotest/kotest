package io.kotest.extensions.testcontainers.kafka

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.containers.KafkaContainer
import java.util.Properties
import java.util.UUID

@Deprecated("Use org.testcontainers.kafka.KafkaContainer or org.testcontainers.kafka.ConfluentKafkaContainer. Deprecated since 6.0")
fun KafkaContainer.createStringStringProducer(
   configure: Properties.() -> Unit = {},
): KafkaProducer<String, String> {
   return createProducer(StringSerializer(), StringSerializer(), configure)
}

@Deprecated("Use org.testcontainers.kafka.KafkaContainer or org.testcontainers.kafka.ConfluentKafkaContainer. Deprecated since 6.0")
fun <K, V> KafkaContainer.createProducer(
   kserializer: Serializer<K>,
   vserializer: Serializer<V>,
   configure: Properties.() -> Unit = {},
): KafkaProducer<K, V> {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props.configure()
   return KafkaProducer(props, kserializer, vserializer)
}

@Deprecated("Use org.testcontainers.kafka.KafkaContainer or org.testcontainers.kafka.ConfluentKafkaContainer. Deprecated since 6.0")
fun KafkaContainer.createStringStringConsumer(
   configure: Properties.() -> Unit = {},
): KafkaConsumer<String, String> {
   return createConsumer(StringDeserializer(), StringDeserializer(), configure)
}

@Deprecated("Use org.testcontainers.kafka.KafkaContainer or org.testcontainers.kafka.ConfluentKafkaContainer. Deprecated since 6.0")
fun <K, V> KafkaContainer.createConsumer(
   kserializer: Deserializer<K>,
   vserializer: Deserializer<V>,
   configure: Properties.() -> Unit = {},
): KafkaConsumer<K, V> {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props[CommonClientConfigs.GROUP_ID_CONFIG] = UUID.randomUUID().toString().replace("-", "")
   props.configure()
   return KafkaConsumer(props, kserializer, vserializer)
}

@Deprecated("Use org.testcontainers.kafka.KafkaContainer or org.testcontainers.kafka.ConfluentKafkaContainer. Deprecated since 6.0")
fun KafkaContainer.createAdminClient(configure: Properties.() -> Unit = {}): AdminClient {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props.configure()
   return AdminClient.create(props)
}
