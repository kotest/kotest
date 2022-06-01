# Convention plugins

```mermaid
stateDiagram-v2
  state "kotlin-conventions" as base
  state "kotest-jvm-conventions" as jvm
  state "kotest-js-conventions" as js
  state "kotest-multiplatform-conventions" as multiplatform
  state "kotest-publishing-conventions" as publishing
  state "kotest-multiplatform-library-conventions" as mpplib

  jvm --> base
  js --> base
  multiplatform --> base
  multiplatform --> js
  multiplatform --> jvm
  mpplib --> multiplatform
  mpplib --> publishing
```

## Kotlin conventions

Configures a base project which uses kotlin-multiplatform (with no targets specified).

Here we configure anything that should apply to _every_ project, such as common plugins, repositories which should be
used, etc.

## Kotest JVM conventions
Adds a JVM target and sets basic JVM options

## Kotest JS conventions
Adds JS targets

## Kotest Multiplatform conventions
Adds _all_ multiplatform targets

## Kotest publishing conventions
Adds everything related to signing and publishing the libraries

## Kotest Multiplatform library conventions
A published library which uses all multiplatform targets. Adds signing and all multiplatform targets.
