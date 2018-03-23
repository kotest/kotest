package io.kotlintest.runner.junit5

import io.kotlintest.Spec
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import java.lang.reflect.Modifier
import java.net.URI
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.jvmName

object TestDiscovery {

  data class DiscoveryRequest(val uris: List<URI>, val classNames: List<String>)

  val isSpec: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) && !Modifier.isAbstract(it.modifiers) }

  private fun reflections(request: DiscoveryRequest): Reflections {
    return Reflections(ConfigurationBuilder()
        .addUrls(request.uris.map { it.toURL() })
        .setScanners(SubTypesScanner()))
  }

  // returns all the locatable specs for the given request
  private fun scan(request: DiscoveryRequest): List<KClass<out Spec>> =
      reflections(request)
          .getSubTypesOf(Spec::class.java)
          .map(Class<out Spec>::kotlin)
          // must filter out abstract to avoid the spec parent classes themselves
          .filter { !it.isAbstract }
          .filter { request.classNames.isEmpty() || request.classNames.contains(it.qualifiedName) }

  operator fun invoke(request: DiscoveryRequest, uniqueId: UniqueId): TestDescriptor {

    val root = RootTestDescriptor(uniqueId.append("root", "kotlintest"))
    val specs = scan(request)

    specs.forEach {
      val spec: Spec = it.createInstance()
      val descriptor = TestContainerDescriptor.fromTestContainer(root.uniqueId, spec.root())
      root.addChild(descriptor)
      root.sortChildren()
    }

    return root
  }
}