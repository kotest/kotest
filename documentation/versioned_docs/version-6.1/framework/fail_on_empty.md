---
id: fail_on_empty
title: Fail On Empty Test Suite
slug: fail-on-empty-test-suite.html
sidebar_label: Fail On Empty Test Suite
---

To ensure that a project always executes at least one test,
you can enable `failOnEmptyTestSuite` in [project config](./project_config.md).

If this is set to true and a module has no tests executed then the build will fail.

```kotlin
class ProjectConfig : AbstractProjectConfig() {
  override val failOnEmptyTestSuite = true
}
```


:::tip
A module is considered empty if no tests are _executed_ regardless of whether tests are defined. This is useful to
catch scenarios were tests are being filtered out erroneously, such as by environment specific settings.
:::
