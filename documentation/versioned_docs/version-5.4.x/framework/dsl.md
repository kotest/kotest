---
id: dsl
title: Kotest DSL
slug: dsl.html
sidebar_label: Kotest DSL
---


This page discusses in detail the Kotest DSL that is used to build tests. You do not need to read this page
to effectively use Kotest, but it may be of interest to users who are implementing extensions or
raising PRs on Kotest itself or anyone who is just curious how things work under the hood.

### Tests

In Kotest a test is essentially just a function `TestContext -> Unit`. This function will contain assertions
(_matchers_ in Kotest nomenclature) which will throw an exception if the assertion fails. These exceptions are
then intercepted by the framework and used to mark a test as _failed_ or _errored_ (depending on the exception class).

### Spec

The basic unit of currency in Kotest is the spec. A spec is the top most container of tests and is essentially
just a class that extends one of the spec styles (FunSpec, DescribeSpec and so on).

Each spec contains tests which are referred to as _root tests_ (rooted in reference to the spec). These root
tests are registered


