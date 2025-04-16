"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[28762],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>d});var r=n(67294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var l=r.createContext({}),p=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=p(e.components);return r.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,o=e.originalType,l=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),m=p(n),d=a,h=m["".concat(l,".").concat(d)]||m[d]||u[d]||o;return n?r.createElement(h,i(i({ref:t},c),{},{components:n})):r.createElement(h,i({ref:t},c))}));function d(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=n.length,i=new Array(o);i[0]=m;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:a,i[1]=s;for(var p=2;p<o;p++)i[p]=n[p];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},52839:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>u,frontMatter:()=>o,metadata:()=>s,toc:()=>p});var r=n(87462),a=(n(67294),n(3905));const o={id:"testfunctions",title:"Property Test Functions",slug:"property-test-functions.html",sidebar_label:"Test Functions"},i=void 0,s={unversionedId:"proptest/testfunctions",id:"version-5.8.x/proptest/testfunctions",title:"Property Test Functions",description:"There are two variants of functions that are used to execute a property test in Kotest: forAll and checkAll.",source:"@site/versioned_docs/version-5.8.x/proptest/test_functions.md",sourceDirName:"proptest",slug:"/proptest/property-test-functions.html",permalink:"/docs/5.8.x/proptest/property-test-functions.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.8.x/proptest/test_functions.md",tags:[],version:"5.8.x",frontMatter:{id:"testfunctions",title:"Property Test Functions",slug:"property-test-functions.html",sidebar_label:"Test Functions"},sidebar:"proptest",previous:{title:"Introduction",permalink:"/docs/5.8.x/proptest/property-based-testing.html"},next:{title:"Generators",permalink:"/docs/5.8.x/proptest/property-test-generators.html"}},l={},p=[{value:"For All",id:"for-all",level:3},{value:"Check All",id:"check-all",level:3},{value:"Iterations",id:"iterations",level:3},{value:"Specifying Generators",id:"specifying-generators",level:3}],c={toc:p};function u(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"There are two variants of functions that are used to execute a property test in Kotest: ",(0,a.kt)("inlineCode",{parentName:"p"},"forAll")," and ",(0,a.kt)("inlineCode",{parentName:"p"},"checkAll"),"."),(0,a.kt)("h3",{id:"for-all"},"For All"),(0,a.kt)("p",null,"The first, ",(0,a.kt)("inlineCode",{parentName:"p"},"forAll"),", accepts an n-arity function ",(0,a.kt)("inlineCode",{parentName:"p"},"(a, ..., n) -> Boolean")," that tests the property.\nThe test will pass if, for all input values, the function returns true."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class PropertyExample: StringSpec({\n   "String size" {\n      forAll<String, String> { a, b ->\n         (a + b).length == a.length + b.length\n      }\n   }\n})\n')),(0,a.kt)("p",null,"Notice that this functions accepts type parameters for the argument types, with arity up to 14.\nKotest uses these type parameters to locate a ",(0,a.kt)("em",{parentName:"p"},"generator")," which provides (generates) random values of a suitable type."),(0,a.kt)("p",null,"For example, ",(0,a.kt)("inlineCode",{parentName:"p"},"forAll<String, Int, Boolean> { a, b, c -> }")," is a 3-arity property test where\nargument ",(0,a.kt)("inlineCode",{parentName:"p"},"a")," is a random String, argument ",(0,a.kt)("inlineCode",{parentName:"p"},"b")," is a random int, and argument ",(0,a.kt)("inlineCode",{parentName:"p"},"c")," is a random boolean."),(0,a.kt)("h3",{id:"check-all"},"Check All"),(0,a.kt)("p",null,"The second, ",(0,a.kt)("inlineCode",{parentName:"p"},"checkAll"),", accepts an n-arity function ",(0,a.kt)("inlineCode",{parentName:"p"},"(a, ..., n) -> Unit")," in which you can simply execute assertions against the inputs.\nThis approach will consider a test valid if no exceptions are thrown.\nHere is the same example again written in the equivalent way using checkAll."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class PropertyExample: StringSpec({\n   "String size" {\n      checkAll<String, String> { a, b ->\n         a + b shouldHaveLength a.length + b.length\n      }\n   }\n})\n')),(0,a.kt)("p",null,"The second approach is more general purpose than returning a boolean, but the first approach is from the original\nhaskell libraries that inspired this library."),(0,a.kt)("h3",{id:"iterations"},"Iterations"),(0,a.kt)("p",null,"By default, Kotest will run the property test 1000 times. We can easily customize this by specifying the iteration count\nwhen invoking the test method."),(0,a.kt)("p",null,"Let's say we want to run a test 10,000 times."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class PropertyExample: StringSpec({\n   "a many iterations test" {\n      checkAll<Double, Double>(10_000) { a, b ->\n         // test here\n      }\n   }\n})\n')),(0,a.kt)("h3",{id:"specifying-generators"},"Specifying Generators"),(0,a.kt)("p",null,"You saw in the previous examples that Kotest would provide values automatically based on the type parameter(s).\nIt does this by locating a ",(0,a.kt)("em",{parentName:"p"},"generator")," that generates values for the required type."),(0,a.kt)("p",null,"For example, the automatically provided ",(0,a.kt)("em",{parentName:"p"},"Integer")," generator generates random ints from all possible values -\nnegative, positive, infinities, zero and so on."),(0,a.kt)("p",null,"This is fine for basic tests but often we want more control over the sample space.\nFor example, we may want to test a function for numbers in a certain range only."),(0,a.kt)("p",null,"Then you would need to specify the generator(s) manually."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'class PropertyExample: StringSpec({\n   "is allowed to drink in Chicago" {\n      forAll(Arb.int(21..150)) { a ->\n         isDrinkingAge(a) // assuming some function that calculates if we\'re old enough to drink\n      }\n   }\n   "is allowed to drink in London" {\n      forAll(Arb.int(18..150)) { a ->\n         isDrinkingAge(a) // assuming some function that calculates if we\'re old enough to drink\n      }\n   }\n})\n')),(0,a.kt)("p",null,"You can see we created two tests and in each test passed a generator into the ",(0,a.kt)("inlineCode",{parentName:"p"},"forAll")," function with a suitable int range."),(0,a.kt)("p",null,"See ",(0,a.kt)("a",{parentName:"p",href:"/docs/5.8.x/proptest/property-test-generators.html"},"here")," for a list of the built in generators."))}u.isMDXComponent=!0}}]);