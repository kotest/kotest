Allure
==========

Allure is an open-source framework designed for detailed and interactive test reports.
It works by generating report files which are then used to create the final HTML report.
You can think of it as like the traditional junit-style xml report but far more advanced.

Allure has report generators for most test frameworks, and Kotest is no different. In order
to activate allure for kotest, you simply need to add the maven module `kotest-allure` to your build.

_Note: In previous versions of Kotest/Kotlintest you needed to add the `AllureTestListener` to your test classes
manually, but this is no longer required._

With the module added to your build, execute your tests and report files will be written to the default location.
Finally, you use the allure binary to [generate  the report](https://docs.qameta.io/allure/#_report_generation).
