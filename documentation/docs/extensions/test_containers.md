---
id: test_containers
title: Testcontainers
sidebar_label: Testcontainers
slug: test_containers.html
---



## Testcontainers

:::note
This documentation is for the latest release of the Testcontainers module and is compatible with Kotest 5.0+.
For earlier versions see docs [here](https://kotest.io/docs/extensions/test_containers_46.html)
:::

The [Testcontainers](https://github.com/testcontainers/testcontainers-java) project provides lightweight, ephemeral instances of common databases,
elasticsearch, kafka, Selenium web browsers, or anything else that can run in a Docker container - ideal for use inside tests.

Kotest provides integration with Testcontainers through an additional module which provides several extensions - specialized extensions for
databases and kafka and general containers support for any supported docker image.

### Dependencies

To begin, add the following dependency to your Gradle build file.

```groovy
io.kotest:kotest-extensions-testcontainers:${kotest.version}
```

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-testcontainers.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-testcontainers)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-testcontainers%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-testcontainers/maven-metadata.xml)


:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group once again, with version cadence tied to
main Kotest releases.
:::

For Maven, you will need these dependencies:

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-extensions-testcontainers</artifactId>
    <version>${kotest.version}</version>
    <scope>test</scope>
</dependency>
```

### Databases

For JDBC compatible databases, Kotest provides the `JdbcTestContainerExtension`. This provides a pooled `javax.sql.DataSource`, backed by
an instance of [HikariCP](https://github.com/brettwooldridge/HikariCP), which can be configured during setup.

Firstly, create the container.

```kotlin
val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
  startupAttempts = 1
  withUrlParam("connectionTimeZone", "Z")
  withUrlParam("zeroDateTimeBehavior", "convertToNull")
}
```

Secondly, install the container inside an extension wrapper, providing an optional configuration lambda.

```kotlin
val ds = install(JdbcDatabaseContainerExtension(mysql)) {
  poolName = "myconnectionpool"
  maximumPoolSize = 8
  idleTimeout = 10000
}
```

If you don't wish to configure the pool, then you can omit the trailing lambda.

Then the datasource can be used in a test. For example, here is a full example of inserting some
objects and then retrieving them to test that the insert was successful.

```kotlin
class QueryDatastoreTest : FunSpec({

  val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
    startupAttempts = 1
    withUrlParam("connectionTimeZone", "Z")
    withUrlParam("zeroDateTimeBehavior", "convertToNull")
  }

  val ds = install(JdbcDatabaseContainerExtension(mysql)) {
    poolName = "myconnectionpool"
    maximumPoolSize = 8
    idleTimeout = 10000
  }

  val datastore = PersonDatastore(ds)

  test("insert happy path") {

    datastore.insert(Person("sam", "Chicago"))
    datastore.insert(Person("jim", "Seattle"))

    datastore.findAll().shouldBe(listOf(
      Person("sam", "Chicago"),
      Person("jim", "Seattle"),
    ))
  }
})
```

:::tip
This extension also supports the `ContainerLifecycleMode` flag to control when the container is started and stopped.
See [Lifecycle](#lifecycle)
:::

#### Initializing the Database Container
There are two ways to initialize the database container: via a _single_ init script added to the TestContainer config,
or via a list of scripts added to the JdbcTestContainerExtension config lambda.

If adding a _**single**_ script, via the TestContainer config, simply add the script to the TestContainer's `withInitScript` config option, like so:
```kotlin
val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
         withInitScript("init.sql")
         startupAttempts = 1
         withUrlParam("connectionTimeZone", "Z")
         withUrlParam("zeroDateTimeBehavior", "convertToNull")
      }
```

If you have multiple init scripts or sets of changesets, you can add them as a list to the `dbInitScripts` extension config lambda, like so:
```kotlin
val ds: DataSource = install(JdbcDatabaseContainerExtension(mysql)) {
      maximumPoolSize = 8
      minimumIdle = 4
      dbInitScripts = listOf("/init.sql", "/sql-changesets")
   }
```
The list can contain absolute or relative paths, for files and folders on the filesystem or on the classpath.

The extension will process the list provided in order. If the list item is a folder, it will process all `.sql` scripts in the folder,
sorted lexicographically. These scripts run every time the container is started, so it supports the `ContainerLifecycleMode` flag.


### General Containers

Similar to the `JdbcDatabaseContainerExtension`, this module also provides a `ContainerExtension` extension which can
wrap any container, not just databases.

We can create the extension using either a docker image name, or a strongly typed container.

For example, using a docker image directly:

```kotlin
val container = install(ContainerExtension("redis:5.0.3-alpine")) {
  startupAttempts = 1
  withExposedPorts(6379)
}
```

And then using a strongly typed container:

```kotlin
val elasticsearch = install(ContainerExtension(ElasticsearchContainer(ELASTICSEARCH_IMAGE) )) {
  withPassword(ELASTICSEARCH_PASSWORD)
}
```

The strongly typed container is preferred when one is provided by the Testcontainers project, because it gives us
access to specific settings - such as the password option in the elasticsearch example above.

However, when a strongly typed container is not available, the former method allows us to spool up any docker image
as a general container.

:::tip
This extension also supports the `ContainerLifecycleMode` flag to control when the container is started and stopped.
See [Lifecycle](#lifecycle)
:::


### Kafka Containers

For Kafka, this module provides convenient extension methods to create a consumer, producer or admin client from the container.

```kotlin
val kafka = install(ContainerExtension(KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")))) {
  withEmbeddedZookeeper()
}
```

Inside the configuration lambda, we can specify options for the Kafka container, such as embedded/external zookeeper,
or kafka broker properties through env vars. For example, to enable dynamic topic creation:

```kotlin
val kafka = install(ContainerExtension(KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")))) {
  withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
}
```
:::caution Note for Apple Silicon/ARM Users
Kafka only publishes a `linux/amd64` version of the container. If you're on an Apple Silicon/ARM architecture computer,
you'll need to explicitly specify the platform with the following added to the configuration lambda outlined above:
```kotlin
withCreateContainerCmdModifier { it.withPlatform("linux/amd64") }
```
:::



Once we have the container installed, we can create a client using the following methods:

* container.createProducer()
* container.createStringStringProducer()
* container.createConsumer()
* container.createStringStringConsumer()
* container.createAdminClient()

Each of these accepts an optional configuration lambda to enable setting values on the properties object that is
used to create the clients.

For example, in this test, we produce and consume a message from the same topic, and we use the configuration
lambda to set max poll to 1.

```kotlin
class KafkaTestContainerExtensionTest : FunSpec() {
  init {

    val kafka = install(ContainerExtension(KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1")))) {
      withEmbeddedZookeeper()
    }

    test("should send/receive message") {

      val producer = kafka.createStringStringProducer()
      producer.send(ProducerRecord("foo", null, "bubble bobble"))
      producer.close()

      val consumer = kafka.createStringStringConsumer {
        this[ConsumerConfig.MAX_POLL_RECORDS_CONFIG] = 1
      }

      consumer.subscribe(listOf("foo"))
      val records = consumer.poll(Duration.ofSeconds(100))
      records.shouldHaveSize(1)
    }
  }
}
```


:::note
When creating a consumer, the consumer group is set to a random uuid. To change this, provide
a configuration lambda and specify your own group consumer group id.
:::


### Lifecycle

By default, the lifecycle of a container is per spec - so it will be started at the `install` command, and shutdown as
the spec is completed. This can be changed to start/stop per test, per leaf test, or per root test.

To do this, pass in a `ContainerLifecycleMode` parameter to the `ContainerExtension` or `JdbcDatabaseContainerExtension`.

For example:

```kotlin
val ds = install(JdbcDatabaseContainerExtension(mysql, ContainerLifecycleMode.Spec)) {
  poolName = "myconnectionpool"
  maximumPoolSize = 8
  idleTimeout = 10000
}
```

### Startables

This module also provides extension methodsscope which let you convert
any `Startable` such as a `DockerContainer` into a kotest `TestListener`, which you can register with Kotest
and then Kotest will manage the lifecycle of that container for you.

For example:

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.testcontainers.perTest
import org.testcontainers.containers.GenericContainer

class DatabaseRepositoryTest : FunSpec({
   val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
   listener(redisContainer.perTest()) //converts container to listener and registering it with Kotest.

   test("some test which assume to have redis container running") {
      //
   }
})
```

In above example, the `perTest()` extension method converts the container into a `TestListener`, which starts the
redis container before each test and stops it after test. Similarly if you want to reuse the container for all tests
in a single spec class you can use `perSpec()` extension method, which converts the container into a `TestListener`
which starts the container before running any test in the spec, and stops it after all tests, thus a single container is
used by all tests in spec class.
