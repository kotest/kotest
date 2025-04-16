"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[20765],{3905:(e,t,o)=>{o.d(t,{Zo:()=>p,kt:()=>d});var r=o(67294);function n(e,t,o){return t in e?Object.defineProperty(e,t,{value:o,enumerable:!0,configurable:!0,writable:!0}):e[t]=o,e}function i(e,t){var o=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),o.push.apply(o,r)}return o}function c(e){for(var t=1;t<arguments.length;t++){var o=null!=arguments[t]?arguments[t]:{};t%2?i(Object(o),!0).forEach((function(t){n(e,t,o[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(o)):i(Object(o)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(o,t))}))}return e}function s(e,t){if(null==e)return{};var o,r,n=function(e,t){if(null==e)return{};var o,r,n={},i=Object.keys(e);for(r=0;r<i.length;r++)o=i[r],t.indexOf(o)>=0||(n[o]=e[o]);return n}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)o=i[r],t.indexOf(o)>=0||Object.prototype.propertyIsEnumerable.call(e,o)&&(n[o]=e[o])}return n}var l=r.createContext({}),a=function(e){var t=r.useContext(l),o=t;return e&&(o="function"==typeof e?e(t):c(c({},t),e)),o},p=function(e){var t=a(e.components);return r.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},b=r.forwardRef((function(e,t){var o=e.components,n=e.mdxType,i=e.originalType,l=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),b=a(o),d=n,m=b["".concat(l,".").concat(d)]||b[d]||u[d]||i;return o?r.createElement(m,c(c({ref:t},p),{},{components:o})):r.createElement(m,c({ref:t},p))}));function d(e,t){var o=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var i=o.length,c=new Array(i);c[0]=b;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:n,c[1]=s;for(var a=2;a<i;a++)c[a]=o[a];return r.createElement.apply(null,c)}return r.createElement.apply(null,o)}b.displayName="MDXCreateElement"},20232:(e,t,o)=>{o.r(t),o.d(t,{assets:()=>l,contentTitle:()=>c,default:()=>u,frontMatter:()=>i,metadata:()=>s,toc:()=>a});var r=o(87462),n=(o(67294),o(3905));const i={id:"robolectric",title:"Robolectric",sidebar_label:"Robolectric",slug:"robolectric.html"},c=void 0,s={unversionedId:"extensions/robolectric",id:"version-5.3.x/extensions/robolectric",title:"Robolectric",description:"Robolectric",source:"@site/versioned_docs/version-5.3.x/extensions/roboelectric.md",sourceDirName:"extensions",slug:"/extensions/robolectric.html",permalink:"/docs/5.3.x/extensions/robolectric.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.3.x/extensions/roboelectric.md",tags:[],version:"5.3.x",frontMatter:{id:"robolectric",title:"Robolectric",sidebar_label:"Robolectric",slug:"robolectric.html"},sidebar:"extensions",previous:{title:"WireMock",permalink:"/docs/5.3.x/extensions/wiremock.html"},next:{title:"Pitest",permalink:"/docs/5.3.x/extensions/pitest.html"}},l={},a=[{value:"Robolectric",id:"robolectric",level:2}],p={toc:a};function u(e){let{components:t,...o}=e;return(0,n.kt)("wrapper",(0,r.Z)({},p,o,{components:t,mdxType:"MDXLayout"}),(0,n.kt)("h2",{id:"robolectric"},"Robolectric"),(0,n.kt)("p",null,(0,n.kt)("a",{parentName:"p",href:"https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-robolectric"},(0,n.kt)("img",{parentName:"a",src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-robolectric",alt:"Latest Release"}))),(0,n.kt)("p",null,(0,n.kt)("a",{parentName:"p",href:"http://robolectric.org/"},"Robolectric")," can be used with Kotest through the ",(0,n.kt)("inlineCode",{parentName:"p"},"RobolectricExtension")," which can be found in a separate repository,",(0,n.kt)("a",{parentName:"p",href:"https://github.com/kotest/kotest-extensions-robolectric"},"kotest-extensions-robolectric")),(0,n.kt)("p",null,"To add this module to project you need specify following in your ",(0,n.kt)("inlineCode",{parentName:"p"},"build.gradle"),":"),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},'testImplementation("io.kotest.extensions:kotest-extensions-robolectric:${version}")\n')),(0,n.kt)("p",null,"This dependency brings in ",(0,n.kt)("inlineCode",{parentName:"p"},"RobolectricExtension"),", which is autoregistered to your projects."),(0,n.kt)("p",null,"Now all you need to do is annotate Robolectric specs with ",(0,n.kt)("inlineCode",{parentName:"p"},"@RobolectricTest")," and you're set!"),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},'@RobolectricTest\nclass MyTest : ShouldSpec({\n    should("Access Robolectric normally!") {\n\n    }\n})\n')))}u.isMDXComponent=!0}}]);