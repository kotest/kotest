"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[82739],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>m});var r=n(67294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},s=Object.keys(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(r=0;r<s.length;r++)n=s[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},h={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},p=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,s=e.originalType,l=e.parentName,u=i(e,["components","mdxType","originalType","parentName"]),p=c(n),m=a,d=p["".concat(l,".").concat(m)]||p[m]||h[m]||s;return n?r.createElement(d,o(o({ref:t},u),{},{components:n})):r.createElement(d,o({ref:t},u))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var s=n.length,o=new Array(s);o[0]=p;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:a,o[1]=i;for(var c=2;c<s;c++)o[c]=n[c];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}p.displayName="MDXCreateElement"},44989:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>o,default:()=>h,frontMatter:()=>s,metadata:()=>i,toc:()=>c});var r=n(87462),a=(n(67294),n(3905));const s={id:"custom_matchers",title:"Custom Matchers",slug:"custom-matchers.html",sidebar_label:"Custom Matchers"},o=void 0,i={unversionedId:"assertions/custom_matchers",id:"version-5.3.x/assertions/custom_matchers",title:"Custom Matchers",description:"It is easy to define your own matchers in Kotest.",source:"@site/versioned_docs/version-5.3.x/assertions/custom.md",sourceDirName:"assertions",slug:"/assertions/custom-matchers.html",permalink:"/docs/5.3.x/assertions/custom-matchers.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.3.x/assertions/custom.md",tags:[],version:"5.3.x",frontMatter:{id:"custom_matchers",title:"Custom Matchers",slug:"custom-matchers.html",sidebar_label:"Custom Matchers"},sidebar:"assertions",previous:{title:"Matchers",permalink:"/docs/5.3.x/assertions/matchers.html"},next:{title:"Composed Matchers",permalink:"/docs/5.3.x/assertions/composed-matchers.html"}},l={},c=[{value:"Extension Variants",id:"extension-variants",level:2}],u={toc:c};function h(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"It is easy to define your own matchers in Kotest."),(0,a.kt)("p",null,"Simply extend the ",(0,a.kt)("inlineCode",{parentName:"p"},"Matcher<T>")," interface, where T is the type you wish to match against. The ",(0,a.kt)("inlineCode",{parentName:"p"},"Matcher")," interface\nspecifies one method, ",(0,a.kt)("inlineCode",{parentName:"p"},"test")," which returns an instance of ",(0,a.kt)("inlineCode",{parentName:"p"},"MatcherResult"),"."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"interface Matcher<in T> {\n  fun test(value: T): MatcherResult\n}\n")),(0,a.kt)("p",null,"This ",(0,a.kt)("inlineCode",{parentName:"p"},"MatcherResult")," type defines three methods - a boolean to indicate if the test passed or failed, and two failure\nmessages."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"interface MatcherResult {\n  fun passed(): Boolean\n  fun failureMessage(): String\n  fun negatedFailureMessage(): String\n}\n")),(0,a.kt)("p",null,"The first failure message is the message to the user if the matcher predicate failed. Usually you can include some\ndetails of the expected value and the actual value and how they differed. The second failure message is the message to\nthe user if the matcher predicate evaluated true in ",(0,a.kt)("em",{parentName:"p"},"negated")," mode. Here you usually indicate that you expected the\npredicate to fail."),(0,a.kt)("p",null,"The difference in those two messages will be clearer with an example. Let's consider writing a length matcher for\nstrings, to assert that a string has a required length. We will want our syntax to be something\nlike ",(0,a.kt)("inlineCode",{parentName:"p"},"str.shouldHaveLength(8)"),"."),(0,a.kt)("p",null,"Then the first message should be something like ",(0,a.kt)("inlineCode",{parentName:"p"},'"string had length 15 but we expected length 8"'),". The second message\nwould need to be something like ",(0,a.kt)("inlineCode",{parentName:"p"},'"string should not have length 8"')),(0,a.kt)("p",null,"First we build out our matcher type:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'fun haveLength(length: Int) = Matcher<String> {\n  return MatcherResult(\n    value.length == length,\n    { "string had length ${value.length} but we expected length $length" },\n    { "string should not have length $length" },\n  )\n}\n')),(0,a.kt)("p",null,"Notice that we wrap the error messages in a function call so we don't evaluate if not needed. This is important for\nerror messages that take some time to generate."),(0,a.kt)("p",null,"This matcher can then be passed to the ",(0,a.kt)("inlineCode",{parentName:"p"},"should")," and ",(0,a.kt)("inlineCode",{parentName:"p"},"shouldNot")," infix functions as follows:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'"hello foo" should haveLength(9)\n"hello bar" shouldNot haveLength(3)\n')),(0,a.kt)("h2",{id:"extension-variants"},"Extension Variants"),(0,a.kt)("p",null,"Usually, we want to define extension functions which invoke the matcher function for you and return the original value\nfor chaining. This is how Kotest structures the built in matchers, and Kotest adopts a shouldXYZ naming strategy. For\nexample:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"fun String.shouldHaveLength(length: Int): String {\n  this should haveLength(length)\n  return this\n}\n\nfun String.shouldNotHaveLength(length: Int): String {\n  this shouldNot haveLength(length)\n  return this\n}\n")),(0,a.kt)("p",null,"Then we can invoke these like:"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'"hello foo".shouldHaveLength(9)\n"hello bar".shouldNotHaveLength(3)\n')))}h.isMDXComponent=!0}}]);