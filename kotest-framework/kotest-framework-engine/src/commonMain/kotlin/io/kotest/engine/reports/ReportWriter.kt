package io.kotest.engine.reports

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.writeString

class ReportWriter {

   fun writeXmlFile(baseDir: String, filename: String, contents: String) {
      val path = Path("""tempPath""", filename)
      SystemFileSystem.createDirectories(path)
      println(" >> Test report will be written to $path")
      val sink = SystemFileSystem.sink(path, append = false).buffered()
      sink.writeString(contents)
      sink.close()
   }

}
