package io.kotest.matchers.compilation

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import com.tschuchort.compiletesting.SourceFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import java.io.File

@EnabledIf(LinuxOnlyGithubCondition::class)
class CompilationsMatcherTest : StringSpec() {
   private lateinit var fileA: File
   private lateinit var fileB: File

   override suspend fun beforeTest(testCase: TestCase) {
      fileA = File("codeSnippetA.kt")
      fileB = File("codeSnippetB.kt")
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      fileA.delete()
      fileB.delete()
   }

   init {

      "a code snippet of invalid variable assignment should not compile" {
         val rawStringCodeSnippet = """
             val aString: String = 123
          """

         val syntaxHighlightedCodeSnippet = codeSnippet("""
             val aString: String = 123
         """)

         rawStringCodeSnippet.shouldNotCompile()
         syntaxHighlightedCodeSnippet.shouldNotCompile()
         fileA.writeText(rawStringCodeSnippet)
         fileA.shouldNotCompile()
      }

      "a code snippet should not compile and should produce the correct error message" {
         val rawStringCodeSnippet = """
             val aString: String = 123
          """

         val syntaxHighlightedCodeSnippet = codeSnippet("""
             val aString: String = 123
         """)

         val expectedErrorMsg = "Initializer type mismatch: expected 'String', actual 'Int'"
         rawStringCodeSnippet.shouldNotCompile(expectedErrorMsg)
         syntaxHighlightedCodeSnippet.shouldNotCompile(expectedErrorMsg)
         fileA.writeText(rawStringCodeSnippet)
         fileA.shouldNotCompile(expectedErrorMsg)
      }

      "shouldNotCompile() should throw AssertionError if the expected message is incorrect" {
         val rawStringCodeSnippet = """
             val aString: String = 123
          """

         shouldThrow<AssertionError> {
            rawStringCodeSnippet.shouldNotCompile("wobble")
         }
         fileA.writeText(rawStringCodeSnippet)
         shouldThrow<AssertionError> {
            fileA.shouldNotCompile("wobble")
            fileA.toPath().shouldNotCompile("wobble")
         }
      }

      "a code snippet with missing variable assignment should not compile" {
         val codeSnippet = """
            package org.bar.foo

            fun foo() {
               val aLocalDate: LocalDate = LocalDate.now()
               println(aLocalDate)
            }
          """

         codeSnippet.shouldNotCompile()
         fileA.writeText(codeSnippet)
         fileA.shouldNotCompile()
         fileA.toPath().shouldNotCompile()
      }

      "a code snippet with a proper import statement should compile" {
         val codeSnippet = """
            package org.bar.foo
            import java.time.LocalDate

            fun foo() {
               val aLocalDate: LocalDate = LocalDate.now()
               println(aLocalDate)
            }
          """

         codeSnippet.shouldCompile()
         fileA.writeText(codeSnippet)
         fileA.shouldCompile()
         fileA.toPath().shouldCompile()
      }

      "a code snippet with an invalid import statement should not compile" {
         val codeSnippet = """
            package org.bar.foo
            import foo.time.LocalDate

            fun foo() {
               val aLocalDate: LocalDate = LocalDate.now()
               println(aLocalDate)
            }
          """

         codeSnippet.shouldNotCompile()
         fileA.writeText(codeSnippet)
         fileA.shouldNotCompile()
         fileA.toPath().shouldNotCompile()
      }

      "a code snippet with an inline function should compile" {
         codeSnippet("""
            val aString = io.kotest.matchers.compilation.inlinedFunc(123)
         """).shouldCompile()
      }

      "a code snippets in multiple files with invalid import statement should not compile" {
         val fileASnippet = """
               abstract class SourceFileA
            """.trimIndent()
         val fileBSnippet = """
               class SourceFileB : SourceFileA()

               package org.bar.foo
               import foo.time.LocalDate

               fun foo() {
                  val aLocalDate: LocalDate = LocalDate.now()
                  println(aLocalDate)
               }
            """
         mapOf(
            "SourceFileA.kt" to fileASnippet,
            "SourceFileB.kt" to fileBSnippet
         ).shouldNotCompile()
         codeSnippet(
            SourceFile.kotlin("SourceFileA.kt", fileASnippet),
            SourceFile.kotlin("SourceFileB.kt", fileBSnippet)
         ).shouldNotCompile()

         fileA.writeText(fileASnippet)
         fileB.writeText(fileBSnippet)

         listOf(fileA, fileB).shouldNotCompile()
         listOf(fileA.toPath(), fileB.toPath()).shouldNotCompile()
      }

      "a code snippets in multiple files should compile" {
         val fileASnippet = """
               class SourceFileA : SourceFileB()
            """.trimIndent()
         val fileBSnippet ="""
               abstract class SourceFileB
            """.trimIndent()

         mapOf(
            "SourceFileA.kt" to fileASnippet,
            "SourceFileB.kt" to fileBSnippet
         ).shouldCompile()

         codeSnippet(
            SourceFile.kotlin("SourceFileA.kt", fileASnippet),
            SourceFile.kotlin("SourceFileB.kt", fileBSnippet)
         ).shouldCompile()
         listOf(
            SourceFile.kotlin("SourceFileA.kt", fileASnippet),
            SourceFile.kotlin("SourceFileB.kt", fileBSnippet)
         ).shouldCompile()

         fileA.writeText(fileASnippet)
         fileB.writeText(fileBSnippet)

         listOf(fileA, fileB).shouldCompile()
         listOf(fileA.toPath(), fileB.toPath()).shouldCompile()
      }

      @OptIn(ExperimentalCompilerApi::class)
      "a code snippet should not compile and should produce several error messages" {
         val codeSnippet = codeSnippet("""
            fun foo() {
               printDate(LocalDate.now())
            }
          """)

         codeSnippet.compile {
            exitCode shouldBe ExitCode.COMPILATION_ERROR
            messages shouldContain "Unresolved reference 'LocalDate'"
            messages shouldContain "Unresolved reference 'printDate'"
         }
      }

      @OptIn(ExperimentalCompilerApi::class)
      "compilation output should contain a message from the dummy compiler plugin" {
         val compileConfig = CompileConfig {
            compilerPluginRegistrars = listOf(DummyCompilerPluginRegistrar())
            verbose = true
         }

         val codeSnippet = compileConfig.codeSnippet("""
            val a = 123
         """)

         codeSnippet.compile {
            exitCode shouldBe ExitCode.OK
            messages shouldContain "Hello from the dummy compiler plugin"
         }
      }
   }
}

@Suppress("NOTHING_TO_INLINE", "unused")
inline fun inlinedFunc(num: Int): String {
   return num.toString()
}

@OptIn(ExperimentalCompilerApi::class)
class DummyCompilerPluginRegistrar : CompilerPluginRegistrar() {
   override val supportsK2: Boolean = true

   override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
      val dummyExtension = DummyIrGenerationExtension(configuration[CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY])
      IrGenerationExtension.registerExtension(dummyExtension)
   }

   class DummyIrGenerationExtension(private val messageCollector: MessageCollector?) : IrGenerationExtension {
      override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
         messageCollector?.report(CompilerMessageSeverity.LOGGING, "Hello from the dummy compiler plugin")
      }
   }
}
