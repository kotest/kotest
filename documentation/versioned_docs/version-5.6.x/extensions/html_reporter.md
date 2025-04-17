---
id: html_reporter
title: HTML Reporter
sidebar_label: HTML Reporter
slug: html_reporter.html
---

[![Latest Release](https://img.shields.io/maven-central/v/io.kotest/kotest-extensions-htmlreporter)](https://search.maven.org/artifact/io.kotest/kotest-extensions-htmlreporter)

When using [JUnit XML](junit_xml.md), we can generate XML results from tests that are able to produce output with nested
tests. Unfortunately, Gradle generates its HTML reports with the results it has in-memory, which doesn't support nested
tests, and it doesn't seem to be able to fetch results from a different XML.

To solve this, Kotest has a listener that is able to generate HTML reports based on the XML reports that are generated
by [JUnit XML](junit_xml.md).

:::note
The following module is needed: `io.kotest:kotest-extensions-htmlreporter` in your build. Search maven central for latest version [here](https://search.maven.org/search?q=kotest-extensions-htmlreporter).
:::

In order to use it, we simply need to add it as a listener through [project config](../framework/project_config.md).

```
class ProjectConfig : AbstractProjectConfig() {

   override val specExecutionOrder = SpecExecutionOrder.Annotated

    override fun extensions(): List<Extension> = listOf(
        JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
        ),
        HtmlReporter()
    )
}
```

Additionally, prevent Gradle from generating its own html reports by adding `html.required.set(false)` to the test task.
```
tasks.test {
  useJUnitPlatform()
  reports {
    html.required.set(false)
    junitXml.required.set(false)
  }
  systemProperty("gradle.build.dir", project.buildDir)
}
```

Notice that we also add `JunitXmlReporter`. This will generate the necessary XML reports, used to generate the HTML reports.
There's no additional configuration needed, it should simply start generating HTML reports.

By default, it stores reports in `path/to/buildDir/reports/tests/test` but this can be modified by changing the parameter
`outputDir`.
