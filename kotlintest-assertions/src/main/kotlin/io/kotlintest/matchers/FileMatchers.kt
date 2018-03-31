package io.kotlintest.matchers

import io.kotlintest.Matcher
import java.io.File

@Deprecated("use io.kotlintest.matchers.file.exist()", ReplaceWith("io.kotlintest.matchers.file.exist()", "io"))
fun exist() = io.kotlintest.matchers.file.exist()

@Deprecated("use io.kotlintest.matchers.file.haveExtension(exts)", ReplaceWith("io.kotlintest.matchers.file.haveExtension(*exts)", "io"))
fun haveExtension(vararg exts: String) = io.kotlintest.matchers.file.haveExtension(*exts)

@Deprecated("use io.kotlintest.matchers.file.aDirectory()", ReplaceWith("io.kotlintest.matchers.file.aDirectory()", "io"))
fun aDirectory(): Matcher<File> = io.kotlintest.matchers.file.aDirectory()

@Deprecated("use io.kotlintest.matchers.file.aFile()", ReplaceWith("io.kotlintest.matchers.file.aFile()", "io"))
fun aFile(): Matcher<File> = io.kotlintest.matchers.file.aFile()