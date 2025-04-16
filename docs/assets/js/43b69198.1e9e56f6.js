"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[3102],{3905:(e,t,a)=>{a.d(t,{Zo:()=>d,kt:()=>m});var n=a(67294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function i(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function o(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?i(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):i(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function s(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},i=Object.keys(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)a=i[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var l=n.createContext({}),p=function(e){var t=n.useContext(l),a=t;return e&&(a="function"==typeof e?e(t):o(o({},t),e)),a},d=function(e){var t=p(e.components);return n.createElement(l.Provider,{value:t},e.children)},c={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,i=e.originalType,l=e.parentName,d=s(e,["components","mdxType","originalType","parentName"]),u=p(a),m=r,h=u["".concat(l,".").concat(m)]||u[m]||c[m]||i;return a?n.createElement(h,o(o({ref:t},d),{},{components:a})):n.createElement(h,o({ref:t},d))}));function m(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=a.length,o=new Array(i);o[0]=u;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:r,o[1]=s;for(var p=2;p<i;p++)o[p]=a[p];return n.createElement.apply(null,o)}return n.createElement.apply(null,a)}u.displayName="MDXCreateElement"},30774:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>c,frontMatter:()=>i,metadata:()=>s,toc:()=>p});var n=a(87462),r=(a(67294),a(3905));const i={id:"introduction",title:"Introduction",slug:"data-driven-testing.html"},o=void 0,s={unversionedId:"framework/datatesting/introduction",id:"version-5.4.x/framework/datatesting/introduction",title:"Introduction",description:"Before data-driven-testing can be used, you need to add the module kotest-framework-datatest to your build.",source:"@site/versioned_docs/version-5.4.x/framework/datatesting/data_driven_testing.md",sourceDirName:"framework/datatesting",slug:"/framework/datatesting/data-driven-testing.html",permalink:"/docs/5.4.x/framework/datatesting/data-driven-testing.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.4.x/framework/datatesting/data_driven_testing.md",tags:[],version:"5.4.x",frontMatter:{id:"introduction",title:"Introduction",slug:"data-driven-testing.html"},sidebar:"framework",previous:{title:"Exceptions",permalink:"/docs/5.4.x/framework/exceptions.html"},next:{title:"Data Test Names",permalink:"/docs/5.4.x/framework/datatesting/custom-test-names.html"}},l={},p=[{value:"Getting Started",id:"getting-started",level:2}],d={toc:p};function c(e){let{components:t,...i}=e;return(0,r.kt)("wrapper",(0,n.Z)({},d,i,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("admonition",{title:"Required Module",type:"tip"},(0,r.kt)("p",{parentName:"admonition"},"Before data-driven-testing can be used, you need to add the module ",(0,r.kt)("inlineCode",{parentName:"p"},"kotest-framework-datatest")," to your build.")),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},"This section covers the new and improved data driven testing support that was released with Kotest 4.6.0.\nTo view the documentation for the previous data test support, ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.4.x/framework/datatesting/data_driven_testing_4.2.0"},"click here"))),(0,r.kt)("p",null,"When writing tests that are logic based, one or two specific code paths that work through particular scenarios make\nsense. Other times we have tests that are more example based, and it would be helpful to test many combinations of\nparameters."),(0,r.kt)("p",null,"In these situations, ",(0,r.kt)("strong",{parentName:"p"},"data driven testing")," (also called table driven testing) is an easy technique to avoid tedious\nboilerplate."),(0,r.kt)("p",null,"Kotest has first class support for data driven testing built into the framework.\nThis means Kotest will automatically generate test case entries, based on input values provided by you."),(0,r.kt)("h2",{id:"getting-started"},"Getting Started"),(0,r.kt)("p",null,"Let's consider writing tests for a ",(0,r.kt)("a",{parentName:"p",href:"https://en.wikipedia.org/wiki/Pythagorean_triple"},"pythagorean triple")," function that\nreturns true if the input values are valid triples (",(0,r.kt)("em",{parentName:"p"},"a squared + b squared = c squared"),")."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"fun isPythagTriple(a: Int, b: Int, c: Int): Boolean = a * a + b * b == c * c\n")),(0,r.kt)("p",null,"Since we need more than one element per row (we need 3), we start by defining a data class that will hold a single ",(0,r.kt)("em",{parentName:"p"},"\nrow")," of values (in our case, the two inputs, and the expected result)."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"data class PythagTriple(val a: Int, val b: Int, val c: Int)\n")),(0,r.kt)("p",null,"We will create tests by using instances of this data class, passing them into the ",(0,r.kt)("inlineCode",{parentName:"p"},"withData")," function, which also\naccepts a lambda that performs the test logic for that given ",(0,r.kt)("em",{parentName:"p"},"row"),"."),(0,r.kt)("p",null,"For example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTests : FunSpec({\n  context("Pythag triples tests") {\n    withData(\n      PythagTriple(3, 4, 5),\n      PythagTriple(6, 8, 10),\n      PythagTriple(8, 15, 17),\n      PythagTriple(7, 24, 25)\n    ) { (a, b, c) ->\n      isPythagTriple(a, b, c) shouldBe true\n    }\n  }\n})\n')),(0,r.kt)("p",null,"Notice that because we are using data classes, the input row can be destructured into the member properties.\nWhen this is executed, we will have 4 test cases in our input, one for each input row."),(0,r.kt)("p",null,"Kotest will automatically generate a test case for each input row, as if you had manually written a separate test case\nfor each."),(0,r.kt)("p",null,(0,r.kt)("img",{alt:"data test example output",src:a(86714).Z,width:"528",height:"216"})),(0,r.kt)("p",null,"The test names are generated from the data classes themselves but can be ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.4.x/framework/datatesting/custom-test-names.html"},"customized"),"."),(0,r.kt)("p",null,"If there is an error for any particular input row, then the test will fail and Kotest will output the values that\nfailed. For example, if we change the previous example to include the row ",(0,r.kt)("inlineCode",{parentName:"p"},"PythagTriple(5, 4, 3)"),"\nthen that test will be marked as a failure."),(0,r.kt)("p",null,(0,r.kt)("img",{alt:"data test example output",src:a(32028).Z,width:"529",height:"249"})),(0,r.kt)("p",null,"The error message will contain the error and the input row details:"),(0,r.kt)("p",null,(0,r.kt)("inlineCode",{parentName:"p"},"Test failed for (a, 5), (b, 4), (c, 3) expected:<9> but was:<41>")),(0,r.kt)("p",null,"In that previous example, we wrapped the ",(0,r.kt)("inlineCode",{parentName:"p"},"withData")," call in a parent test, so we have more context when the test results appear.\nThe syntax varies depending on the ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.4.x/framework/testing-styles.html"},"spec style")," used - here we used ",(0,r.kt)("em",{parentName:"p"},"fun spec")," which uses context blocks for containers.\nIn fact, data tests can be nested inside any number of containers."),(0,r.kt)("p",null,"But this is optional, you can define data tests at the root level as well."),(0,r.kt)("p",null,"For example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MyTests : FunSpec({\n  withData(\n    PythagTriple(3, 4, 5),\n    PythagTriple(6, 8, 10),\n    PythagTriple(8, 15, 17),\n    PythagTriple(7, 24, 25)\n  ) { (a, b, c) ->\n    isPythagTriple(a, b, c) shouldBe true\n  }\n})\n")),(0,r.kt)("admonition",{type:"caution"},(0,r.kt)("p",{parentName:"admonition"},"Data tests can only be defined at the root or in container scopes. They cannot be defined inside leaf scopes.")))}c.isMDXComponent=!0},86714:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/datatest1-55f0023d0e24fd14ff7081a3746e32ef.png"},32028:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/datatest2-caababc9c261775d74c2c236cbf23686.png"}}]);