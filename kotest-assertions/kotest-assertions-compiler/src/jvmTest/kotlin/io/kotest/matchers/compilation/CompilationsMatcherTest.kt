package io.kotest.matchers.compilation

import com.tschuchort.compiletesting.KotlinCompilation.ExitCode
import io.kotest.assertions.throwables.shouldThrowExactly
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
   private lateinit var file: File

   override suspend fun beforeTest(testCase: TestCase) {
      file = File("codeSnippet.kt")
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      file.delete()
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
         file.writeText(rawStringCodeSnippet)
         file.shouldNotCompile()
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
         file.writeText(rawStringCodeSnippet)
         file.shouldNotCompile(expectedErrorMsg)
      }

      "shouldNotCompile() should throw AssertionError if the expected message is incorrect" {
         val rawStringCodeSnippet = """
             val aString: String = 123
          """

         shouldThrowExactly<AssertionError> {
            rawStringCodeSnippet.shouldNotCompile("Initializer type mismatch: expected 'Int', actual 'String'")
         }
         file.writeText(rawStringCodeSnippet)
         shouldThrowExactly<AssertionError> {
            file.shouldNotCompile("Initializer type mismatch: expected 'Int', actual 'String'")
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
         file.writeText(codeSnippet)
         file.shouldNotCompile()
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
         file.writeText(codeSnippet)
         file.shouldCompile()
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
         file.writeText(codeSnippet)
         file.shouldNotCompile()
      }

      "a code snippet with an inline function should compile" {
         codeSnippet("""
            val aString = io.kotest.matchers.compilation.inlinedFunc(123)
         """).shouldCompile()
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
