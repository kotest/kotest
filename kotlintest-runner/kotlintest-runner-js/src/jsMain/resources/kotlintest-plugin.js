var kotlin_test = require('./build/node_modules/kotlin-test.js');

kotlin_test.setAdapter({
    suite: function (name, ignored, fn) {
        if (name == "FunSpec" ||
            name == "BehaviorSpec" ||
            name == "StringSpec")
            return
        describe(name, fn)
    },
    test: function (name, ignored, fn) {
        if (name == "kotlintest_generate_tests") {
           fn()
        } else {
           it(name, fn)
        }
    }
});
