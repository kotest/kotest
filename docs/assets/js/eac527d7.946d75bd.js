"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[6795],{3905:(e,t,n)=>{n.d(t,{Zo:()=>d,kt:()=>f});var r=n(67294);function s(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){s(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,s=function(e,t){if(null==e)return{};var n,r,s={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(s[n]=e[n]);return s}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(s[n]=e[n])}return s}var l=r.createContext({}),p=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},d=function(e){var t=p(e.components);return r.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},c=r.forwardRef((function(e,t){var n=e.components,s=e.mdxType,o=e.originalType,l=e.parentName,d=i(e,["components","mdxType","originalType","parentName"]),c=p(n),f=s,m=c["".concat(l,".").concat(f)]||c[f]||u[f]||o;return n?r.createElement(m,a(a({ref:t},d),{},{components:n})):r.createElement(m,a({ref:t},d))}));function f(e,t){var n=arguments,s=t&&t.mdxType;if("string"==typeof e||s){var o=n.length,a=new Array(o);a[0]=c;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:s,a[1]=i;for(var p=2;p<o;p++)a[p]=n[p];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}c.displayName="MDXCreateElement"},46304:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>a,default:()=>u,frontMatter:()=>o,metadata:()=>i,toc:()=>p});var r=n(87462),s=(n(67294),n(3905));const o={id:"seeds",title:"Property Test Seeds",slug:"property-test-seeds.html",sidebar_label:"Seeds"},a=void 0,i={unversionedId:"proptest/seeds",id:"version-5.7.x/proptest/seeds",title:"Property Test Seeds",description:"When a property test is executed, the values are generated using a random source that is created from a seed value. By",source:"@site/versioned_docs/version-5.7.x/proptest/seed.md",sourceDirName:"proptest",slug:"/proptest/property-test-seeds.html",permalink:"/docs/5.7.x/proptest/property-test-seeds.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.7.x/proptest/seed.md",tags:[],version:"5.7.x",frontMatter:{id:"seeds",title:"Property Test Seeds",slug:"property-test-seeds.html",sidebar_label:"Seeds"},sidebar:"proptest",previous:{title:"Assumptions",permalink:"/docs/5.7.x/proptest/property-test-assumptions.html"},next:{title:"Configuration",permalink:"/docs/5.7.x/proptest/property-test-config.html"}},l={},p=[{value:"Manually specifying the seed",id:"manually-specifying-the-seed",level:3},{value:"Rerunning failed seeds",id:"rerunning-failed-seeds",level:3},{value:"Failing when seeds set",id:"failing-when-seeds-set",level:3}],d={toc:p};function u(e){let{components:t,...n}=e;return(0,s.kt)("wrapper",(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"When a property test is executed, the values are generated using a random source that is created from a seed value. By\ndefault this seed value is itself randomly chosen (using the default ",(0,s.kt)("inlineCode",{parentName:"p"},"kotlin.random.Random")," instance). However, there\nare times when this value needs to be fixed or repeated."),(0,s.kt)("p",null,"You can change the default used by all tests, unless overriden through the options listed below, by changing the\nconfiguration value ",(0,s.kt)("inlineCode",{parentName:"p"},"PropertyTesting.defaultSeed"),"."),(0,s.kt)("h3",{id:"manually-specifying-the-seed"},"Manually specifying the seed"),(0,s.kt)("p",null,"To manually set the seed, pass an instance of ",(0,s.kt)("inlineCode",{parentName:"p"},"PropTestConfig")," to your prop test methods. You might want to do this if\nyou find a test failure, and you want to ensure that those values continue to be executed in the future as a regression\ntest."),(0,s.kt)("p",null,"For example:"),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-kotlin"},'class PropertyExample : StringSpec({\n  "String size" {\n    forAll<String, String>(PropTestConfig(seed = 127305235)) { a, b ->\n      (a + b).length == a.length + b.length\n    }\n  }\n})\n')),(0,s.kt)("admonition",{type:"tip"},(0,s.kt)("p",{parentName:"admonition"},"Whenever a property test fails, Kotest will output the seed that was used. You can duplicate the test, setting it to use\nthis seed so you have permanent regression test for those values.")),(0,s.kt)("h3",{id:"rerunning-failed-seeds"},"Rerunning failed seeds"),(0,s.kt)("p",null,"By default, when a property test fails, the seed used by that test is written to a file\nin ",(0,s.kt)("inlineCode",{parentName:"p"},"~/.kotest/seeds/<spec>/<testname>"),". Whenever a property test runs, this seed is detected if the file exists, and\nused in place of a random seed. Next time the test is successful, the seed file will be removed."),(0,s.kt)("admonition",{type:"note"},(0,s.kt)("p",{parentName:"admonition"},"A manually specified seed always takes precedence over a failed seed.")),(0,s.kt)("admonition",{type:"tip"},(0,s.kt)("p",{parentName:"admonition"},"This feature can be disabled by setting ",(0,s.kt)("inlineCode",{parentName:"p"},"PropertyTesting.writeFailedSeed = false"))),(0,s.kt)("h3",{id:"failing-when-seeds-set"},"Failing when seeds set"),(0,s.kt)("p",null,"Some users prefer to avoid manually specifying seeds. They want to use them locally only, when developing, but to avoid\nchecking them in. If this is your style, then set ",(0,s.kt)("inlineCode",{parentName:"p"},"PropertyTesting.failOnSeed = false")," or the env\nvar ",(0,s.kt)("inlineCode",{parentName:"p"},"kotest.proptest.seed.fail-if-set")," to ",(0,s.kt)("inlineCode",{parentName:"p"},"false")," on your server."),(0,s.kt)("p",null,"Then if a seed is detected, the test suite will fail."))}u.isMDXComponent=!0}}]);