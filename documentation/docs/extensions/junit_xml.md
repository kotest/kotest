---
id: junit_xml
title: JUnit XML Format Reporter
sidebar_label: JUnit XML
slug: junit_xml.html
---





JUnit includes an XML report generator that it calls
the [legacy xml report](https://junit.org/junit5/docs/5.5.0-RC2/api/org/junit/platform/reporting/legacy/xml/LegacyXmlReportGeneratingListener.html)
. Many tools integrate with this format so it is very useful. However, this report has no concept of nesting tests.
Therefore when used with a nested [test style](../framework/styles.md) in Kotest, it will include parent tests as
orphans.

To solve this, Kotest has it's own implementation of the same format, that is configurable on whether to include parent
tests and/or collapse the names.

To set this up, we need to add the `JunitXmlReporter` to our project
through [project config](../framework/project_config.md).

```kotlin
class MyConfig : AbstractProjectConfig() {
  override fun listeners(): List<Listener> = listOf(
    JunitXmlReporter(
      includeContainers = false,
      useTestPathAsName = true
    )
  )
}
```

Additionally, the reporter needs to know where your build output folder is by setting a system property, so we configure
that in the tests block in gradle.

```kotlin
tasks.named<Test>("test") {
  useJUnitPlatform()
  systemProperty("gradle.build.dir", project.buildDir)
}
```

### Parameters

The reporter has two parameters:

* `includeContainers` when true, all intermediate tests are included in the report as tests in their own right. Defaults
  to false.
* `useTestPathAsName` when true, the full test path will be used as the name. In other words the name will include the
  name of any parent tests as a single string.
