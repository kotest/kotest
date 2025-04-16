"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[90856],{3905:(e,t,r)=>{r.d(t,{Zo:()=>p,kt:()=>m});var n=r(67294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var l=n.createContext({}),c=function(e){var t=n.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},p=function(e){var t=c(e.components);return n.createElement(l.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},u=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,l=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),u=c(r),m=o,f=u["".concat(l,".").concat(m)]||u[m]||d[m]||a;return r?n.createElement(f,i(i({ref:t},p),{},{components:r})):n.createElement(f,i({ref:t},p))}));function m(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=u;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:o,i[1]=s;for(var c=2;c<a;c++)i[c]=r[c];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}u.displayName="MDXCreateElement"},20658:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>d,frontMatter:()=>a,metadata:()=>s,toc:()=>c});var n=r(87462),o=(r(67294),r(3905));const a={id:"spec_ordering",title:"Spec Ordering",slug:"spec-ordering.html"},i=void 0,s={unversionedId:"framework/spec_ordering",id:"version-5.4.x/framework/spec_ordering",title:"Spec Ordering",description:"By default, the ordering of Spec classes is not defined. This means they are essentially random, in whatever order the discovery mechanism finds them.",source:"@site/versioned_docs/version-5.4.x/framework/spec_ordering.md",sourceDirName:"framework",slug:"/framework/spec-ordering.html",permalink:"/docs/5.4.x/framework/spec-ordering.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.4.x/framework/spec_ordering.md",tags:[],version:"5.4.x",frontMatter:{id:"spec_ordering",title:"Spec Ordering",slug:"spec-ordering.html"},sidebar:"framework",previous:{title:"Jacoco",permalink:"/docs/5.4.x/framework/integrations/jacoco.html"},next:{title:"Test Ordering",permalink:"/docs/5.4.x/framework/test-ordering.html"}},l={},c=[{value:"Annotated Example",id:"annotated-example",level:3}],p={toc:c};function d(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},p,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"By default, the ordering of Spec classes is not defined. This means they are essentially random, in whatever order the discovery mechanism finds them."),(0,o.kt)("p",null,"This is often sufficient, but if we need control over the execution order of specs, we can do this by specifying the order in ",(0,o.kt)("a",{parentName:"p",href:"/docs/5.4.x/framework/project-config.html"},"project config"),"."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MyConfig: AbstractProjectConfig() {\n    override val specExecutionOrder = ...\n}\n")),(0,o.kt)("p",null,"There are several options."),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("inlineCode",{parentName:"p"},"Undefined")," - This is the default. The order of specs is undefined and will execute in the order they are discovered at runtime. Eg either from JVM classpath discovery, or the order they appear in javascript files.")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("inlineCode",{parentName:"p"},"Lexicographic")," - Specs are ordered lexicographically.")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("inlineCode",{parentName:"p"},"Random")," - Specs are explicitly executed in a random order.")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("p",{parentName:"li"},(0,o.kt)("inlineCode",{parentName:"p"},"Annotated")," - Specs are ordered using the ",(0,o.kt)("inlineCode",{parentName:"p"},"@Order"),' annotation added at the class level, with lowest values executed first. Any specs without such an annotation are considered "last".\nThis option only works on the JVM. Any ties will be broken arbitrarily.'))),(0,o.kt)("h3",{id:"annotated-example"},"Annotated Example"),(0,o.kt)("p",null,"Given the following specs annoated with @Order."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"@Order(1)\nclass FooTest : FunSpec() { }\n\n@Order(0)\nclass BarTest: FunSpec() {}\n\n@Order(1)\nclass FarTest : FunSpec() { }\n\nclass BooTest : FunSpec() {}\n")),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"BarTest")," will be executed first, as it has the lowest order value. ",(0,o.kt)("inlineCode",{parentName:"p"},"FooTest")," and ",(0,o.kt)("inlineCode",{parentName:"p"},"FarTest")," will be executed next, as they have the next lowest order values, although their values are both 1 so the order between them is undefined. Finally, ",(0,o.kt)("inlineCode",{parentName:"p"},"BooTest")," will execute last, as it has no annotation."))}d.isMDXComponent=!0}}]);