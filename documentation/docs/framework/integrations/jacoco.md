---
id: jacoco
title: Jacoco
sidebar_label: Jacoco
slug: jacoco.html
---



Kotest integrates with [Jacoco](https://www.eclemma.org/jacoco/) for code coverage in the normal way you'd expect.

1. In gradle, add jacoco to your plugins.

```kotlin
plugins {
   ...
   jacoco
   ...
}
```

2. Add the jacoco XML report task.

```kotlin
tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}
```
