"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[20189],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>m});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=r.createContext({}),c=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=c(e.components);return r.createElement(l.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,l=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),d=c(n),m=o,f=d["".concat(l,".").concat(m)]||d[m]||p[m]||i;return n?r.createElement(f,a(a({ref:t},u),{},{components:n})):r.createElement(f,a({ref:t},u))}));function m(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=d;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s.mdxType="string"==typeof e?e:o,a[1]=s;for(var c=2;c<i;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},90459:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>a,default:()=>p,frontMatter:()=>i,metadata:()=>s,toc:()=>c});var r=n(87462),o=(n(67294),n(3905));const i={id:"instant",title:"Current Instant Listeners",sidebar_label:"Current Instant Listeners",slug:"instant.html"},a=void 0,s={unversionedId:"extensions/instant",id:"version-5.6.x/extensions/instant",title:"Current Instant Listeners",description:"Since Kotest 5.6.0, Current instant listeners are located in the artifact io.kotest$.",source:"@site/versioned_docs/version-5.6.x/extensions/instant.md",sourceDirName:"extensions",slug:"/extensions/instant.html",permalink:"/docs/5.6.x/extensions/instant.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.6.x/extensions/instant.md",tags:[],version:"5.6.x",frontMatter:{id:"instant",title:"Current Instant Listeners",sidebar_label:"Current Instant Listeners",slug:"instant.html"},sidebar:"extensions",previous:{title:"Allure",permalink:"/docs/5.6.x/extensions/allure.html"},next:{title:"Koin",permalink:"/docs/5.6.x/extensions/koin.html"}},l={},c=[{value:"Current instant listeners",id:"current-instant-listeners",level:3}],u={toc:c};function p(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("admonition",{type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"Since Kotest 5.6.0, Current instant listeners are located in the artifact ",(0,o.kt)("inlineCode",{parentName:"p"},"io.kotest:kotest-extensions-now:${kotest-version}"),"."),(0,o.kt)("p",{parentName:"admonition"},"Add it as a dependency to use any of the functionality mentioned below.")),(0,o.kt)("h3",{id:"current-instant-listeners"},"Current instant listeners"),(0,o.kt)("p",null,"Sometimes you may want to use the ",(0,o.kt)("inlineCode",{parentName:"p"},"now")," static functions located in ",(0,o.kt)("inlineCode",{parentName:"p"},"java.time")," classes for multiple reasons, such as setting the creation date of an entity"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"data class MyEntity(creationDate: LocalDateTime = LocalDateTime.now())"),"."),(0,o.kt)("p",null,"But what to do when you want to test that value? ",(0,o.kt)("inlineCode",{parentName:"p"},"now")," will be different\neach time you call it!"),(0,o.kt)("p",null,"For that, Kotest provides ",(0,o.kt)("inlineCode",{parentName:"p"},"ConstantNowListener")," and ",(0,o.kt)("inlineCode",{parentName:"p"},"withConstantNow")," functions."),(0,o.kt)("p",null,"While executing your code, your ",(0,o.kt)("inlineCode",{parentName:"p"},"now")," will always be the value that you want to test against."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"val foreverNow = LocalDateTime.now()\n\nwithConstantNow(foreverNow) {\n  LocalDateTime.now() shouldBe foreverNow\n  delay(10) // Code is taking a small amount of time to execute, but `now` changed!\n  LocalDateTime.now() shouldBe foreverNow\n}\n\n")),(0,o.kt)("p",null,"Or, with a listener for all the tests:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"  override fun listeners() = listOf(\n    ConstantNowTestListener(foreverNow)\n  )\n")),(0,o.kt)("admonition",{type:"caution"},(0,o.kt)("p",{parentName:"admonition"},(0,o.kt)("inlineCode",{parentName:"p"},"withContantNow")," and ",(0,o.kt)("inlineCode",{parentName:"p"},"ConstantNowTestListener")," are very sensitive to race conditions. Using them, mocks the static method ",(0,o.kt)("inlineCode",{parentName:"p"},"now")," which is global to the whole JVM instance,\nif you're using it while running test in parallel, the results may be inconsistent.")))}p.isMDXComponent=!0}}]);