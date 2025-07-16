package io.kotest.framework.multiplatform


import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemPathSeparator
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.writeString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import kotlin.math.round
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.DurationUnit


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
private val tempPath =
    Path(SystemTemporaryDirectory.let {
        //this hack here can go away once we pass the host's temp dir to the reported
        if (it.toString().split(SystemPathSeparator).last().startsWith("com.apple.CoreSimulator"))
            Path(it.toString().substring(0, it.toString().lastIndexOf("/")))
        else it
        //delete the old reports from the temp dir, before we start a new round of tests
    }, "kotest-report", "test-results", "${target}Test").apply { deleteRecursively() }

private fun deleteRecursivelyInternal(path: Path) {
    if (!SystemFileSystem.exists(path)) return

    if (SystemFileSystem.metadataOrNull(path)?.isDirectory == true) {
        // Delete all children first
        SystemFileSystem.list(path).forEach { child ->
            deleteRecursivelyInternal(child)
        }
    }

    // Delete the file or empty directory
    SystemFileSystem.delete(path)
}

private fun Path.deleteRecursively() = deleteRecursivelyInternal(this)

/* ---------- platform hook ---------- */
private fun writeXmlFile(xml: String, filename: String) {
    val path = Path(tempPath, filename)
    SystemFileSystem.createDirectories(tempPath)
    println(" >> Test report will be written to $path")
    val sink = SystemFileSystem.sink(path, append = false).buffered()
    sink.writeString(xml)
    sink.close()

}

private fun Double.toThreeDecimals(): String {
    val multiplied = this * 1000
    val rounded = round(multiplied) / 1000
    return rounded.toString()
}


private object DoubleSerializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("JunitXMLDouble", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeString(value.toThreeDecimals())
    }

    override fun deserialize(decoder: Decoder): Double {
        return decoder.decodeString().toDouble()
    }

}

@Serializable
@XmlSerialName("testsuite", "", "")
private data class TestSuite(
    @XmlElement(false) val name: String,
    @XmlElement(false) val tests: Int,
    @XmlElement(false) val failures: Int,
    @XmlElement(false) val errors: Int,
    @XmlElement(false) val skipped: Int,
    @XmlElement(false) val timestamp: String,
    @XmlElement(false) @Serializable(with = DoubleSerializer::class) val time: Double,
    @XmlElement val cases: List<TestCaseXml>
)

@Serializable
@XmlSerialName("testcase", "", "")
private data class TestCaseXml(
    @XmlElement(false) val classname: String,
    @XmlElement(false) val name: String,
    @XmlElement(false) @Serializable(with = DoubleSerializer::class) val time: Double,
    val failure: Failure? = null,
    val error: ErrorTag? = null,
    val skipped: Skipped? = null
)

@Serializable
@XmlSerialName("failure", "", "")
private data class Failure(
    @XmlElement(false) val message: String,
    @XmlElement(false) val type: String,
    @XmlValue val stack: String
)

@Serializable
@XmlSerialName("error", "", "")
private data class ErrorTag(
    @XmlElement(false) val message: String,
    @XmlElement(false) val type: String,
    @XmlValue val stack: String
)

@Serializable
@XmlSerialName("skipped", "", "")
private class Skipped

/* ---------- Kotest listener that writes the file at engine stop ---------- */
private class JUnitXmlReporter(
) : AfterTestListener, AfterProjectListener, AfterSpecListener {

    val bySpec = mutableMapOf<String, MutableList<Pair<TestCase, TestResult>>>()

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        val spec = testCase.spec::class.simpleName ?: "UnknownSpec"
        bySpec.getOrPut(spec) { mutableListOf() } += testCase to result
    }

    override suspend fun afterSpec(spec: Spec) {
        val spec = spec::class.simpleName ?: "UnknownSpec"
        val suites = bySpec[spec].let { pairs ->
            var fails = 0;
            var errs = 0;
            var skips = 0
            val cases = pairs!!.map { (tc, res) ->
                val secs = res.duration.toDouble(DurationUnit.MILLISECONDS) / 1_000

                if (res.isFailure) fails++
                if (res.isError) errs++
                if (res.isIgnored) skips++
                val bld = mutableListOf<String>()
                var currentCase: TestCase? = tc
                while (currentCase != null) {
                    bld += currentCase.name.name
                    currentCase = currentCase.parent
                }
                val name = bld.reversed().joinToString(".")
                TestCaseXml(
                    classname = spec,
                    name = "[${target}] $name",
                    time = secs,
                    failure = res.takeIf { it is TestResult.Failure }?.let {
                        (res as TestResult.Failure)
                        Failure(
                            res.errorOrNull?.message ?: "",
                            res.errorOrNull?.let { it::class.simpleName } ?: "",
                            res.errorOrNull?.stackTraceToString() ?: ""
                        )
                    },
                    error = res.takeIf { it is TestResult.Error }?.let {
                        (res as TestResult.Error)
                        ErrorTag(
                            res.errorOrNull?.message ?: "",
                            res.errorOrNull?.let { it::class.simpleName } ?: "",
                            res.errorOrNull?.stackTraceToString() ?: ""
                        )
                    },
                    skipped = res.takeIf { it is TestResult.Ignored }?.let { Skipped() }
                )
            }
            TestSuite(
                spec,
                cases.size,
                fails,
                errs,
                skips,
                Clock.System.now().toString(),
                cases.sumOf { it.time },
                cases
            )
        }

        val xml = XML {
            indentString = "  "
            xmlVersion = XmlVersion.XML10
        }
            .encodeToString(TestSuite.serializer(), (suites))

        @OptIn(ExperimentalStdlibApi::class)
        writeXmlFile(
            xml,
            "TEST-$spec-${Random.nextBytes(4).toHexString()}.xml"
        )
    }

}
