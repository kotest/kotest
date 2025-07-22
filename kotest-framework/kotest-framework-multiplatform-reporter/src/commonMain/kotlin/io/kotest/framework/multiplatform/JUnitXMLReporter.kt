package io.kotest.framework.multiplatform

internal expect val target: String

//TODO automagically call extensions(JUnitXmlReporter()) whenever this dependency is present.
// there is no compileOnly-dependency on KMP so this is not straight forward without SPI/EgagerInitialization

//TODO: pass the **HOSTS**'s temp dir and replace this MacGuyver-ish hack
// stick with the temp dir as this is guaranteed to be writable even from within whatever container/runner/… runs the tests

// TODO: add something like the following to the kotest gradle plugin:
/*
val $kotestReportDir = TODO(Some temp dir)
//TODO work magic to pass teh temp dir to the KMP report generator
internal fun Project.registerKotestCopyTask() {
    afterEvaluate {
        //cannot filter for test instance, since kmp tests do not inherit Test
        tasks.matching { it.name.endsWith("Test") }.forEach {
            it.doLast {
                runCatching {
                    logger.lifecycle("  >> Copying tests from $kotestReportDir")
                    val source = File(kotestReportDir.toString())
                    source.copyRecursively(layout.buildDirectory.asFile.get(), overwrite = true)
                }.getOrElse {
                    Logger.warn(" >> Copying tests from $kotestReportDir failed: ${it.message}")
                }
            }
        }
    }
}
 */
//private val tempPath =
//    Path(SystemTemporaryDirectory.let {
//        //this hack here can go away once we pass the host's temp dir to the reported
//        if (it.toString().split(SystemPathSeparator).last().startsWith("com.apple.CoreSimulator"))
//            Path(it.toString().substring(0, it.toString().lastIndexOf("/")))
//        else it
//        //delete the old reports from the temp dir, before we start a new round of tests
//    }, "kotest-report", "test-results", "${target}Test").apply { deleteRecursively() }
//
//private fun deleteRecursivelyInternal(path: Path) {
//    if (!SystemFileSystem.exists(path)) return
//
//    if (SystemFileSystem.metadataOrNull(path)?.isDirectory == true) {
//        // Delete all children first
//        SystemFileSystem.list(path).forEach { child ->
//            deleteRecursivelyInternal(child)
//        }
//    }
//
//    // Delete the file or empty directory
//    SystemFileSystem.delete(path)
//}
//
//private fun Path.deleteRecursively() = deleteRecursivelyInternal(this)
//
///* ---------- platform hook ---------- */
//private fun writeXmlFile(xml: String, filename: String) {
//    val path = Path(tempPath, filename)
//    SystemFileSystem.createDirectories(tempPath)
//    println(" >> Test report will be written to $path")
//    val sink = SystemFileSystem.sink(path, append = false).buffered()
//    sink.writeString(xml)
//    sink.close()
//}
//






/* ---------- Kotest listener that writes the file at engine stop ---------- */
//private class JUnitXmlReporter(
//) : AfterTestListener, AfterProjectListener, AfterSpecListener {
//
//    val bySpec = mutableMapOf<String, MutableList<Pair<TestCase, TestResult>>>()
//
//    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
//        val spec = testCase.spec::class.simpleName ?: "UnknownSpec"
//        bySpec.getOrPut(spec) { mutableListOf() } += testCase to result
//    }
//
//    override suspend fun afterSpec(spec: Spec) {
//        val spec = spec::class.simpleName ?: "UnknownSpec"
//        val suites = bySpec[spec].let { pairs ->
//            var fails = 0;
//            var errs = 0;
//            var skips = 0
//            val cases = pairs!!.map { (tc, res) ->
//                val secs = res.duration.toDouble(DurationUnit.MILLISECONDS) / 1_000
//
//                if (res.isFailure) fails++
//                if (res.isError) errs++
//                if (res.isIgnored) skips++
//                val bld = mutableListOf<String>()
//                var currentCase: TestCase? = tc
//                while (currentCase != null) {
//                    bld += currentCase.name.name
//                    currentCase = currentCase.parent
//                }
//                val name = bld.reversed().joinToString(".")
//                TestCaseElement(
//                    classname = spec,
//                    name = "[${target}] $name",
//                    time = secs,
//                    failure = res.takeIf { it is TestResult.Failure }?.let {
//                        (res as TestResult.Failure)
//                        FailureElement(
//                            res.errorOrNull?.message ?: "",
//                            res.errorOrNull?.let { it::class.simpleName } ?: "",
//                            res.errorOrNull?.stackTraceToString() ?: ""
//                        )
//                    },
//                    error = res.takeIf { it is TestResult.Error }?.let {
//                        (res as TestResult.Error)
//                        ErrorElement(
//                            res.errorOrNull?.message ?: "",
//                            res.errorOrNull?.let { it::class.simpleName } ?: "",
//                            res.errorOrNull?.stackTraceToString() ?: ""
//                        )
//                    },
//                    skipped = res.takeIf { it is TestResult.Ignored }?.let { SkippedElement(it.reasonOrNull) }
//                )
//            }
//            TestSuite(
//                spec,
//                cases.size,
//                fails,
//                errs,
//                skips,
//               "2021-04-02T15:48:23", // Clock.System.now().toString(),
//                cases.sumOf { it.time },
//                cases
//            )
//        }
//
//        val xml = XML {
//            indentString = "  "
//            xmlVersion = XmlVersion.XML10
//        }
//            .encodeToString(TestSuite.serializer(), (suites))
//
//        @OptIn(ExperimentalStdlibApi::class)
//        writeXmlFile(
//            xml,
//            "TEST-$spec-${Random.nextBytes(4).toHexString()}.xml"
//        )
//    }
//
//}
