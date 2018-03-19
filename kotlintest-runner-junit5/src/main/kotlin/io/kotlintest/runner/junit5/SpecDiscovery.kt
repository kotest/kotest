package io.kotlintest.runner.junit5

import io.kotlintest.Spec
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.PackageSelector
import java.lang.reflect.Modifier

object SpecDiscovery {

  val isSpec: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) && !Modifier.isAbstract(it.modifiers) }

  // returns all the locatable specs for the given request
  private fun specs(request: EngineDiscoveryRequest): List<Class<Spec>> = request.getSelectorsByType(PackageSelector::class.java).flatMap {
    println("Searching ${it.packageName}")
    ReflectionUtils.findAllClassesInPackage(it.packageName, isSpec, { true }).map {
      it as Class<Spec>
    }.sortedBy { it.simpleName }
  }

  operator fun invoke(request: EngineDiscoveryRequest, uniqueId: UniqueId): RootTestDescriptor {
    val root = RootTestDescriptor(uniqueId.append("root", "kotlintest"))
    val specs = specs(request)

    specs.forEach {
      val spec: Spec = it.newInstance()
      root.addChild(TestContainerDescriptor.fromTestContainer(root.uniqueId, spec.root()))
    }

    return root
  }
}