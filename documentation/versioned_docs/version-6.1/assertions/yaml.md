---
title: YAML
slug: yaml-matchers.html
sidebar_label: YAML
---

To use these matchers add `testImplementation("io.kotest:kotest-assertions-yaml:<version>")` to your build.

## Basic matchers

| Matcher                | Description                                           | Targets       |
|------------------------|-------------------------------------------------------|:--------------|
| `shouldBeValidYaml`    | verifies that a given string parses to valid YAML     | Multiplatform |
| `shouldNotBeValidYaml` | verifies that a given not string parses to valid YAML | Multiplatform |

## Content-based matching

| Matcher                                                                      | Description                                                                             | Targets       |
|------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|:--------------|
| `shouldEqualYaml`                                                            | Verifies that a String matches a given YAML.                                            | Multiplatform |
| `shouldNotEqualYaml`                                                         | Verifies that a String not matches a given YAML.                             | Multiplatform |
