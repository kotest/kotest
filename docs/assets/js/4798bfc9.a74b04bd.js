"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[28193],{3905:(e,t,s)=>{s.d(t,{Zo:()=>p,kt:()=>m});var n=s(67294);function r(e,t,s){return t in e?Object.defineProperty(e,t,{value:s,enumerable:!0,configurable:!0,writable:!0}):e[t]=s,e}function a(e,t){var s=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),s.push.apply(s,n)}return s}function o(e){for(var t=1;t<arguments.length;t++){var s=null!=arguments[t]?arguments[t]:{};t%2?a(Object(s),!0).forEach((function(t){r(e,t,s[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(s)):a(Object(s)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(s,t))}))}return e}function i(e,t){if(null==e)return{};var s,n,r=function(e,t){if(null==e)return{};var s,n,r={},a=Object.keys(e);for(n=0;n<a.length;n++)s=a[n],t.indexOf(s)>=0||(r[s]=e[s]);return r}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)s=a[n],t.indexOf(s)>=0||Object.prototype.propertyIsEnumerable.call(e,s)&&(r[s]=e[s])}return r}var l=n.createContext({}),c=function(e){var t=n.useContext(l),s=t;return e&&(s="function"==typeof e?e(t):o(o({},t),e)),s},p=function(e){var t=c(e.components);return n.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},h=n.forwardRef((function(e,t){var s=e.components,r=e.mdxType,a=e.originalType,l=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),h=c(s),m=r,d=h["".concat(l,".").concat(m)]||h[m]||u[m]||a;return s?n.createElement(d,o(o({ref:t},p),{},{components:s})):n.createElement(d,o({ref:t},p))}));function m(e,t){var s=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var a=s.length,o=new Array(a);o[0]=h;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:r,o[1]=i;for(var c=2;c<a;c++)o[c]=s[c];return n.createElement.apply(null,o)}return n.createElement.apply(null,s)}h.displayName="MDXCreateElement"},64889:(e,t,s)=>{s.r(t),s.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>u,frontMatter:()=>a,metadata:()=>i,toc:()=>c});var n=s(87462),r=(s(67294),s(3905));const a={id:"index",title:"Assertions",slug:"assertions.html"},o=void 0,i={unversionedId:"assertions/index",id:"version-5.2.x/assertions/index",title:"Assertions",description:"Kotest is split into several subprojects which can be used independently. One of these subprojects is",source:"@site/versioned_docs/version-5.2.x/assertions/index.md",sourceDirName:"assertions",slug:"/assertions/assertions.html",permalink:"/docs/5.2.x/assertions/assertions.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.2.x/assertions/index.md",tags:[],version:"5.2.x",frontMatter:{id:"index",title:"Assertions",slug:"assertions.html"},sidebar:"assertions",next:{title:"Matchers",permalink:"/docs/5.2.x/assertions/matchers.html"}},l={},c=[{value:"Multitude of Matchers",id:"multitude-of-matchers",level:2},{value:"Clues",id:"clues",level:2},{value:"Inspectors",id:"inspectors",level:2},{value:"Custom Matchers",id:"custom-matchers",level:2}],p={toc:c};function u(e){let{components:t,...s}=e;return(0,r.kt)("wrapper",(0,n.Z)({},p,s,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Kotest is split into several subprojects which can be used independently. One of these subprojects is\nthe comprehensive assertion / matchers support. These can be used with the ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/framework/framework.html"},"Kotest test framework"),",\nor with another test framework like JUnit or Spock."),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://search.maven.org/search?q=g:io.kotest"},(0,r.kt)("img",{parentName:"a",src:"https://img.shields.io/maven-central/v/io.kotest/kotest-assertions-core-jvm.svg?label=release",alt:"version badge"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/"},(0,r.kt)("img",{parentName:"a",src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-assertions-core-jvm.svg?label=snapshot",alt:"version badge"}))),(0,r.kt)("p",null,"The core functionality of the assertion modules are functions that test state. Kotest calls these types of state\nassertion functions ",(0,r.kt)("em",{parentName:"p"},"matchers"),". There are ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/matchers.html"},"core")," matchers and matchers for third party libraries."),(0,r.kt)("p",null,"There are also many other utilities for writing tests, such as ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/exceptions.html"},"testing for exceptions"),", functions to\nhelp test ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/non-deterministic-testing.html"},"non-determistic code"),", ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/inspectors.html"},"inspectors")," for collections, and\n",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/soft-assertions.html"},"soft assertions")," to group assertions."),(0,r.kt)("h2",{id:"multitude-of-matchers"},"Multitude of Matchers"),(0,r.kt)("p",null,"For example, to assert that a variable has an expected value, we can use the ",(0,r.kt)("inlineCode",{parentName:"p"},"shouldBe")," function."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'name shouldBe "sam"\n')),(0,r.kt)("p",null,"There are general purpose matchers, such as ",(0,r.kt)("inlineCode",{parentName:"p"},"shouldBe")," as well as matchers for many other specific scenarios,\nsuch as ",(0,r.kt)("inlineCode",{parentName:"p"},"str.shouldHaveLength(10)")," for testing the length of a string, and ",(0,r.kt)("inlineCode",{parentName:"p"},"file.shouldBeDirectory()")," which test\nthat a particular file points to a directory. They come in both infix and regular variants."),(0,r.kt)("p",null,"Assertions can generally be chained, for example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'"substring".shouldContain("str")\n           .shouldBeLowerCase()\n\nmyImageFile.shouldHaveExtension(".jpg")\n           .shouldStartWith("https")\n')),(0,r.kt)("p",null,"There are over 350 matchers spread across multiple modules. Read about all the ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/matchers.html"},"matchers here"),"."),(0,r.kt)("h2",{id:"clues"},"Clues"),(0,r.kt)("p",null,"Sometimes a failed assertion does not contain enough information to know exactly what went wrong."),(0,r.kt)("p",null,"For example,"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"user.name shouldNotBe null\n")),(0,r.kt)("p",null,"If this failed, you would simply get:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"<null> should not equal <null>\n")),(0,r.kt)("p",null,"Which isn't particularly helpful. We can add extra context to failure messages through the use of ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/clues.html"},"clues"),"."),(0,r.kt)("h2",{id:"inspectors"},"Inspectors"),(0,r.kt)("p",null,"Inspectors allow us to test elements in a collection, and assert the quantity of elements that should be\nexpected to pass (all, none, exactly k and so on). For example"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'mylist.forExactly(3) {\n    it.city shouldBe "Chicago"\n}\n')),(0,r.kt)("p",null,"Read about ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/inspectors.html"},"inspectors here")),(0,r.kt)("h2",{id:"custom-matchers"},"Custom Matchers"),(0,r.kt)("p",null,"It is easy to add your own matchers by extending the ",(0,r.kt)("inlineCode",{parentName:"p"},"Matcher<T>")," interface, where T is the type you wish to match against. Custom matchers can compose existing matchers or be completely standalone."),(0,r.kt)("p",null,"See a ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/assertions/custom-matchers.html"},"full worked example"),"."))}u.isMDXComponent=!0}}]);