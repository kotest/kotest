---
id: mountables
title: Mountables
slug: mountables.html
sidebar_label: Mountables
---

Mountables are a special type of extension that can be installed in a spec, returning what is called a materialized
value to the caller. This allows these extensions to return control objects which differ from the extension itself. The
mountable can be customized as it is created through an optional configuration
block.

Mountables are installed using the `install` function at the top level of a spec.

For example, we could imagine a Kafka extension that returns a Kafka client once installed.

```
class MyExampleTest : FunSpec() {
  init {
    val kafka = install(EmbeddedKafka) {
      port = 9092
    }
  }
}
```

Here `kafka` would be the materialized value and in this example, it could be a Kafka consumer connected to the
embedded Kafka instance. The configuration block is used to configure the extension, and `EmbeddedKafka` is the
extension itself.

Mountables can of course return the extension itself if they don't need a separate object, or return Unit.

Another example of a mountable is the JDBC Test Container extension, which returns a JDBC connection pool as the
materialized value. This makes using test containers in Kotest very convenient.

## Creating a Mountable

Implement the `MountableExtension` interface and additionally implement any other lifecycle methods you need. This is
another powerful feature of mountables, as it allows your extension to hook into other lifecycle events. For example,
your mountable instance can itself implement `AfterSpec` and then the `afterSpec` method of the mountable will be called
after the spec has finished running.

The interface has two type parameters: the config type and the materialized value type. The latter is the type that is
returned to the caller, and as mentioned previously, it can be the same as the extension itself. The config type is
passed as a receiver object to the configuration block. So this is where you define values or methods you want callers
to be able to assign or invoke.

:::caution
One drawback is that the configuration block is not suspendable, due to the fact that the init block is not itself
suspendable. To work around this, you can use runBlocking { } inside your implementation.
:::

## Example

Lets create a mountable that installs a H2 embedded database. The materialized value will be a connection to the
database instance. We will allow the user to configure some details of the database in a configuration
block. We will also implement `AfterSpec` so that the database is closed after the spec has finished running.

First we create the configuration class that contains our customization options.

```kotlin
class H2Config {
   var databaseName = "test"
}
```

Now we make the mountable extension, also implementing `AfterSpec` so we can close the connection after the spec has
finished running. Notice that we use the `mount` function to perform the installation logic, and this function
returns the materialized value - in this case, a JDBC connection to the database.

```kotlin
class H2DatabaseMountableExtension() : MountableExtension<H2Config, Connection>, AfterSpecListener {

   var conn: Connection? = null

   override fun mount(configure: H2Config.() -> Unit): Connection {
      val config = H2Config()
      config.configure()
      conn = DriverManager.getConnection("jdbc:h2:~/${config.databaseName}")
      return conn!!
   }

   override suspend fun afterSpec(spec: Spec) {
      conn?.close()
   }
}
```
