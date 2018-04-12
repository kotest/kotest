package io.kotlintest.runner.junit5

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object TestDiscovery {

  init {
    ReflectionsHelper.registerUrlTypes()
  }

  data class DiscoveryRequest(val uris: List<URI>, val classNames: List<String>)

  private fun reflections(uris: List<URI>): Reflections {

    val classOnly = { name: String? -> name?.endsWith(".class") ?: false }
    val excludeJDKPackages = FilterBuilder.parsePackages("-java, -javax, -sun, -com.sun")

    return Reflections(ConfigurationBuilder()
        .addUrls(uris.map { it.toURL() })
        .setExpandSuperTypes(true)
        .useParallelExecutor(2)
        .filterInputsBy(excludeJDKPackages.add(classOnly))
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

    val classes = when {
      request.classNames.isNotEmpty() -> loadClasses(request.classNames)
      else -> scan(request.uris)
    }

    fun <T : Spec> createSpecInstance(clazz: KClass<T>): Spec {
      val nullSpec: Spec? = null
      val instance = Project.discoveryExtensions().fold(nullSpec, { spec, ext -> spec ?: ext.instantiate(clazz) })
      return instance ?: clazz.createInstance()
    }

    val filteredClasses = Project.discoveryExtensions().fold(classes, { cl, ext -> ext.afterScan(cl) })
    val instances = filteredClasses.map { createSpecInstance(it) }.sortedBy { it.name() }
    val descriptions = instances.map { it.root().description() }

    Project.listeners().forEach { it.afterDiscovery(descriptions) }

    val root = EngineDescriptor(uniqueId, "KotlinTest")
    instances.forEach {
      val specDescriptor = SpecTestDescriptor.fromSpecScope(root.uniqueId, it.root())
      it.root().scopes.forEach {
        val scopeDescriptor = when (it) {
          is TestContainer -> TestContainerDescriptor.fromTestContainer(specDescriptor.uniqueId, it)
          is TestCase -> TestCaseDescriptor.fromTestCase(specDescriptor.uniqueId, it)
          else -> throw IllegalArgumentException()
        }
        specDescriptor.addChild(scopeDescriptor)
      }
      root.addChild(specDescriptor)
    }
    return root
  }
}