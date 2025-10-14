//package io.kotest.extensions.spring
//
//import ch.qos.logback.classic.Level
//import ch.qos.logback.classic.spi.ILoggingEvent
//import ch.qos.logback.core.read.ListAppender
//import io.kotest.core.extensions.ApplyExtension
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.shouldBe
//import org.slf4j.LoggerFactory
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.boot.runApplication
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.server.LocalServerPort
//import org.springframework.boot.web.embedded.tomcat.TomcatWebServer
//import org.springframework.context.ConfigurableApplicationContext
//import org.springframework.test.context.ContextConfigurationAttributes
//import org.springframework.test.context.ContextCustomizer
//import org.springframework.test.context.ContextCustomizerFactories
//import org.springframework.test.context.ContextCustomizerFactory
//import org.springframework.test.context.MergedContextConfiguration
//
//@SpringBootTest(
//   webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
//   classes = [KotestV6Application::class]
//)
//@ContextCustomizerFactories(DummyContextCustomizerFactory::class)
//@ApplyExtension(SpringExtension::class)
//class WithContextCustomizerFactoryTest(
//   @param:LocalServerPort val port: Int
//) : FunSpec({
//
//   val logger = LoggerFactory.getLogger(TomcatWebServer::class.java) as ch.qos.logback.classic.Logger
//   val memoryLogAppender = logger.getAppender("MEMORY") as ListAppender<ILoggingEvent>
//
//   afterTest {
//      memoryLogAppender.reset()
//   }
//
//   test("should log only one tomcat started") {
//      memoryLogAppender.countTomcatStartedLogs() shouldBe 1
//   }
//})
//
//fun ListAppender<ILoggingEvent>.countTomcatStartedLogs() = list
//   .filter { event -> event.level == Level.INFO && event.toString().contains("Tomcat initialized with port") }
//   .size
//
//fun ListAppender<ILoggingEvent>.reset() = list.clear()
//
//@SpringBootApplication
//open class KotestV6Application
//
//fun main(args: Array<String>) {
//   runApplication<KotestV6Application>(*args)
//}
//
//class DummyContextCustomizerFactory : ContextCustomizerFactory {
//   override fun createContextCustomizer(
//      testClass: Class<*>,
//      configAttributes: MutableList<ContextConfigurationAttributes>
//   ): ContextCustomizer = DummyCustomizer(testClass)
//}
//
//class DummyCustomizer(private val testClass: Class<*>) : ContextCustomizer {
//   override fun customizeContext(context: ConfigurableApplicationContext, mergedConfig: MergedContextConfiguration) {
//      println("STARTING customizer!")
//   }
//}
