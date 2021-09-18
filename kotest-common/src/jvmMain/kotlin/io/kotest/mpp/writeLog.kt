package io.kotest.mpp

import java.io.FileWriter

val file: FileWriter by lazy { FileWriter("/home/sam/development/workspace/kotest/kotest/kotest.log",false) }

actual fun writeLog(t: Throwable?, f: () -> String) {
   file.write(f())
   file.write("\n")
   file.flush()
}
