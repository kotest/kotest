"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[67804],{3905:(t,e,n)=>{n.d(e,{Zo:()=>p,kt:()=>h});var a=n(67294);function r(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}function l(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(t);e&&(a=a.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,a)}return n}function s(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?l(Object(n),!0).forEach((function(e){r(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function i(t,e){if(null==t)return{};var n,a,r=function(t,e){if(null==t)return{};var n,a,r={},l=Object.keys(t);for(a=0;a<l.length;a++)n=l[a],e.indexOf(n)>=0||(r[n]=t[n]);return r}(t,e);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(t);for(a=0;a<l.length;a++)n=l[a],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(r[n]=t[n])}return r}var o=a.createContext({}),m=function(t){var e=a.useContext(o),n=e;return t&&(n="function"==typeof t?t(e):s(s({},e),t)),n},p=function(t){var e=m(t.components);return a.createElement(o.Provider,{value:e},t.children)},d={inlineCode:"code",wrapper:function(t){var e=t.children;return a.createElement(a.Fragment,{},e)}},u=a.forwardRef((function(t,e){var n=t.components,r=t.mdxType,l=t.originalType,o=t.parentName,p=i(t,["components","mdxType","originalType","parentName"]),u=m(n),h=r,c=u["".concat(o,".").concat(h)]||u[h]||d[h]||l;return n?a.createElement(c,s(s({ref:e},p),{},{components:n})):a.createElement(c,s({ref:e},p))}));function h(t,e){var n=arguments,r=e&&e.mdxType;if("string"==typeof t||r){var l=n.length,s=new Array(l);s[0]=u;var i={};for(var o in e)hasOwnProperty.call(e,o)&&(i[o]=e[o]);i.originalType=t,i.mdxType="string"==typeof t?t:r,s[1]=i;for(var m=2;m<l;m++)s[m]=n[m];return a.createElement.apply(null,s)}return a.createElement.apply(null,n)}u.displayName="MDXCreateElement"},8380:(t,e,n)=>{n.r(e),n.d(e,{assets:()=>o,contentTitle:()=>s,default:()=>d,frontMatter:()=>l,metadata:()=>i,toc:()=>m});var a=n(87462),r=(n(67294),n(3905));const l={title:"Jsoup Matchers",slug:"jsoup-matchers.html",sidebar_label:"Jsoup"},s=void 0,i={unversionedId:"assertions/jsoup",id:"version-5.2.x/assertions/jsoup",title:"Jsoup Matchers",description:"This page lists all current matchers in the KotlinTest jsoup matchers extension library. To use this library",source:"@site/versioned_docs/version-5.2.x/assertions/jsoup.md",sourceDirName:"assertions",slug:"/assertions/jsoup-matchers.html",permalink:"/docs/5.2.x/assertions/jsoup-matchers.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.2.x/assertions/jsoup.md",tags:[],version:"5.2.x",frontMatter:{title:"Jsoup Matchers",slug:"jsoup-matchers.html",sidebar_label:"Jsoup"},sidebar:"assertions",previous:{title:"Compiler",permalink:"/docs/5.2.x/assertions/compiler-matchers.html"}},o={},m=[],p={toc:m};function d(t){let{components:e,...n}=t;return(0,r.kt)("wrapper",(0,a.Z)({},p,n,{components:e,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"This page lists all current matchers in the KotlinTest jsoup matchers extension library. To use this library\nyou need to add ",(0,r.kt)("inlineCode",{parentName:"p"},"kotlintest-assertions-jsoup")," to your build."),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Element"),(0,r.kt)("th",{parentName:"tr",align:null}))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveChildWithTag(tag)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has a child with the given tag")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveText(text)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has the given text")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveAttribute(name)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has an attribute with the given name")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveAttributeValue(name, value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element have an attribute with the given value")))),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Elements"),(0,r.kt)("th",{parentName:"tr",align:null}))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"elements.shouldBePresent()")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the Elements object has some item")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"elements.shouldBePresent(n)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the Elements object has N items")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"elements.shouldBePresent(n)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the Elements object has N items")))),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"HTML"),(0,r.kt)("th",{parentName:"tr",align:null}))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveId(id)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has an attribute id with the given value")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveClass(class)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has the specified class")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveSrc(src)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has an attribute src with the given value")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveHref(href)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has an attribute href with the given value")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveElementWithId(id)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has a child with the given id")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"element.shouldHaveChildWithClass(id)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the element has a child with the given class")))))}d.isMDXComponent=!0}}]);