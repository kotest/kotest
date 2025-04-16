"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[56823],{3905:(e,t,r)=>{r.d(t,{Zo:()=>u,kt:()=>f});var o=r(67294);function n(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,o)}return r}function l(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){n(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,o,n=function(e,t){if(null==e)return{};var r,o,n={},a=Object.keys(e);for(o=0;o<a.length;o++)r=a[o],t.indexOf(r)>=0||(n[r]=e[r]);return n}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)r=a[o],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(n[r]=e[r])}return n}var c=o.createContext({}),i=function(e){var t=o.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):l(l({},t),e)),r},u=function(e){var t=i(e.components);return o.createElement(c.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},m=o.forwardRef((function(e,t){var r=e.components,n=e.mdxType,a=e.originalType,c=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),m=i(r),f=n,d=m["".concat(c,".").concat(f)]||m[f]||p[f]||a;return r?o.createElement(d,l(l({ref:t},u),{},{components:r})):o.createElement(d,l({ref:t},u))}));function f(e,t){var r=arguments,n=t&&t.mdxType;if("string"==typeof e||n){var a=r.length,l=new Array(a);l[0]=m;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s.mdxType="string"==typeof e?e:n,l[1]=s;for(var i=2;i<a;i++)l[i]=r[i];return o.createElement.apply(null,l)}return o.createElement.apply(null,r)}m.displayName="MDXCreateElement"},15055:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>l,default:()=>p,frontMatter:()=>a,metadata:()=>s,toc:()=>i});var o=r(87462),n=(r(67294),r(3905));const a={id:"autoclose",title:"Closing resources automatically",slug:"autoclose.html"},l=void 0,s={unversionedId:"framework/autoclose",id:"version-5.2.x/framework/autoclose",title:"Closing resources automatically",description:"You can let Kotest close resources automatically after all tests have been run:",source:"@site/versioned_docs/version-5.2.x/framework/autoclose.md",sourceDirName:"framework",slug:"/framework/autoclose.html",permalink:"/docs/5.2.x/framework/autoclose.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.2.x/framework/autoclose.md",tags:[],version:"5.2.x",frontMatter:{id:"autoclose",title:"Closing resources automatically",slug:"autoclose.html"},sidebar:"framework",previous:{title:"Grouping Tests",permalink:"/docs/5.2.x/framework/tags.html"},next:{title:"Temporary Files",permalink:"/docs/5.2.x/framework/temporary-files"}},c={},i=[],u={toc:i};function p(e){let{components:t,...r}=e;return(0,n.kt)("wrapper",(0,o.Z)({},u,r,{components:t,mdxType:"MDXLayout"}),(0,n.kt)("p",null,"You can let Kotest close resources automatically after all tests have been run:"),(0,n.kt)("pre",null,(0,n.kt)("code",{parentName:"pre",className:"language-kotlin"},'class StringSpecExample : StringSpec() {\n\n  val reader = autoClose(StringReader("xyz"))\n\n  init {\n    "your test case" {\n      // use resource reader here\n    }\n  }\n}\n')),(0,n.kt)("p",null,"Resources that should be closed this way must implement ",(0,n.kt)("a",{parentName:"p",href:"https://docs.oracle.com/javase/7/docs/api/java/lang/AutoCloseable.html"},(0,n.kt)("inlineCode",{parentName:"a"},"java.lang.AutoCloseable")),". Closing is performed in\nreversed order of declaration after the return of the last spec interceptor."))}p.isMDXComponent=!0}}]);