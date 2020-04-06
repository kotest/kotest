import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.testcontainers.containers.GenericContainer
import kotlin.reflect.KClass

class ContainerPerSpecListener(private val genericContainer: GenericContainer<Nothing>) : TestListener {

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      genericContainer.stop()
      super.finalizeSpec(kclass, results)
   }

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      genericContainer.start()
      super.prepareSpec(kclass)
   }
}
