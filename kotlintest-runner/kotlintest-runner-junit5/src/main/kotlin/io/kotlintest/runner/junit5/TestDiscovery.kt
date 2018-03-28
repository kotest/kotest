package io.kotlintest.runner.junit5

import io.kotlintest.Description
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object TestDiscovery {

  init {
    ReflectionsHelper.registerUrlTypes()
  }

  data class DiscoveryRequest(val uris: List<URI>, val classNames: List<String>)

  val isSpec: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) && !Modifier.isAbstract(it.modifiers) }

  private fun reflections(uris: List<URI>): Reflections {
    return Reflections(ConfigurationBuilder()
        .addUrls(uris.map { it.toURL() })
        .setScanners(SubTypesScanner()))
  }

  // returns all the locatable specs for the given uris
  private fun scan(uris: List<URI>): List<KClass<out Spec>> =
      reflections(uris)
          .getSubTypesOf(Spec::class.java)
          .map(Class<out Spec>::kotlin)
          // must filter out abstract to avoid the spec parent classes themselves
          .filter { !it.isAbstract }

  private fun loadClasses(classes: List<String>): List<KClass<out Spec>> =
      classes.map { Class.forName(it).kotlin }.filterIsInstance<KClass<out Spec>>()

  operator fun invoke(request: DiscoveryRequest, uniqueId: UniqueId): EngineDescriptor {

    val root = EngineDescriptor(uniqueId.append("root", "kotlintest"), "KotlinTest")

    val specs = when {
      request.classNames.isNotEmpty() -> loadClasses(request.classNames)
      else -> scan(request.uris)
    }

    val descriptions = mutableListOf<Description>()

    val instances = specs.map { it.createInstance() }.sortedBy { it.name() }
    instances.forEach {
      descriptions.add(it.root().description())
      val descriptor = SpecTestDescriptor.fromSpecScope(root.uniqueId, it.root())
      it.root().scopes.forEach {
        val newDescriptor = when (it) {
          is TestContainer -> TestContainerDescriptor.fromTestContainer(descriptor.uniqueId, it)
          is TestCase -> TestCaseDescriptor.fromTestCase(descriptor.uniqueId, it)
          else -> throw IllegalArgumentException()
        }
        descriptor.addChild(newDescriptor)
      }
      root.addChild(descriptor)
    }

    Project.listeners().forEach { it.afterDiscovery(descriptions.toList()) }

    return root
  }
}