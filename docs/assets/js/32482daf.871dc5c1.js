"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[93268],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>d});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},p=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,a=e.originalType,l=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),m=c(n),d=o,f=m["".concat(l,".").concat(d)]||m[d]||u[d]||a;return n?r.createElement(f,s(s({ref:t},p),{},{components:n})):r.createElement(f,s({ref:t},p))}));function d(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=n.length,s=new Array(a);s[0]=m;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:o,s[1]=i;for(var c=2;c<a;c++)s[c]=n[c];return r.createElement.apply(null,s)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},61542:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>s,default:()=>u,frontMatter:()=>a,metadata:()=>i,toc:()=>c});var r=n(87462),o=(n(67294),n(3905));const a={id:"index",title:"Introduction",slug:"framework.html"},s=void 0,i={unversionedId:"framework/index",id:"version-5.8.x/framework/index",title:"Introduction",description:"introgif",source:"@site/versioned_docs/version-5.8.x/framework/index.md",sourceDirName:"framework",slug:"/framework/framework.html",permalink:"/docs/5.8.x/framework/framework.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.8.x/framework/index.md",tags:[],version:"5.8.x",frontMatter:{id:"index",title:"Introduction",slug:"framework.html"},sidebar:"framework",next:{title:"Setup",permalink:"/docs/5.8.x/framework/project-setup.html"}},l={},c=[{value:"Test with Style",id:"test-with-style",level:2},{value:"Check all the Tricky Cases With Data Driven Testing",id:"check-all-the-tricky-cases-with-data-driven-testing",level:2},{value:"Fine Tune Test Execution",id:"fine-tune-test-execution",level:2}],p={toc:c};function u(e){let{components:t,...a}=e;return(0,o.kt)("wrapper",(0,r.Z)({},p,a,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,(0,o.kt)("img",{alt:"intro_gif",src:n(75618).Z,width:"600",height:"230"})),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://search.maven.org/search?q=g:io.kotest%20OR%20g:io.kotest.extensions"},(0,o.kt)("img",{parentName:"a",src:"https://img.shields.io/maven-central/v/io.kotest/kotest-framework-engine.svg?label=release",alt:"version badge"})),"\n",(0,o.kt)("a",{parentName:"p",href:"https://s01.oss.sonatype.org/content/repositories/snapshots/io/kotest/"},(0,o.kt)("img",{parentName:"a",src:"https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot",alt:"version badge"}))),(0,o.kt)("h2",{id:"test-with-style"},"Test with Style"),(0,o.kt)("p",null,"Write ",(0,o.kt)("a",{parentName:"p",href:"/docs/5.8.x/framework/writing-tests.html"},"simple and beautiful tests")," using one of the available styles:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTests : StringSpec({\n   "length should return size of string" {\n      "hello".length shouldBe 5\n   }\n   "startsWith should test for a prefix" {\n      "world" should startWith("wor")\n   }\n})\n')),(0,o.kt)("p",null,"Kotest allows tests to be created in several styles, so you can choose the style that suits you best."),(0,o.kt)("h2",{id:"check-all-the-tricky-cases-with-data-driven-testing"},"Check all the Tricky Cases With Data Driven Testing"),(0,o.kt)("p",null,"Handle even an enormous amount of input parameter combinations easily with ",(0,o.kt)("a",{parentName:"p",href:"/docs/5.8.x/framework/datatesting/data-driven-testing.html"},"data driven tests"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class StringSpecExample : StringSpec({\n   "maximum of two numbers" {\n      forAll(\n         row(1, 5, 5),\n         row(1, 0, 1),\n         row(0, 0, 0)\n      ) { a, b, max ->\n         Math.max(a, b) shouldBe max\n      }\n   }\n})\n')),(0,o.kt)("h2",{id:"fine-tune-test-execution"},"Fine Tune Test Execution"),(0,o.kt)("p",null,"You can specify the number of invocations, parallelism, and a timeout for each test or for all tests. And you can group\ntests by tags or disable them conditionally. All you need is ",(0,o.kt)("a",{parentName:"p",href:"/docs/5.8.x/framework/project-config.html"},(0,o.kt)("inlineCode",{parentName:"a"},"config")),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MySpec : StringSpec({\n   "should use config".config(timeout = 2.seconds, invocations = 10, threads = 2, tags = setOf(Database, Linux)) {\n      // test here\n   }\n})\n')))}u.isMDXComponent=!0},75618:(e,t,n)=>{n.d(t,{Z:()=>r});const r=n.p+"assets/images/intro_gif-41d4e868847b330dec1c3b60e500b4fb.gif"}}]);