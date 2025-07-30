package io.kotest.extensions.testcontainers

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.kafka.KafkaContainer
import java.util.Properties
import java.util.UUID

/**
 * Creates a [KafkaProducer] for the given [KafkaContainer] with a String serializer for both keys and values.
 */
fun KafkaContainer.createStringStringProducer(
   configure: Properties.() -> Unit = {},
): KafkaProducer<String, String> {
   return createProducer(StringSerializer(), StringSerializer(), configure)
}

/**
 * Creates a [KafkaProducer] for the given [KafkaContainer] with the given serializers.
 */
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

/**
 * Creates a [KafkaConsumer] for the given [KafkaContainer] with a String deserializer for both keys and values.
 */
fun KafkaContainer.createStringStringConsumer(
   configure: Properties.() -> Unit = {},
): KafkaConsumer<String, String> {
   return createConsumer(StringDeserializer(), StringDeserializer(), configure)
}

/**
 * Creates a [KafkaConsumer] for the given [KafkaContainer] with the given deserializers.
 */
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

/**
 * Creates a [AdminClient] for the given [KafkaContainer].
 */
fun KafkaContainer.createAdminClient(configure: Properties.() -> Unit = {}): AdminClient {
   val props = Properties()
   props[CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
   props.configure()
   return AdminClient.create(props)
}
