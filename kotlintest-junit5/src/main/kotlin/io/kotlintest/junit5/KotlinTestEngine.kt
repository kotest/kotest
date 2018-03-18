package io.kotlintest.junit5

import org.junit.platform.commons.JUnitException
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.PackageSelector
import java.lang.reflect.Modifier
import java.util.*

class KotlinTestEngine : TestEngine {

  override fun execute(request: ExecutionRequest) {
    Project.beforeAll()
    request.rootTestDescriptor.children.forEach {
      when (it) {
        is SpecDescriptor -> {
          val executor = when {
            it.spec.oneInstancePerTest -> OneInstanceSpecExecutor
            else -> SharedSpecExecutor
          }
          executor.execute(it, request.engineExecutionListener)
        }
        else -> throw IllegalStateException("All children of root test descriptor must be specs but $it")
      }
    }
    Project.afterAll()
  }

  override fun getId(): String = "io.kotlintest"

  override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {

    val isSpec: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) && !Modifier.isAbstract(it.modifiers) }

    val specs: List<Class<Spec>> = discoveryRequest.getSelectorsByType(PackageSelector::class.java).flatMap {
      ReflectionUtils.findAllClassesInPackage(it.packageName, isSpec, { true }).map {
        it as Class<Spec>
      }
    }

    val root = ContainerTestDescriptor(uniqueId, "Test Results")
    specs.forEach {
      val spec: Spec = it.newInstance()
      root.addChild(spec.specDescriptor)
    }
    return root
  }
}

// a container test descriptor that is used as the top level for each spec
class SpecDescriptor(id: UniqueId,
                     displayName: String,
                     val spec: Spec // the spec that this test descriptor was created in
) : ContainerTestDescriptor(id, displayName)

// a container test descriptor that can hold tests
open class ContainerTestDescriptor(private val id: UniqueId,
                                   private val displayName: String) : TestDescriptor {

  private val children = mutableListOf<TestDescriptor>()
  private var parent: Optional<TestDescriptor> = Optional.empty()

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER
  override fun getUniqueId(): UniqueId = id

  override fun getSource(): Optional<TestSource> = Optional.empty()

  override fun removeFromHierarchy() {
    if (isRoot) throw JUnitException("Cannot remove from hierarchy for root")
  }

  override fun setParent(parent: TestDescriptor) {
    this.parent = Optional.ofNullable(parent)
  }

  override fun getParent(): Optional<TestDescriptor> = parent

  override fun getChildren(): MutableSet<out TestDescriptor> = children.toMutableSet()

  override fun getDisplayName(): String = displayName

  override fun removeChild(descriptor: TestDescriptor?) {
    throw UnsupportedOperationException()
  }

  override fun addChild(descriptor: TestDescriptor) {
    descriptor.setParent(this)
    this.children.add(descriptor)
  }

  override fun findByUniqueId(uniqueId: UniqueId): Optional<out TestDescriptor> =
      if (uniqueId == id) Optional.of(this)
      else children.map { it.findByUniqueId(uniqueId) }.first { it.isPresent }

  override fun getTags(): MutableSet<TestTag> = mutableSetOf()
}