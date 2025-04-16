"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[83212],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>k});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var o=a.createContext({}),d=function(e){var t=a.useContext(o),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},c=function(e){var t=d(e.components);return a.createElement(o.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},m=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,o=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),m=d(n),k=r,u=m["".concat(o,".").concat(k)]||m[k]||p[k]||i;return n?a.createElement(u,l(l({ref:t},c),{},{components:n})):a.createElement(u,l({ref:t},c))}));function k(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,l=new Array(i);l[0]=m;var s={};for(var o in t)hasOwnProperty.call(t,o)&&(s[o]=t[o]);s.originalType=e,s.mdxType="string"==typeof e?e:r,l[1]=s;for(var d=2;d<i;d++)l[d]=n[d];return a.createElement.apply(null,l)}return a.createElement.apply(null,n)}m.displayName="MDXCreateElement"},24923:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>o,contentTitle:()=>l,default:()=>p,frontMatter:()=>i,metadata:()=>s,toc:()=>d});var a=n(87462),r=(n(67294),n(3905));const i={id:"simple_extensions",title:"Simple Extensions",slug:"simple-extensions.html",sidebar_label:"Simple Extensions"},l=void 0,s={unversionedId:"framework/extensions/simple_extensions",id:"version-5.5.x/framework/extensions/simple_extensions",title:"Simple Extensions",description:"This table lists the most basic extensions, that cover test and spec lifecycle events, and are mostly equivalent to lifecycle hooks. For more advanced extensions that can be used to modify the way the Engine runs, see advanced extensions.",source:"@site/versioned_docs/version-5.5.x/framework/extensions/simple.md",sourceDirName:"framework/extensions",slug:"/framework/extensions/simple-extensions.html",permalink:"/docs/5.5.x/framework/extensions/simple-extensions.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.5.x/framework/extensions/simple.md",tags:[],version:"5.5.x",frontMatter:{id:"simple_extensions",title:"Simple Extensions",slug:"simple-extensions.html",sidebar_label:"Simple Extensions"},sidebar:"framework",previous:{title:"Introduction",permalink:"/docs/5.5.x/framework/extensions/extensions-introduction.html"},next:{title:"Advanced Extensions",permalink:"/docs/5.5.x/framework/extensions/advanced-extensions.html"}},o={},d=[],c={toc:d};function p(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,a.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"This table lists the most basic extensions, that cover test and spec lifecycle events, and are mostly equivalent to lifecycle hooks. For more advanced extensions that can be used to modify the way the Engine runs, see ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.5.x/framework/extensions/advanced-extensions.html"},"advanced extensions"),"."),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Extension"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"BeforeContainerListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked directly before each test with type ",(0,r.kt)("inlineCode",{parentName:"td"},"TestType.Container")," is executed. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"AfterContainerListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked immediately after a ",(0,r.kt)("inlineCode",{parentName:"td"},"TestCase")," with type ",(0,r.kt)("inlineCode",{parentName:"td"},"TestType.Container")," has finished. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.",(0,r.kt)("br",null),(0,r.kt)("br",null),"The callback will execute even if the test fails.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"BeforeEachListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked directly before each test with type ",(0,r.kt)("inlineCode",{parentName:"td"},"TestType.Test")," is executed. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"AfterEachListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked immediately after a ",(0,r.kt)("inlineCode",{parentName:"td"},"TestCase")," with type ",(0,r.kt)("inlineCode",{parentName:"td"},"TestType.Test")," has finished, with the ",(0,r.kt)("inlineCode",{parentName:"td"},"TestResult")," of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.",(0,r.kt)("br",null),(0,r.kt)("br",null),"The callback will execute even if the test fails.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"BeforeTestListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked directly before each test is executed with the ",(0,r.kt)("inlineCode",{parentName:"td"},"TestCase")," instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"AfterTestListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked immediately after a ",(0,r.kt)("inlineCode",{parentName:"td"},"TestCase")," has finished with the ",(0,r.kt)("inlineCode",{parentName:"td"},"TestResult")," of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.",(0,r.kt)("br",null),(0,r.kt)("br",null),"The callback will execute even if the test fails.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"BeforeInvocationListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked before each 'run' of a test, with a flag indicating the iteration number. If you are running a test with the default single invocation then this callback is effectively the same as ",(0,r.kt)("inlineCode",{parentName:"td"},"beforeTest"),".",(0,r.kt)("br",null),(0,r.kt)("br",null),(0,r.kt)("em",{parentName:"td"},"Note: If you have multiple invocations and multiple threads, then this callback will be invoked concurrently."))),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"AfterInvocationListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked after each 'run' of a test, with a flag indicating the iteration number. If you are running a test with the default single invocation then this callback is effectively the same as ",(0,r.kt)("inlineCode",{parentName:"td"},"afterTest"),".",(0,r.kt)("br",null),(0,r.kt)("br",null),(0,r.kt)("em",{parentName:"td"},"Note: If you have multiple invocations and multiple threads, then this callback will be invoked concurrently."))),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"BeforeSpecListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Invoked after the Engine instantiates a spec to be used as part of a test execution. If a spec is instantiated multiple times - for example, if ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerTest")," or ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerLeaf")," isolation modes are used, then this callback will be invoked for each instance created.",(0,r.kt)("br",null),(0,r.kt)("br",null),"This callback can be used to perform setup each time a new spec instance is created. To perform setup once per class, then use ",(0,r.kt)("inlineCode",{parentName:"td"},"PrepareSpecListener"),".",(0,r.kt)("br",null),(0,r.kt)("br",null),"This listener is invoked before any test lifecycle events.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"AfterSpecListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Is invoked after the ",(0,r.kt)("inlineCode",{parentName:"td"},"TestCase"),"s that are part of a particular spec instance have completed.",(0,r.kt)("br",null),(0,r.kt)("br",null),"If a spec is instantiated multiple times - for example, if ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerTest")," or ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerLeaf")," isolation modes are used, then this callback will be invoked for each instantiated spec, after the tests that are applicable to that spec instance have returned.",(0,r.kt)("br",null),(0,r.kt)("br",null),"This callback can be used to perform cleanup after each individual spec instance. To perform cleanup once per class, then use ",(0,r.kt)("inlineCode",{parentName:"td"},"FinalizeSpecListener."),(0,r.kt)("br",null),(0,r.kt)("br",null),"This listener is invoked after all test lifecycle events.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"PrepareSpecListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Called once per spec, when the engine is preparing to execute the tests for that spec.",(0,r.kt)("br",null),(0,r.kt)("br",null),"Regardless of how many times the spec is instantiated, for example, if ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerTest")," or ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerLeaf")," isolation modes are used, this callback will only be invoked once. If the spec is skipped then this callback will ",(0,r.kt)("strong",{parentName:"td"},"not")," be invoked.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"FinalizeSpecListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Called once per ",(0,r.kt)("inlineCode",{parentName:"td"},"Spec"),", after all tests have completed for that spec.",(0,r.kt)("br",null),(0,r.kt)("br",null),"Regardless of how many times the spec is instantiated, for example, if ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerTest")," or ",(0,r.kt)("inlineCode",{parentName:"td"},"InstancePerLeaf")," isolation modes are used, this callback will only be invoked once. If the spec is skipped then this callback will ",(0,r.kt)("strong",{parentName:"td"},"not")," be invoked.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"BeforeProjectListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Is invoked before any specs are created.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"AfterProjectListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Is invoked once all tests and specs have completed")))))}p.isMDXComponent=!0}}]);