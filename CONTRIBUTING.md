How to  Contribute
==================

You can contribute in different ways:

* pick up an issue and do the coding, preferably bugs over features
* increase the test coverage of Kotest itself (but only for real logic)
* improve the documentation
* report bugs
* suggest new features and improvements
* spread the word

Before [creating an issue](https://github.com/kotest/kotest/issues/new), please make sure that there is no [existing issue](https://github.com/kotest/kotest/issues) with the same topic.

Branching Model
---------------
The development is done with feature branches such as `feature/xxx`. Any bug fixes for a particular release should target a release branch such as `release/4.6.x`.

Project members push directly to branches in the main repository. External contributors work on the according branch in their own clone and issue a pull request.

You can, of course, suggest any change by a pull request, but we suggest that you create an issue first. Creating an issue helps to avoid waste of your time.

Coding conventions
------------------
We follow the [Kotlin Coding Conventions](http://kotlinlang.org/docs/reference/coding-conventions.html) , except the indentation which is only 3 spaces.

* Minimize mutability
* Choose self-explanatory names


Binary compatibility
------------------
We use [Kotlin Binary Compatibility Validator](https://github.com/kotlin/binary-compatibility-validator/) to keep
changes to the public API under control.

Before submitting a PR which changes the API you need to run `./gradlew apiDump` to generate updates to the api dumps.

