import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import org.testcontainers.containers.GenericContainer

class ContainerPerSpecListenerTest : StringSpec() {
   init {
      "should stop container in finalizeSpec callback" {
         val mockContainer: GenericContainer<Nothing> = mockk(relaxed = true)
         val containerPerSpecListener = ContainerPerSpecListener(mockContainer)

         containerPerSpecListener.finalizeSpec(mockk(), emptyMap())

         verify(exactly = 1) { mockContainer.stop() }
      }

      "should start container in prepareSpec callback" {
         val mockContainer: GenericContainer<Nothing> = mockk(relaxed = true)
         val containerPerSpecListener = ContainerPerSpecListener(mockContainer)

         containerPerSpecListener.prepareSpec(mockk())

         verify(exactly = 1) { mockContainer.start() }
      }
   }
}
