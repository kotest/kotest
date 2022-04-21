---
id: project_timeout
title: Project Timeout
slug: project-timeouts.html
sidebar_label: Project Timeout
---


Kotest supports a project level timeout.
This timeout applies to all tests in a module and includes the setup/teardown time of every spec/test in the module.

To enable this, we can use [ProjectConfig](../project_config.md).

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val projectTimeout: Duration = 10.minutes
}
```

In the above example, we have specified a project timeout of 10 minutes. All specs and tests must complete within
that 10 minute period or the build will fail.
