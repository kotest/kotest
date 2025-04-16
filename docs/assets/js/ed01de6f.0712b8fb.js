"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[54046],{3905:(e,t,r)=>{r.d(t,{Zo:()=>p,kt:()=>m});var o=r(67294);function n(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,o)}return r}function c(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){n(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function i(e,t){if(null==e)return{};var r,o,n=function(e,t){if(null==e)return{};var r,o,n={},a=Object.keys(e);for(o=0;o<a.length;o++)r=a[o],t.indexOf(r)>=0||(n[r]=e[r]);return n}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)r=a[o],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(n[r]=e[r])}return n}var l=o.createContext({}),s=function(e){var t=o.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):c(c({},t),e)),r},p=function(e){var t=s(e.components);return o.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},d=o.forwardRef((function(e,t){var r=e.components,n=e.mdxType,a=e.originalType,l=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),d=s(r),m=n,f=d["".concat(l,".").concat(m)]||d[m]||u[m]||a;return r?o.createElement(f,c(c({ref:t},p),{},{components:r})):o.createElement(f,c({ref:t},p))}));function m(e,t){var r=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var a=r.length,c=new Array(a);c[0]=d;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:n,c[1]=i;for(var s=2;s<a;s++)c[s]=r[s];return o.createElement.apply(null,c)}return o.createElement.apply(null,r)}d.displayName="MDXCreateElement"},70001:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>l,contentTitle:()=>c,default:()=>u,frontMatter:()=>a,metadata:()=>i,toc:()=>s});var o=r(87462),n=(r(67294),r(3905));const a={id:"jacoco",title:"Jacoco",sidebar_label:"Jacoco",slug:"jacoco.html"},c=void 0,i={unversionedId:"framework/integrations/jacoco",id:"version-5.7.x/framework/integrations/jacoco",title:"Jacoco",description:"Kotest integrates with Jacoco for code coverage in the standard gradle way.",source:"@site/versioned_docs/version-5.7.x/framework/integrations/jacoco.md",sourceDirName:"framework/integrations",slug:"/framework/integrations/jacoco.html",permalink:"/docs/5.7.x/framework/integrations/jacoco.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.7.x/framework/integrations/jacoco.md",tags:[],version:"5.7.x",frontMatter:{id:"jacoco",title:"Jacoco",sidebar_label:"Jacoco",slug:"jacoco.html"},sidebar:"framework",previous:{title:"Mocking",permalink:"/docs/5.7.x/framework/integrations/mocking.html"},next:{title:"Spec Ordering",permalink:"/docs/5.7.x/framework/spec-ordering.html"}},l={},s=[],p={toc:s};function u(e){let{components:t,...r}=e;return(0,n.kt)("wrapper",(0,o.Z)({},p,r,{components:t,mdxType:"MDXLayout"}),(0,n.kt)("p",null,"Kotest integrates with ",(0,n.kt)("a",{parentName:"p",href:"https://www.eclemma.org/jacoco/"},"Jacoco")," for code coverage in the standard gradle way.\nYou can read gradle installation instructions ",(0,n.kt)("a",{parentName:"p",href:"https://docs.gradle.org/current/userguide/jacoco_plugin.html"},"here"),"."),(0,n.kt)("ol",null,(0,n.kt)("li",{parentName:"ol"},"In gradle, add jacoco to your plugins.")),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"plugins {\n   ...\n   jacoco\n   ...\n}\n")),(0,n.kt)("ol",{start:2},(0,n.kt)("li",{parentName:"ol"},"Configure jacoco")),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"jacoco {\n    toolVersion = \"0.8.7\"\n    reportsDirectory = layout.buildDirectory.dir('customJacocoReportDir') // optional\n}\n")),(0,n.kt)("ol",{start:3},(0,n.kt)("li",{parentName:"ol"},"Add the jacoco XML report task.")),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"tasks.jacocoTestReport {\n    dependsOn(tasks.test)\n    reports {\n        xml.required.set(true)\n    }\n}\n")),(0,n.kt)("ol",{start:4},(0,n.kt)("li",{parentName:"ol"},"Change tests task to depend on jacoco.")),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},"tasks.test {\n  ...\n  finalizedBy(tasks.jacocoTestReport)\n}\n")),(0,n.kt)("p",null,"Now when you run ",(0,n.kt)("inlineCode",{parentName:"p"},"test"),", the Jacoco report files should be generated in ",(0,n.kt)("inlineCode",{parentName:"p"},"$buildDir/reports/jacoco"),"."),(0,n.kt)("admonition",{type:"note"},(0,n.kt)("p",{parentName:"admonition"},"You may need to apply the jacoco plugin to each submodule if you have a multi module project.")))}u.isMDXComponent=!0}}]);