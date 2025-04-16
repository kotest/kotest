"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[22276],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>m});var r=n(67294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var l=r.createContext({}),p=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=p(e.components);return r.createElement(l.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},u=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,l=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),u=p(n),m=a,f=u["".concat(l,".").concat(m)]||u[m]||d[m]||o;return n?r.createElement(f,i(i({ref:t},c),{},{components:n})):r.createElement(f,i({ref:t},c))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,i=new Array(o);i[0]=u;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:a,i[1]=s;for(var p=2;p<o;p++)i[p]=n[p];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}u.displayName="MDXCreateElement"},74396:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>d,frontMatter:()=>o,metadata:()=>s,toc:()=>p});var r=n(87462),a=(n(67294),n(3905));const o={},i=void 0,s={unversionedId:"framework/datatesting/data_driven_testing_4.2.0",id:"version-5.8.x/framework/datatesting/data_driven_testing_4.2.0",title:"data_driven_testing_4.2.0",description:"To test your code with different parameter combinations, you can use a table of values as input for your test",source:"@site/versioned_docs/version-5.8.x/framework/datatesting/data_driven_testing_4.2.0.md",sourceDirName:"framework/datatesting",slug:"/framework/datatesting/data_driven_testing_4.2.0",permalink:"/docs/5.8.x/framework/datatesting/data_driven_testing_4.2.0",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.8.x/framework/datatesting/data_driven_testing_4.2.0.md",tags:[],version:"5.8.x",frontMatter:{}},l={},p=[],c={toc:p};function d(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"To test your code with different parameter combinations, you can use a table of values as input for your test\ncases. This is called ",(0,a.kt)("em",{parentName:"p"},"data driven testing")," also known as ",(0,a.kt)("em",{parentName:"p"},"table driven testing"),"."),(0,a.kt)("p",null,"Invoke the ",(0,a.kt)("inlineCode",{parentName:"p"},"forAll")," or ",(0,a.kt)("inlineCode",{parentName:"p"},"forNone")," function, passing in one or more ",(0,a.kt)("inlineCode",{parentName:"p"},"row")," objects, where each row object contains\nthe values to be used be a single invocation of the test. After the ",(0,a.kt)("inlineCode",{parentName:"p"},"forAll")," or ",(0,a.kt)("inlineCode",{parentName:"p"},"forNone")," function, setup your\nactual test function to accept the values of each row as parameters."),(0,a.kt)("p",null,"The row object accepts any set of types, and the type checker will ensure your types are consistent with the parameter\ntypes in the test function."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'"square roots" {\n  forAll(\n      row(2, 4),\n      row(3, 9),\n      row(4, 16),\n      row(5, 25)\n  ) { root, square ->\n    root * root shouldBe square\n  }\n}\n')),(0,a.kt)("p",null,"In the above example, the ",(0,a.kt)("inlineCode",{parentName:"p"},"root")," and ",(0,a.kt)("inlineCode",{parentName:"p"},"square")," parameters are automatically inferred to be integers."),(0,a.kt)("p",null,"If there is an error for any particular input row, then the test will fail and KotlinTest will automatically\nmatch up each input to the corresponding parameter names. For example, if we change the previous example to include the row ",(0,a.kt)("inlineCode",{parentName:"p"},"row(5,55)"),"\nthen the test will be marked as a failure with the following error message."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre"},"Test failed for (root, 5), (square, 55) with error expected: 55 but was: 25\n")),(0,a.kt)("p",null,"Table testing can be used within any spec. Here is an example using ",(0,a.kt)("inlineCode",{parentName:"p"},"StringSpec"),"."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class StringSpecExample : StringSpec({\n  "string concat" {\n    forAll(\n      row("a", "b", "c", "abc"),\n      row("hel", "lo wo", "rld", "hello world"),\n      row("", "z", "", "z")\n    ) { a, b, c, d ->\n      a + b + c shouldBe d\n    }\n  }\n})\n')),(0,a.kt)("p",null,"It may be desirable to have each row of data parameters as an individual test. To generating such individual tests follow a similar pattern for each spec style. An example in the ",(0,a.kt)("inlineCode",{parentName:"p"},"FreeSpec")," is below."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class IntegerMathSpec : FreeSpec({\n    "Addition" - {\n        listOf(\n            row("1 + 0", 1) { 1 + 0 },\n            row("1 + 1", 2) { 1 + 1 }\n        ).map { (description: String, expected: Int, math: () -> Int) ->\n            description {\n                math() shouldBe expected\n            }\n        }\n    }\n    // ...\n    "Complex Math" - {\n        listOf(\n            row("8/2(2+2)", 16) { 8 / 2 * (2 + 2) },\n            row("5/5 + 1*1 + 3-2", 3) { 5 / 5 + 1 * 1 + 3 - 2 }\n        ).map { (description: String, expected: Int, math: () -> Int) ->\n            description {\n                math() shouldBe expected\n            }\n        }\n    }\n})\n')),(0,a.kt)("p",null,"Produces 4 tests and 2 parent descriptions:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-txt"},"IntegerMathSpec\n  \u2713 Addition\n    \u2713 1 + 0\n    \u2713 1 + 1\n  \u2713 Complex Math\n    \u2713 8/2(2+2)\n    \u2713 5/5 + 1*1 + 3-2\n")))}d.isMDXComponent=!0}}]);