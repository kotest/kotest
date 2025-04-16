"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[69648],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>u});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function a(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var s=r.createContext({}),p=function(e){var t=r.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},c=function(e){var t=p(e.components);return r.createElement(s.Provider,{value:t},e.children)},m={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,s=e.parentName,c=a(e,["components","mdxType","originalType","parentName"]),d=p(n),u=o,h=d["".concat(s,".").concat(u)]||d[u]||m[u]||i;return n?r.createElement(h,l(l({ref:t},c),{},{components:n})):r.createElement(h,l({ref:t},c))}));function u(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,l=new Array(i);l[0]=d;var a={};for(var s in t)hasOwnProperty.call(t,s)&&(a[s]=t[s]);a.originalType=e,a.mdxType="string"==typeof e?e:o,l[1]=a;for(var p=2;p<i;p++)l[p]=n[p];return r.createElement.apply(null,l)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},12041:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>l,default:()=>m,frontMatter:()=>i,metadata:()=>a,toc:()=>p});var r=n(87462),o=(n(67294),n(3905));const i={id:"compiler",title:"Compiler Matchers",slug:"compiler-matchers.html",sidebar_label:"Compiler"},l=void 0,a={unversionedId:"assertions/compiler",id:"version-5.4.x/assertions/compiler",title:"Compiler Matchers",description:"The `kotest-assertions-compiler` extension provides matchers to assert that given kotlin code snippet compiles or not.",source:"@site/versioned_docs/version-5.4.x/assertions/compiler.md",sourceDirName:"assertions",slug:"/assertions/compiler-matchers.html",permalink:"/docs/5.4.x/assertions/compiler-matchers.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.4.x/assertions/compiler.md",tags:[],version:"5.4.x",frontMatter:{id:"compiler",title:"Compiler Matchers",slug:"compiler-matchers.html",sidebar_label:"Compiler"},sidebar:"assertions",previous:{title:"Klock",permalink:"/docs/5.4.x/assertions/klock-matchers.html"},next:{title:"Jsoup",permalink:"/docs/5.4.x/assertions/jsoup-matchers.html"}},s={},p=[],c={toc:p};function m(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,r.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"The ",(0,o.kt)("inlineCode",{parentName:"p"},"kotest-assertions-compiler")," extension provides matchers to assert that given kotlin code snippet compiles or not.\nThis extension is a wrapper over ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/tschuchortdev/kotlin-compile-testing"},"kotlin-compile-testing")," and provides following matchers"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},"String.shouldCompile()"),(0,o.kt)("li",{parentName:"ul"},"String.shouldNotCompile()"),(0,o.kt)("li",{parentName:"ul"},"File.shouldCompile()"),(0,o.kt)("li",{parentName:"ul"},"File.shouldNotCompile()")),(0,o.kt)("p",null,"To add the compilation matcher, add the following dependency to your project"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-groovy"},'testImplementation("io.kotest.extensions:kotest-assertions-compiler:${version}")\n')),(0,o.kt)("p",null,"Usage:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'    class CompilationTest: StringSpec() {\n        init {\n            "shouldCompile test" {\n                val codeSnippet = """ val aString: String = "A valid assignment" """.trimMargin()\n\n                codeSnippet.shouldCompile()\n                File("SourceFile.kt").shouldCompile()\n            }\n\n            "shouldNotCompile test" {\n                val codeSnippet = """ val aInteger: Int = "A invalid assignment" """.trimMargin()\n\n                codeSnippet.shouldNotCompile()\n                File("SourceFile.kt").shouldNotCompile()\n            }\n        }\n    }\n')),(0,o.kt)("p",null,"During checking of code snippet compilation the classpath of calling process is inherited, which means any dependencies which are available in calling process will also be available while compiling the code snippet."),(0,o.kt)("p",null,"Matchers that verify if a given piece of Kotlin code compiles or not"),(0,o.kt)("table",null,(0,o.kt)("thead",{parentName:"table"},(0,o.kt)("tr",{parentName:"thead"},(0,o.kt)("th",{parentName:"tr",align:null},"Matcher"),(0,o.kt)("th",{parentName:"tr",align:null},"Description"))),(0,o.kt)("tbody",{parentName:"table"},(0,o.kt)("tr",{parentName:"tbody"},(0,o.kt)("td",{parentName:"tr",align:null},(0,o.kt)("inlineCode",{parentName:"td"},"string.shouldCompile()")),(0,o.kt)("td",{parentName:"tr",align:null},"Asserts that the string is a valid Kotlin code.")),(0,o.kt)("tr",{parentName:"tbody"},(0,o.kt)("td",{parentName:"tr",align:null},(0,o.kt)("inlineCode",{parentName:"td"},"file.shouldCompile()")),(0,o.kt)("td",{parentName:"tr",align:null},"Asserts that the file contains valid Kotlin code.")))))}m.isMDXComponent=!0}}]);