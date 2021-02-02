package io.kotest.core.plan

/**
 *  A test plan describes the tests for a project.
 *
 *  It consists of a tree describing tests, where each node in the tree is
 *  an instance of [Descriptor], and configuration.
 *
 */
data class TestPlan(val root: Descriptor, val config: Map<String, String>)
