package io.kotlintest.runner.junit5

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.runner.jvm.DiscoveryRequest
import io.kotlintest.runner.jvm.IsolationTestEngineListener
import io.kotlintest.runner.jvm.SynchronizedTestEngineListener
import io.kotlintest.runner.jvm.TestDiscovery
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathResourceSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.FileSelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.ModuleSelector
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.discovery.UriSelector
import org.junit.platform.engine.support.descriptor.EngineDescriptor
import org.reflections.util.ClasspathHelper
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class KotlinTestEngine : org.junit.platform.engine.TestEngine {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  companion object {
    const val EngineId = "kotlintest"
  }

  override fun getId(): String = EngineId

  override fun execute(request: ExecutionRequest) {
    logger.debug("JUnit execution request [configurationParameters=${request.configurationParameters}; rootTestDescriptor=${request.rootTestDescriptor}]")
    val root = request.rootTestDescriptor as KotlinTestEngineDescriptor
    val listener = SynchronizedTestEngineListener(IsolationTestEngineListener(JUnitTestRunnerListener(SynchronizedEngineExecutionListener(request.engineExecutionListener), root)))
    val runner = io.kotlintest.runner.jvm.TestEngine(root.classes, Project.parallelism(), listener)
    runner.execute()
  }

  override fun discover(request: EngineDiscoveryRequest,
                        uniqueId: UniqueId): EngineDescriptor {
    logger.debug("JUnit discovery request [" +
        "configParameters=${request.configurationParameters}; " +
        "classSelectors=${request.getSelectorsByType(ClassSelector::class.java)}; " +
        "classpathResourceSelectors=${request.getSelectorsByType(ClasspathResourceSelector::class.java)}; " +
        "classpathRootSelectors=${request.getSelectorsByType(ClasspathRootSelector::class.java)}; " +
        "methodSelectors=${request.getSelectorsByType(MethodSelector::class.java)}; " +
        "fileSelectors=${request.getSelectorsByType(FileSelector::class.java)}; " +
        "directorySelectors=${request.getSelectorsByType(DirectorySelector::class.java)}; " +
        "moduleSelectors=${request.getSelectorsByType(ModuleSelector::class.java)}; " +
        "packageSelectors=${request.getSelectorsByType(PackageSelector::class.java)}; " +
        "uniqueIdSelectors=${request.getSelectorsByType(UniqueIdSelector::class.java)}; " +
        "uriSelectors=${request.getSelectorsByType(UriSelector::class.java)}; " +
        "classnameFilters=${request.getFiltersByType(ClassNameFilter::class.java)}; " +
        "packageNameFilters=${request.getFiltersByType(PackageNameFilter::class.java)}; " +
        "uniqueId=$uniqueId]")

    // inside intellij when running a single test, we might be passed a class selector
    // and gradle will sometimes pass a class selector for each class it has detected
    val classSelectors = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

    val uris = request.getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
        request.getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
        request.getSelectorsByType(UriSelector::class.java).map { it.uri } +
        ClasspathHelper.forClassLoader().toList().map { it.toURI() }

    val result = TestDiscovery.discover(DiscoveryRequest(uris, classSelectors))
    return KotlinTestEngineDescriptor(uniqueId, result.classes)
  }

  class KotlinTestEngineDescriptor(val id: UniqueId,
                                   val classes: List<KClass<out Spec>>) : EngineDescriptor(id, "KotlinTest") {
    override fun mayRegisterTests(): Boolean = true
  }
}


