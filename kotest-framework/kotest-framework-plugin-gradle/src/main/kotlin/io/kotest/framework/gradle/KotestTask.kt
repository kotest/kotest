package io.kotest.framework.gradle

import jetbrains.buildServer.messages.serviceMessages.ServiceMessagesParser
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitDetails
import org.gradle.api.file.FileVisitor
import org.gradle.api.internal.file.FileCollectionFactory
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.tasks.testing.DefaultTestSuiteDescriptor
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.internal.concurrent.ExecutorFactory
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.PipedInputStream
import java.io.PipedOutputStream
import javax.inject.Inject
import kotlin.concurrent.thread

// gradle requires the class be extendable
open class KotestTask @Inject constructor(
   private val fileResolver: FileResolver,
   private val fileCollectionFactory: FileCollectionFactory,
   private val executorFactory: ExecutorFactory,
) : DefaultTask() {

   private var tags: String? = null
   private var tests: String? = null

   // gradle will call this if --tests was specified on the command line
   @Option(option = "tests", description = "Filter to a single spec and/or test")
   fun setTests(tests: String) {
      this.tests = tests
   }

   // gradle will call this if --tags was specified on the command line
   @Option(option = "tags", description = "Set tag expression to include or exclude tests")
   fun setTags(tags: String) {
      this.tags = tags
   }

   @TaskAction
   fun executeTests() {
      println("Running tests with tags $tags and tests $tests")
      //val testResultsDir = project.buildDir.resolve("test-results")
      val testSourceSet = project.javaTestSourceSet() ?: return
      println("sourceset $testSourceSet")

      val sourceSets =
         project.extensions.findByType(JavaPluginExtension::class.java)?.sourceSets?.findByName("test") ?: return
      println("sourceSets $sourceSets")

      val specs = TestClassDetector().load(sourceSets.runtimeClasspath.asFileTree)
      println("specs are $specs")
//      val urls = classpaths.map { it.toURI().toURL() }

      val result = try {
         val builder = TestLauncherExecBuilder
            .builder(fileResolver, fileCollectionFactory, executorFactory)
            .withClasspath(testSourceSet.runtimeClasspath)
            .withSpecs(specs)
            .withCommandLineTags(tags)
//         if (hasRtJar()) {
//            builder.withStandardOutputConsumer(listener.output)
//            listener.start()
//         }
         val exec = builder.build()
         exec.execute()
      } catch (e: Exception) {
         println(e)
         e.printStackTrace()
         throw GradleException("Test process failed", e)
      }

      if (result.exitValue != 0) {
         throw GradleException("There were test failures")
      }
   }

   private fun hasRtJar(): Boolean {
      return try {
         this::class.java.classLoader.loadClass("com.intellij.rt.execution.application.AppMainV2") != null
      } catch (_: ClassNotFoundException) {
         false
      }
   }
}

/**
 * Scans a gradle classpath looking for classes that extend a spec class.
 * We use object-asm to scan the bytecode.
 */
class TestClassDetector {

   private val specClasses = listOf(
      "io/kotest/core/spec/style/AnnotationSpec",
      "io/kotest/core/spec/style/BehaviorSpec",
      "io/kotest/core/spec/style/DescribeSpec",
      "io/kotest/core/spec/style/ExpectSpec",
      "io/kotest/core/spec/style/FeatureSpec",
      "io/kotest/core/spec/style/FreeSpec",
      "io/kotest/core/spec/style/FunSpec",
      "io/kotest/core/spec/style/ShouldSpec",
      "io/kotest/core/spec/style/StringSpec",
      "io/kotest/core/spec/style/WordSpec",
   )

   fun load(candidates: FileTree): List<String> {
      val specs = mutableListOf<String>()
      candidates.filter { it.name.endsWith(".class") }.asFileTree.visit(object : FileVisitor {
         override fun visitDir(dirDetails: FileVisitDetails) {
         }

         override fun visitFile(fileDetails: FileVisitDetails) {
//            println("Visiting file ${fileDetails.file}")
            ClassReader(fileDetails.file.readBytes()).accept(object : ClassVisitor(Opcodes.ASM4) {
               override fun visit(
                  version: Int,
                  access: Int,
                  name: String,
                  signature: String?,
                  superName: String,
                  interfaces: Array<out String?>?
               ) {
                  if (specClasses.contains(superName)) {
                     println("Accepting spec $name")
                     specs.add(name)
                  }
               }
            }, ClassReader.SKIP_DEBUG)
         }
      })
      return specs.toList()
   }
}

class TeamCityListener {

   // the intput stream will receive anything written to the output stream
   private val input = PipedInputStream()

   // the output stream should be attached to the java exec process to receive whatever is written to stdout
   val output = PipedOutputStream(input)

   // this parser is provided by the kotlin gradle plugin library and will parse teamcity messages
   private val parser = ServiceMessagesParser()

   private val root = DefaultTestSuiteDescriptor("root", "root")

   // the service message parser emits events to a callback implementation which we provide
   private val callback = KotestServiceMessageParserCallback(root, emptyList(), mutableListOf())

   /**
    * Starts a new thread which consumes the input stream and parses the teamcity messages.
    */
   fun start() {
      thread {
//         listeners.forEach {
//            it.beforeSuite(root)
//         }
         input.bufferedReader().useLines { lines ->
            // the lines here is a lazy sequence which will be fed lines as they arrive from std out
            lines.forEach { parser.parse(it, callback) }
         }
//         listeners.forEach {
//            it.afterSuite(root, DefaultTestResult(TestResult.ResultType.SUCCESS, 0, 0, 0, 0, 0, emptyList()))
//         }
      }
   }
}
