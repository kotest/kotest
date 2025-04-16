"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[61866],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>d});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},s=Object.keys(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},p=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},f=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,s=e.originalType,l=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),f=c(n),d=o,m=f["".concat(l,".").concat(d)]||f[d]||u[d]||s;return n?r.createElement(m,a(a({ref:t},p),{},{components:n})):r.createElement(m,a({ref:t},p))}));function d(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var s=n.length,a=new Array(s);a[0]=f;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:o,a[1]=i;for(var c=2;c<s;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}f.displayName="MDXCreateElement"},31351:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>a,default:()=>u,frontMatter:()=>s,metadata:()=>i,toc:()=>c});var r=n(87462),o=(n(67294),n(3905));const s={id:"soft_assertions",title:"Soft Assertions",slug:"soft-assertions.html"},a=void 0,i={unversionedId:"assertions/soft_assertions",id:"version-5.7.x/assertions/soft_assertions",title:"Soft Assertions",description:"Normally, assertions like shouldBe throw an exception when they fail.",source:"@site/versioned_docs/version-5.7.x/assertions/soft_assertions.md",sourceDirName:"assertions",slug:"/assertions/soft-assertions.html",permalink:"/docs/5.7.x/assertions/soft-assertions.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.7.x/assertions/soft_assertions.md",tags:[],version:"5.7.x",frontMatter:{id:"soft_assertions",title:"Soft Assertions",slug:"soft-assertions.html"},sidebar:"assertions",previous:{title:"Clues",permalink:"/docs/5.7.x/assertions/clues.html"},next:{title:"Eventually",permalink:"/docs/5.7.x/assertions/eventually.html"}},l={},c=[],p={toc:c};function u(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"Normally, assertions like ",(0,o.kt)("inlineCode",{parentName:"p"},"shouldBe")," throw an exception when they fail.\nBut sometimes you want to perform multiple assertions in a test, and\nwould like to see all of the assertions that failed. Kotest provides\nthe ",(0,o.kt)("inlineCode",{parentName:"p"},"assertSoftly")," function for this purpose."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"assertSoftly {\n  foo shouldBe bar\n  foo should contain(baz)\n}\n")),(0,o.kt)("p",null,"If any assertions inside the block failed, the test will continue to\nrun. All failures will be reported in a single exception at the end of\nthe block."),(0,o.kt)("p",null,"Another version of ",(0,o.kt)("inlineCode",{parentName:"p"},"assertSoftly")," takes a test target and lambda with test target as its receiver."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'assertSoftly(foo) {\n    shouldNotEndWith("b")\n    length shouldBe 3\n}\n')),(0,o.kt)("p",null,"We can configure assert softly to be implicitly added to every test via ",(0,o.kt)("a",{parentName:"p",href:"/docs/5.7.x/framework/project-config.html"},"project config"),"."))}u.isMDXComponent=!0}}]);