import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.testcontainers.containers.GenericContainer

class ContainerPerTestListener(private val genericContainer: GenericContainer<Nothing>) : TestListener {

   override suspend fun beforeTest(testCase: TestCase) {
      genericContainer.start()
      super.beforeTest(testCase)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      genericContainer.stop()
      super.afterTest(testCase, result)
   }
}
