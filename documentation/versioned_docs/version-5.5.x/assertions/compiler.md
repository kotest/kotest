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
* File.shouldCompile()
* File.shouldNotCompile()

To add the compilation matcher, add the following dependency to your project

```groovy
testImplementation("io.kotest.extensions:kotest-assertions-compiler:${version}")
```

Usage:
```kotlin
    class CompilationTest: StringSpec() {
        init {
            "shouldCompile test" {
                val codeSnippet = """ val aString: String = "A valid assignment" """.trimMargin()

                codeSnippet.shouldCompile()
                File("SourceFile.kt").shouldCompile()
            }

            "shouldNotCompile test" {
                val codeSnippet = """ val aInteger: Int = "A invalid assignment" """.trimMargin()

                codeSnippet.shouldNotCompile()
                File("SourceFile.kt").shouldNotCompile()
            }
        }
    }
```

During checking of code snippet compilation the classpath of calling process is inherited, which means any dependencies which are available in calling process will also be available while compiling the code snippet.



Matchers that verify if a given piece of Kotlin code compiles or not

| Matcher | Description    |
| ---------- | --- |
| `string.shouldCompile()` | Asserts that the string is a valid Kotlin code. |
| `file.shouldCompile()` | Asserts that the file contains valid Kotlin code. |
