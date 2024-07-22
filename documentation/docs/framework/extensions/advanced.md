---
id: advanced_extensions
title: Advanced Extensions
slug: advanced-extensions.html
sidebar_label: Advanced Extensions
---

This table lists more advanced extensions that can be used to hook into the Engine itself to:

  * intercept tests, skipping them, and modify test results
  * intercept specs specs skipping them if required
  * post process spec instances after instantiation
  * modify the coroutine context used by specs and tests
  * apply custom instantiation logic
  * filter specs and tests
  * adjust test output


| Extension                     | Description                                                                                                                                |
|-------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| ConstructorExtension          | Provides custom logic to instantiate spec classes. An example is the Spring extension constructor extension which autowire's spring beans. |
| TestCaseExtension             | Intercepts calls to a test, can skip a test, override the test result, and modify the coroutine context.                                   |
| SpecExtension                 | Intercepts calls to a spec, can skip a spec, and modify the coroutine context.                                                             |
| SpecRefExtension              | Intercepts calls to a spec before it is instantiated. Can skip instantiation.                                                              |
| DisplayNameFormatterExtension | Can customize the display names of tests used in test output.                                                                              |
| EnabledExtension              | Can provide custom logic to determine if a test is enabled or disabled.                                                                    |
| ProjectExtension              | Intercepts calls to the test engine before a project starts.                                                                               |
| SpecExecutionOrderExtension   | Can sort specs before execution begins to provide a custom spec execution order.                                                           |
| TagExtension                  | Can provide active tags from arbitrary sources.                                                                                            |
| InstantiationErrorListener    | Is notified when a spec fails to be instantiated due to some exception.                                                                    |
| InstantiationListener         | Is notified when a spec is successfully instantiated.                                                                                      |
| PostInstantiationExtension    | Intercepts specs when they are instantiated, can replace the spec instance and modify coroutine context.                                   |
| IgnoredSpecListener           | Is notified when a spec is skipped.                                                                                                        |
| SpecFilter                    | Can provide custom logic to skip a spec.                                                                                                   |
| TestFilter                    | Can provide custom logic to skip a test.                                                                                                   |
| DisabledTestListener          | Is notified when a test is disabled.                                                                                                       |

