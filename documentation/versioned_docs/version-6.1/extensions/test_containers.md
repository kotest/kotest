---
id: test_containers
title: Testcontainers
sidebar_label: Testcontainers
slug: test_containers.html
---

## Testcontainers

[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-testcontainers.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-testcontainers)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-testcontainers%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-testcontainers/maven-metadata.xml)

:::note
This documentation is for the latest release of the Testcontainers module and is compatible with Kotest 6.0+.
:::

The [Testcontainers](https://github.com/testcontainers/testcontainers-java) project provides lightweight, ephemeral
instances of common databases, Elasticsearch, Kafka, Redis, or anything else that can run in a Docker container - ideal
for use inside tests.

Kotest provides integration with `Testcontainers` through an additional module which provides support for:

* `GenericContainer`'s
* `ComposeContainer`s
* `JDBC` compatible databases

## Dependencies

To begin, add the following dependency to your Gradle build file.

```
io.kotest:kotest-extensions-testcontainers:${kotest.version}
```

For Maven, you will need these dependencies:

```xml

<dependency>
  <groupId>io.kotest</groupId>
  <artifactId>kotest-extensions-testcontainers</artifactId>
  <version>${kotest.version}</version>
  <scope>test</scope>
</dependency>
```

:::note
Since Kotest 6.0, all extensions are published under the `io.kotest` group with version cadence tied to the main Kotest
releases.
:::

## Generic Containers

Kotest provides two `GenericContainer` extensions - `TestContainerSpecExtension` and `TestContainerProjectExtension`
which can be used to wrap any container and which tie the lifecycle of the container to the lifecycle of the spec or
project depending on which extension is used.

We can create the extension using either a strongly typed container, or docker image name.

Using a strongly typed container:

```kotlin
val elasticsearch = install(ContainerExtension(ElasticsearchContainer(ELASTICSEARCH_IMAGE))) {
  withPassword(ELASTICSEARCH_PASSWORD)
}
```

Or using a docker image name:

```kotlin
val container = install(TestContainerSpecExtension(GenericContainer("redis:5.0.3-alpine"))) {
  startupAttempts = 2
  withExposedPorts(6379)
}
val jedis = JedisPool(container.host, container.firstMappedPort)
```

The strongly typed container is preferred when one is provided by the `Testcontainers` project, because it gives
access to specific settings - such as the `password` option in the elasticsearch example above. However, when a strongly
typed container is not available, the former method allows us to fall back to any docker image as a general container.

Once the container is installed, you can use the containers's host and port to configure a client to access that
container. For example, to connect a Jedis client to a Redis container:

```kotlin
val container = install(TestContainerSpecExtension(GenericContainer("redis:5.0.3-alpine")))
val jedis = JedisPool(container.host, container.firstMappedPort)
```

:::tip
Use the `TestContainerProjectExtension` if you want to share the same container across multiple specs in a
project for faster startup times.
:::

## Databases

For JDBC compatible databases, Kotest provides the `JdbcDatabaseContainerSpecExtension` and
`JdbcDatabaseContainerProjectExtension`. These return not the container directly, but a `javax.sql.DataSource`, backed
by an instance of [HikariCP](https://github.com/brettwooldridge/HikariCP), which can be configured during setup.

Firstly, create the container.

```kotlin
val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
  startupAttempts = 1
  withUrlParam("connectionTimeZone", "Z")
  withUrlParam("zeroDateTimeBehavior", "convertToNull")
}
```

Secondly, install the container inside an extension, providing an optional configuration lambda for Hikari.

```kotlin
val ds = install(JdbcDatabaseContainerSpecExtension(mysql)) {
  // these are hikari pool config options
  poolName = "myconnectionpool"
  maximumPoolSize = 8
  idleTimeout = 10000
}
```

If you don't wish to configure the pool, then you can omit the trailing lambda. If you don't want to use Hikari, then
you can use the generic extensions instead.

Then the datasource can be used in a test. For example, here is a full example of inserting some
objects and then retrieving them to test that the insert was successful.

```kotlin
class QueryDatastoreTest : FunSpec({

  val mysql = MySQLContainer<Nothing>("mysql:8.0.26").apply {
    startupAttempts = 1
    withUrlParam("connectionTimeZone", "Z")
    withUrlParam("zeroDateTimeBehavior", "convertToNull")
  }

  val ds = install(JdbcDatabaseContainerSpecExtension(mysql)) {
    poolName = "myconnectionpool"
    maximumPoolSize = 8
    idleTimeout = 10000
  }

  val datastore = PersonDatastore(ds)

  test("insert happy path") {

    datastore.insert(Person("sam", "Chicago"))
    datastore.insert(Person("jim", "Seattle"))

    datastore.findAll().shouldBe(
      listOf(
        Person("sam", "Chicago"),
        Person("jim", "Seattle"),
      )
    )
  }
})
```

:::tip
Use the `JdbcDatabaseContainerProjectExtension` if you want to share the same container across multiple specs in a
project for faster startup times.
:::

## Compose Containers

Kotest provides two extensions for `ComposeContainer`s - `ComposeContainerSpecExtension` and
`ComposeContainerProjectExtension`. This extension can be used to wrap `ComposeContainer` and startup multiple
containers at once defined in a docker compose file.

We can create the extension using a `File` pointing to the docker compose file:

```kotlin
val container = install(ComposeContainerSpecExtension(ComposeContainer(File("my-compose-file.yml"))))
```

Alternative, if our docker compose file is in the resources folder of our project, we can use the following shortcut:

```kotlin
// eg for src/main/resources/docker-compose.yml
val container = install(ComposeContainerSpecExtension.fromResource("docker-compose.yml"))
```

## Container Logs

Kotest provides the option to capture the logs from the containers that are started by the extensions and output
those in the test console. This can be enabled by passing an instance of `ContainerExtensionConfig` to the extension.
In the config instance, set the `logConsumer` option to be `StandardLogConsumer`, specifying the level of logs to
capture. For example:

```kotlin
install(
   TestContainerSpecExtension(
      container,
      ContainerExtensionConfig(logConsumer = StandardLogConsumer(LogTypes.ALL))
   )
)
```

The log types can be set to capture `ALL`, `STDOUT`, `STDERR` or `NONE`.
