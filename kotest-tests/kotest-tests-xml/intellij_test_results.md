# Fact-Finding: IntelliJ Test Results Output

There are three ways tests are reported to Intellij.

* Gradle's JUnit platform integration
* TeamCity service messages
* JavaScript integration via a FrameworkAdapter.

## Notes

### Native

* This flow goes via TeamCity service messages.
* IntelliJ is not displaying intermediate containers, only containers that contain a test.
* Common prefixes are stripped.
* Package names are stripped from the node name if it looks like a class.
* Periods in test names break the output.

### JVM

* This flow goes via Gradle's JUnit platform integration
* IntelliJ is displaying intermediate containers correctly.

### JS

* Node test goes via the Javascript integrations eg mocha / karma
* Root tests are displayed under the class node.
* Contexts are displayed as top-level nodes with tests nested inside.

### WasmWasi

* The same behavior as Native-intermediate containers disappears.
* The package name is stripped from the node name as intellij is assuming a class FQN.
* Common prefixes are stripped.
