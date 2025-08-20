---
id: compiler
title: Compiler Matchers
slug: compiler-matchers.html
sidebar_label: Compiler
---


The ```kotest-assertions-compiler``` extension provides matchers to assert that given kotlin code snippet compiles or not.
This extension is a wrapper over [kotlin-compile-testing](https://github.com/tschuchortdev/kotlin-compile-testing) and provides following matchers

* String.shouldCompile()
* String.shouldNotCompile()
* CodeSnippet.shouldCompile()
* CodeSnippet.shouldNotCompile()
* File.shouldCompile()
* File.shouldNotCompile()

To add the compilation matcher, add the following dependency to your project

```kotlin
testImplementation("io.kotest.extensions:kotest-assertions-compiler:$version")
```

Usage:
```kotlin
class CompilationTest : StringSpec({
    "shouldCompile test" {
        val rawStringCodeSnippet = """
            val aString: String = "A valid assignment"
        """

        val syntaxHighlightedSnippet = codeSnippet("""
            val aString: String = "A valid assignment"
        """)

        rawStringCodeSnippet.shouldCompile()
        syntaxHighlightedSnippet.shouldCompile()
        File("SourceFile.kt").shouldCompile()
    }

    "shouldNotCompile test" {
        val rawStringCodeSnippet = """
            val anInteger: Int = "An invalid assignment"
        """

        val syntaxHighlightedSnippet = codeSnippet("""
            val anInteger: Int = "An invalid assignment"
        """)

        rawStringCodeSnippet.shouldNotCompile()
        syntaxHighlightedSnippet.shouldNotCompile()
        File("SourceFile.kt").shouldNotCompile()

        // check that a compilation error occurred for a specific reason
        rawStringCodeSnippet.shouldNotCompile("expected 'Int', actual 'String'")
        syntaxHighlightedSnippet.shouldNotCompile("expected 'Int', actual 'String'")
        File("SourceFile.kt").shouldNotCompile("expected 'Int', actual 'String'")
    }

    @OptIn(ExperimentalCompilerApi::class)
    "custom assertions on JvmCompilationResult" {
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
    "custom compiler configuration" {
        val compileConfig = CompileConfig {
            compilerPluginRegistrars = listOf(MyCompilerPluginRegistrar())
        }
        val codeSnippet = compileConfig.codeSnippet("""
            @MyAnnotation
            fun hello() {}
        """)

        codeSnippet.shouldCompile()
    }
})
```

During checking of code snippet compilation the classpath of calling process is inherited, which means any dependencies which are available in calling process will also be available while compiling the code snippet.


Matchers that verify if a given piece of Kotlin code compiles or not

| Matcher                              | Description                                        |
|--------------------------------------|----------------------------------------------------|
| `String.shouldCompile()`             | Asserts that the raw string snippet compiles       |
| `CodeSnippet.shouldCompile()`        | Same as above, with syntax highlighting            |
| `File.shouldCompile()`               | Same as above, from a file                         |
| `String.shouldNotCompile(msg?)`      | Asserts it fails, optionally for a specific reason |
| `CodeSnippet.shouldNotCompile(msg?)` | Save as above, with syntax highlighting            |
| `File.shouldNotCompile(msg?)`        | Same as above, from a file                         |
