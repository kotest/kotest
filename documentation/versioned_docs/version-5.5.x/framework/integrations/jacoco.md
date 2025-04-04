---
id: jacoco
title: Jacoco
sidebar_label: Jacoco
slug: jacoco.html
---



Kotest integrates with [Jacoco](https://www.eclemma.org/jacoco/) for code coverage in the standard gradle way.
You can read gradle installation instructions [here](https://docs.gradle.org/current/userguide/jacoco_plugin.html).

1. In gradle, add jacoco to your plugins.

```kotlin
plugins {
   ...
   jacoco
   ...
}
```

2. Configure jacoco

```kotlin
jacoco {
    toolVersion = "0.8.7"
    reportsDirectory = layout.buildDirectory.dir('customJacocoReportDir') // optional
}
```

3. Add the jacoco XML report task.

```kotlin
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.isEnabled = true
    }
}
```

4. Change tests task to depend on jacoco.

```kotlin
tasks.test {
  ...
  finalizedBy(tasks.jacocoTestReport)
}
```

Now when you run `test`, the Jacoco report files should be generated in `$buildDir/reports/jacoco`. 

:::note
You may need to apply the jacoco plugin to each submodule if you have a multi module project.
:::

