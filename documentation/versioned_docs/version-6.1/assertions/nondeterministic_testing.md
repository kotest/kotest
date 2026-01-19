---
id: nondeterministic
title: Non-deterministic Testing
slug: non-deterministic-testing.html
---


Sometimes you have to work with code that is non-deterministic in nature. This is not the ideal scenario for writing
tests, but for the times when it is required, Kotest provides several functions that help writing tests where the happy path can take a variable amount of time to
pass successfully.


| Function                      | Role                                                                                                                                                                        |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Eventually](eventually.md)   | Used to ensure that a test will _eventually_ pass within a specified time duration. The test is repeatedly executed until the test passes or the duration expires.          |
| [Continually](continually.md) | Used to ensure that a test _continually_ passes for a period of time. Will repeatedly execute a test until the duration has expired or the test fails.                      |
| [Until](until.md)             | Used to ensure that a predicate will eventually hold true within a specified time duration. The predicate is repeatedly executed until true or the duration expires.        |
| [Retry](retry.md)             | Used to ensure that a test willi eventually pass within a given number of retries. The test is repeatedly executed until the test passes or the iteration count is reached. |
