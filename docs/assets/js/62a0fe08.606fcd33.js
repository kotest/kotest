"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[61749],{3905:(t,e,n)=>{n.d(e,{Zo:()=>c,kt:()=>u});var a=n(67294);function r(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}function i(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(t);e&&(a=a.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,a)}return n}function s(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?i(Object(n),!0).forEach((function(e){r(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function o(t,e){if(null==t)return{};var n,a,r=function(t,e){if(null==t)return{};var n,a,r={},i=Object.keys(t);for(a=0;a<i.length;a++)n=i[a],e.indexOf(n)>=0||(r[n]=t[n]);return r}(t,e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(t);for(a=0;a<i.length;a++)n=i[a],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(r[n]=t[n])}return r}var l=a.createContext({}),p=function(t){var e=a.useContext(l),n=e;return t&&(n="function"==typeof t?t(e):s(s({},e),t)),n},c=function(t){var e=p(t.components);return a.createElement(l.Provider,{value:e},t.children)},d={inlineCode:"code",wrapper:function(t){var e=t.children;return a.createElement(a.Fragment,{},e)}},m=a.forwardRef((function(t,e){var n=t.components,r=t.mdxType,i=t.originalType,l=t.parentName,c=o(t,["components","mdxType","originalType","parentName"]),m=p(n),u=r,k=m["".concat(l,".").concat(u)]||m[u]||d[u]||i;return n?a.createElement(k,s(s({ref:e},c),{},{components:n})):a.createElement(k,s({ref:e},c))}));function u(t,e){var n=arguments,r=e&&e.mdxType;if("string"==typeof t||r){var i=n.length,s=new Array(i);s[0]=m;var o={};for(var l in e)hasOwnProperty.call(e,l)&&(o[l]=e[l]);o.originalType=t,o.mdxType="string"==typeof t?t:r,s[1]=o;for(var p=2;p<i;p++)s[p]=n[p];return a.createElement.apply(null,s)}return a.createElement.apply(null,n)}m.displayName="MDXCreateElement"},2500:(t,e,n)=>{n.r(e),n.d(e,{assets:()=>l,contentTitle:()=>s,default:()=>d,frontMatter:()=>i,metadata:()=>o,toc:()=>p});var a=n(87462),r=(n(67294),n(3905));const i={id:"advanced_extensions",title:"Advanced Extensions",slug:"advanced-extensions.html",sidebar_label:"Advanced Extensions"},s=void 0,o={unversionedId:"framework/extensions/advanced_extensions",id:"version-5.9.x/framework/extensions/advanced_extensions",title:"Advanced Extensions",description:"This table lists more advanced extensions that can be used to hook into the Engine itself to:",source:"@site/versioned_docs/version-5.9.x/framework/extensions/advanced.md",sourceDirName:"framework/extensions",slug:"/framework/extensions/advanced-extensions.html",permalink:"/docs/framework/extensions/advanced-extensions.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.9.x/framework/extensions/advanced.md",tags:[],version:"5.9.x",frontMatter:{id:"advanced_extensions",title:"Advanced Extensions",slug:"advanced-extensions.html",sidebar_label:"Advanced Extensions"},sidebar:"framework",previous:{title:"Simple Extensions",permalink:"/docs/framework/extensions/simple-extensions.html"},next:{title:"Examples",permalink:"/docs/framework/extensions/extension-examples.html"}},l={},p=[],c={toc:p};function d(t){let{components:e,...n}=t;return(0,r.kt)("wrapper",(0,a.Z)({},c,n,{components:e,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"This table lists more advanced extensions that can be used to hook into the Engine itself to:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"intercept tests, skipping them, and modify test results"),(0,r.kt)("li",{parentName:"ul"},"intercept specs specs skipping them if required"),(0,r.kt)("li",{parentName:"ul"},"post process spec instances after instantiation"),(0,r.kt)("li",{parentName:"ul"},"modify the coroutine context used by specs and tests"),(0,r.kt)("li",{parentName:"ul"},"apply custom instantiation logic"),(0,r.kt)("li",{parentName:"ul"},"filter specs and tests"),(0,r.kt)("li",{parentName:"ul"},"adjust test output")),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Extension"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"ConstructorExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Provides custom logic to instantiate spec classes. An example is the Spring extension constructor extension which autowire's spring beans.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"TestCaseExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Intercepts calls to a test, can skip a test, override the test result, and modify the coroutine context.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"SpecExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Intercepts calls to a spec, can skip a spec, and modify the coroutine context.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"SpecRefExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Intercepts calls to a spec before it is instantiated. Can skip instantiation.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"DisplayNameFormatterExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Can customize the display names of tests used in test output.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"EnabledExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Can provide custom logic to determine if a test is enabled or disabled.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"ProjectExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Intercepts calls to the test engine before a project starts.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"SpecExecutionOrderExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Can sort specs before execution begins to provide a custom spec execution order.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"TagExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Can provide active tags from arbitrary sources.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"InstantiationErrorListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Is notified when a spec fails to be instantiated due to some exception.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"InstantiationListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Is notified when a spec is successfully instantiated.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"PostInstantiationExtension"),(0,r.kt)("td",{parentName:"tr",align:null},"Intercepts specs when they are instantiated, can replace the spec instance and modify coroutine context.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"IgnoredSpecListener"),(0,r.kt)("td",{parentName:"tr",align:null},"Is notified when a spec is skipped.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"SpecFilter"),(0,r.kt)("td",{parentName:"tr",align:null},"Can provide custom logic to skip a spec.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"TestFilter"),(0,r.kt)("td",{parentName:"tr",align:null},"Can provide custom logic to skip a test.")))))}d.isMDXComponent=!0}}]);