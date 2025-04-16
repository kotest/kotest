"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[86968],{3905:(e,t,r)=>{r.d(t,{Zo:()=>p,kt:()=>f});var n=r(67294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function l(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var s=n.createContext({}),c=function(e){var t=n.useContext(s),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},p=function(e){var t=c(e.components);return n.createElement(s.Provider,{value:t},e.children)},m={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,s=e.parentName,p=l(e,["components","mdxType","originalType","parentName"]),u=c(r),f=o,d=u["".concat(s,".").concat(f)]||u[f]||m[f]||a;return r?n.createElement(d,i(i({ref:t},p),{},{components:r})):n.createElement(d,i({ref:t},p))}));function f(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=u;var l={};for(var s in t)hasOwnProperty.call(t,s)&&(l[s]=t[s]);l.originalType=e,l.mdxType="string"==typeof e?e:o,i[1]=l;for(var c=2;c<a;c++)i[c]=r[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}u.displayName="MDXCreateElement"},39981:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>s,contentTitle:()=>i,default:()=>m,frontMatter:()=>a,metadata:()=>l,toc:()=>c});var n=r(87462),o=(r(67294),r(3905));const a={id:"tempfile",title:"Temporary Files",slug:"temporary-files"},i=void 0,l={unversionedId:"framework/tempfile",id:"version-5.5.x/framework/tempfile",title:"Temporary Files",description:"Sometimes it is required for a test to create a file and delete it after test, deleting it manually may lead to flaky",source:"@site/versioned_docs/version-5.5.x/framework/tempfile.md",sourceDirName:"framework",slug:"/framework/temporary-files",permalink:"/docs/5.5.x/framework/temporary-files",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.5.x/framework/tempfile.md",tags:[],version:"5.5.x",frontMatter:{id:"tempfile",title:"Temporary Files",slug:"temporary-files"},sidebar:"framework",previous:{title:"Closing resources automatically",permalink:"/docs/5.5.x/framework/autoclose.html"},next:{title:"Test Case Config",permalink:"/docs/5.5.x/framework/testcaseconfig.html"}},s={},c=[{value:"Temporary Directories",id:"temporary-directories",level:2}],p={toc:c};function m(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},p,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"Sometimes it is required for a test to create a file and delete it after test, deleting it manually may lead to flaky\ntest."),(0,o.kt)("p",null,"For example, you may be using a temporary file during a test. If the test passes successfully, your clean up code will execute\nand the file will be deleted. But in case the assertion fails or another error occurs, which may have caused the file to not be deleted, you will get a stale file\nwhich might affect the test on the next run (file cannot be overwritten exception and so on)."),(0,o.kt)("p",null,"Kotest provides a function ",(0,o.kt)("inlineCode",{parentName:"p"},"tempfile()")," which you can use in your Spec to create a temporary file for your tests, and which\nKotest will take the responsibility of cleaning up after running all tests in the Spec. This way your\ntests does not have to worry about deleting the temporary file."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MySpec : FunSpec({\n\n   val file = tempfile()\n\n   test("a temporary file dependent test") {\n      //...\n   }\n})\n\n')),(0,o.kt)("h2",{id:"temporary-directories"},"Temporary Directories"),(0,o.kt)("p",null,"Similar to temp files, we can create a temp dir using ",(0,o.kt)("inlineCode",{parentName:"p"},"tempdir()"),"."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MySpec : FunSpec({\n\n   val dir = tempdir()\n\n   test("a temporary dir dependent test") {\n      //...\n   }\n})\n')))}m.isMDXComponent=!0}}]);