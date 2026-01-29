# Fact-Finding: Test Reports

Gradle generates reports in the `build/test-results` and `build/reports` directories. The `test-results` directory
contains JUnit XML reports, while the `reports` directory contains HTML reports.

## JUnit XML Reports

JUnit XML reports are machine-readable files that provide detailed information about the test execution process. They
contain test results, including pass/fail status, test names, and other metadata. These reports are useful for automated
test analysis and integration with continuous integration (CI) systems.

The general path is `build/test-results/<test task name>/<test class name>.xml`.
An example path is `build/test-results/jvmTest/TEST-com.sksamuel.kotest.xml.MySpec2.xml`.

## HTML Reports

HTML reports are human-readable reports that provide a visual overview of the test results.

## Findings

| KMP  | Franework   | Target              | File Path                                                               | Classname                                           | Test name             | Suites included |
|------|-------------|---------------------|-------------------------------------------------------------------------|-----------------------------------------------------|-----------------------|-----------------|
| true | Kotest      | `jvmTest`           | `build/test-results/jvmTest/TEST-<class fqn>.xml`                       | com.sksamuel.kotest.xml.MySpec2.xml                 | test c[jvm]           | skipped         |
| true | kotest.test | `jvmTest`           | `build/test-results/jvmTest/TEST-<class fqn>.xml`                       | com.sksamuel.kotest.xml.MySpec2.xml                 | testA()[jvm]          | n/a             |
| true | Kotest      | `linuxX64Test`      | `build/test-results/linuxX64Test/TEST-<class fqn>.<context path>.xml`   | com.sksamuel.kotest.xml.MySpec2.context a.context b | test a[linuxX64]      | skipped         |
| true | kotest.test | `linuxX64Test`      | `build/test-results/linuxX64Test/TEST-<class fqn>.<context path>.xml`   | com.sksamuel.kotest.xml.MySpec2.context a.context b | test a[linuxX64]      | n/a             |
| true | Kotest      | `jsBrowserTest`     | `build/test-results/jsBrowserTest/TEST-<class fqn>.<context path>.xml`  | com.sksamuel.kotest.xml.MySpec2.context a.context b | test a[linuxX64]      | skipped         |
| true | kotest.test | `jsBrowserTest`     | `build/test-results/jsBrowserTest/TEST-<class fqn>.<context path>.xml`  | com.sksamuel.kotest.xml.MySpec2.context a.context b | test a[linuxX64]      | n/a             |
| true | Kotest      | `jsNodeTest`        | `build/test-results/jsNodeTest/TEST-<class fqn>.<context path>.xml`     | com.sksamuel.kotest.xml.MySpec2.context a.context b |                       | skipped         |
| true | kotest.test | `jsNodeTest`        | `build/test-results/jsNodeTest/TEST-<class fqn>.<context path>.xml`     | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | n/a             |
| true | kotest.test | `wasmJsNodeTest`    | `build/test-results/wasmJsNodeTest/TEST-<class fqn>.<context path>.xml` | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | n/a             |
| true | kotest.test | `wasmJsNodeTest`    | `build/test-results/wasmJsNodeTest/TEST-<class fqn>.<context path>.xml` | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | n/a             |
| true | kotest.test | `wasmJsD8Test`      | `build/test-results/wasmJsD8Test/TEST-<class fqn>.<context path>.xml`   | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | n/a             |
| true | kotest.test | `wasmJsD8Test`      | `build/test-results/wasmJsD8Test/TEST-<class fqn>.<context path>.xml`   | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | n/a             |
| true | Kotest      | `wasmJsBrowserTest` | none                                                                    | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | skipped         |
| true | kotest.test | `wasmJsBrowserTest` | `build/test-results/wasmJsBrowserTest/TEST-<class fqn>.xml`             | com.sksamuel.kotest.xml.MySpec2.context a.context b | testA[js, node]       | n/a             |
| true | Kotest      | `wasmWasiNodeTest`  | `build/test-results/wasmWasiNodeTest/TEST-<class fqn>.xml`              | com.sksamuel.kotest.xml.KotestSpec                  | testA[wasmWasi, node] | skipped         |
| true | kotest.test | `wasmWasiNodeTest`  | `build/test-results/wasmWasiNodeTest/TEST-<class fqn>.xml`              | com.sksamuel.kotest.xml.KotlinTest                  | testA[wasmWasi, node] | n/a             |

## Notes

### Native

* Kotest is including a test file per container.
* This doesn't affect `kotlin.test` because it's not nested
* Only the containers with tests are being output, not intermediate containers.
* Seems ok with test names up to 1000

### JVM

* JVM is correctly including the test file per class.
* Containers do not appear in the test cases, only leaf tests
* Remember to add the `Kotest` JUnit runner and configure the Test task to use JUnit Platform if using JVM tests
* `Kotest` and `kotlin.test` will both output XML reports to the right place
* todo test with `JUnit Jupiter`.
* Seems ok with test names up to 1000

### JS

* Node and Browser are separate targets so separate directories in the test results directory
* JS is including a test file per container.
* This doesn't affect `kotlin.test` because it's not nested
* Only the containers with tests are being output, not intermediate containers.
* kotlin.test includes the FQN in the file name, Kotest only has the class name.
* Same for the classname in the XML report.
* Seems ok with test names up to 1000

### Wasm JS

* Node, D8, and Browser are separate targets so separate directories in the test results directory
* Kotest is including a test file per container.
* This doesn't affect `kotlin.test` because it's not nested
* Only the containers with tests are being output, not intermediate containers.
* The file has the full FQN in the path for both frameworks.
* WasmJS Browser has no XML reports at all from Kotest

### WasmWasi

* Node, D8, and Browser are separate targets so separate directories in the test results directory
* Kotest is including a test file per container.
* This doesn't affect `kotlin.test` because it's not nested
* Only the containers with tests are being output, not intermediate containers.
* The file has the full FQN in the path for both frameworks.
* Seems ok with test names up to 1000
