package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Path
import java.nio.file.Paths

class PathPrintTest : FunSpec({
   test("path print") {
      PathPrint.print(Paths.get("foo/bar.txt"), 0) shouldBe Printed(value = "foo/bar.txt", type = Path::class)
      PathPrint.print(Paths.get("/tmp/foo"), 0) shouldBe Printed(value = "/tmp/foo", type = Path::class)
   }
})
