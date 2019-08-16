var kotlin_test = require('./build/node_modules/kotlin-test.js');

kotlin_test.setAdapter({
    suite: function (name, ignored, fn) {
        if (name != "FunSpec") describe(name, fn)
    },
    test: function (name, ignored, fn) {
        if (name == "kotlintestSpecSetup") {
           fn()
        } else {
           it(name, fn)
        }
    }
});