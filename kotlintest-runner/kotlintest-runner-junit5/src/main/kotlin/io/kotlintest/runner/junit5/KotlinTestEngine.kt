package io.kotlintest.runner.junit5

import io.kotlintest.Spec
import io.kotlintest.runner.jvm.DiscoveryRequest
import io.kotlintest.runner.jvm.TestDiscovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.UriSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.reflections.util.ClasspathHelper
import kotlin.reflect.KClass

class KotlinTestEngine : org.junit.platform.engine.TestEngine {

  companion object {
    const val EngineId = "kotlintest"
  }

  override fun getId(): String = EngineId

  override fun execute(request: ExecutionRequest) {
    val root = request.rootTestDescriptor as KotlinTestEngineDescriptor
    val listener = JUnitTestRunnerListener(SynchronizedEngineExecutionListener(request.engineExecutionListener), root)
    val runner = io.kotlintest.runner.jvm.TestEngine(root.classes, listener)
    runner.execute()
  }

  override fun discover(request: EngineDiscoveryRequest,
                        uniqueId: UniqueId): EngineDescriptor {

    // inside intellij when running a single test, we might be passed a class selector
    // which will be the classname of a spec implementation
    val classSelectors = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

    val uris = request.getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
        request.getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
        request.getSelectorsByType(UriSelector::class.java).map { it.uri } +
        ClasspathHelper.forClassLoader().toList().map { it.toURI() }

    val classes = TestDiscovery.discover(DiscoveryRequest(uris, classSelectors))
    return KotlinTestEngineDescriptor(uniqueId, classes)
  }

  class KotlinTestEngineDescriptor(val id: UniqueId, val classes: List<KClass<out Spec>>) : EngineDescriptor(id, "KotlinTest") {
    override fun mayRegisterTests(): Boolean = true
  }
}


