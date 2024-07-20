---
id: discovery_extension
title: Discovery Extensions
slug: discovery-extensions.html
---


_Advanced Feature_

Another type of extension that can be used inside `ProjectConfig` is the `DiscoveryExtension`. This extension is designed
 to allow customisation of the way spec classes are discovered and instantiated. There are two functions of interest that
 can be overridden.

The first is `afterScan` which accepts a list of Spec classes that were discovered by Kotest during the _discovery_ phase
 of the test engine. This function then returns a list of the classes that should actually be instantiated and executed. By
 overriding this function, you are able to filter which classes are used, or even add in extra classes not originally discovered.

The second function is `instantiate` which accepts a `KClass<Spec>` and then attempts to create an instance of this Spec class in order
 to then run the test cases defined in it. By default, Spec classes are assumed to have a zero-arg primary constructor.
 If you wish to use non-zero arg primary constructors this function can be implemented with logic on how to instantiate a test class.

An implementation can choose to create a new instance, or it can choose to return null if it wishes to pass control to the next
extension (or if no more extensions, then back to the Test Engine itself).

By overriding this function, extensions are able to customize the way classes are created, to support things like constructors
with parameters, or classes that require special initialization logic. This type of extension is how the Spring Constructor Injection
add-on works for example.


