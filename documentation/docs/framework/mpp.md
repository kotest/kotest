---
id: mpp
title: Multiplatform Support
slug: multiplatform-support.html
---


Kotest is multiplatform in some aspects. The test framework can execute tests for both JVM and Javascript projects.
Core assertions are available on all platforms except for linuxArm64.

## JVM

To use Kotest with Kotlin JVM projects, we need to use the `kotest-runner-junit5` module.

Add this to the `jvmTest` configuration in gradle.

Assertions and property testing is available for the JVM.

## JS

To use Kotest with Kotlin Javascript projects, we need to use the `kotest-framework-engine` module.

Add this to the `jsTest` configuration in gradle.

Assertions and property testing is available for Javascript.

## Native

Kotest assertions are available for native platforms but not the test framework.
