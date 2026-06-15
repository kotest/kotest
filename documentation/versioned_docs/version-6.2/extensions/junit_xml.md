---
id: junit_xml
title: JUnit XML Format Reporter
sidebar_label: JUnit XML
slug: junit_xml.html
---


[<img src="https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-junitxml.svg?label=latest%20release"/>](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-junitxml)
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fio%2Fkotest%2Fkotest-extensions-junitxml%2Fmaven-metadata.xml"/>](https://central.sonatype.com/repository/maven-snapshots/io/kotest/kotest-extensions-junitxml/maven-metadata.xml)


JUnit includes an XML report generator that it calls
the [legacy xml report](https://junit.org/junit5/docs/5.5.0-RC2/api/org/junit/platform/reporting/legacy/xml/LegacyXmlReportGeneratingListener.html)
. Many tools integrate with this format so it is very useful. However, this report has no concept of nesting tests.
Therefore when used with a nested [test style](../framework/styles.md) in Kotest, it will include parent tests as
orphans.

To solve this, Kotest has it's own implementation of the same format, that is configurable on whether to include parent
tests and/or collapse the names.

:::note
The following module is needed: `io.kotest:kotest-extensions-junitxml` in your build. Search maven central for latest version [here](https://central.sonatype.com/artifact/io.kotest/kotest-extensions-junitxml).
:::

To configure in your project, you need to add the `JunitXmlReporter` using [project config](../framework/project_config.md).

```kotlin
class MyConfig : AbstractProjectConfig() {
  override val extensions: List<Extension> = listOf(
    JunitXmlReporter(
      includeContainers = false, // don't write out status for all tests
      useTestPathAsName = true, // use the full test path (ie, includes parent test names)
      outputDir = "../target/junit-xml" // include to set output dir for maven
    )
  )
}
```

Additionally, the reporter needs to know where your build output folder is by setting a system property.
Gradle also needs to know that it should not generate JUnit XML reports by itself.
We configure that in the tests block in gradle.

```kotlin
tasks.named<Test>("test") {
  useJUnitPlatform()
  reports {
    junitXml.required.set(false)
  }
  systemProperty("gradle.build.dir", project.buildDir)
}
```

### Parameters

The reporter has three parameters:

* `includeContainers` when true, all intermediate tests are included in the report as tests in their own right. Defaults
  to false.
* `useTestPathAsName` when true, the full test path will be used as the name. In other words the name will include the
  name of any parent tests as a single string.
* `outputDir` when set, the reports are generated in that folder, default value is: test-results/test
