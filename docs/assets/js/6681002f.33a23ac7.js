"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[82144],{3905:(t,e,n)=>{n.d(e,{Zo:()=>d,kt:()=>m});var a=n(67294);function r(t,e,n){return e in t?Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}):t[e]=n,t}function s(t,e){var n=Object.keys(t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(t);e&&(a=a.filter((function(e){return Object.getOwnPropertyDescriptor(t,e).enumerable}))),n.push.apply(n,a)}return n}function o(t){for(var e=1;e<arguments.length;e++){var n=null!=arguments[e]?arguments[e]:{};e%2?s(Object(n),!0).forEach((function(e){r(t,e,n[e])})):Object.getOwnPropertyDescriptors?Object.defineProperties(t,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(e){Object.defineProperty(t,e,Object.getOwnPropertyDescriptor(n,e))}))}return t}function l(t,e){if(null==t)return{};var n,a,r=function(t,e){if(null==t)return{};var n,a,r={},s=Object.keys(t);for(a=0;a<s.length;a++)n=s[a],e.indexOf(n)>=0||(r[n]=t[n]);return r}(t,e);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(t);for(a=0;a<s.length;a++)n=s[a],e.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(t,n)&&(r[n]=t[n])}return r}var i=a.createContext({}),p=function(t){var e=a.useContext(i),n=e;return t&&(n="function"==typeof t?t(e):o(o({},e),t)),n},d=function(t){var e=p(t.components);return a.createElement(i.Provider,{value:e},t.children)},u={inlineCode:"code",wrapper:function(t){var e=t.children;return a.createElement(a.Fragment,{},e)}},k=a.forwardRef((function(t,e){var n=t.components,r=t.mdxType,s=t.originalType,i=t.parentName,d=l(t,["components","mdxType","originalType","parentName"]),k=p(n),m=r,h=k["".concat(i,".").concat(m)]||k[m]||u[m]||s;return n?a.createElement(h,o(o({ref:e},d),{},{components:n})):a.createElement(h,o({ref:e},d))}));function m(t,e){var n=arguments,r=e&&e.mdxType;if("string"==typeof t||r){var s=n.length,o=new Array(s);o[0]=k;var l={};for(var i in e)hasOwnProperty.call(e,i)&&(l[i]=e[i]);l.originalType=t,l.mdxType="string"==typeof t?t:r,o[1]=l;for(var p=2;p<s;p++)o[p]=n[p];return a.createElement.apply(null,o)}return a.createElement.apply(null,n)}k.displayName="MDXCreateElement"},15568:(t,e,n)=>{n.r(e),n.d(e,{assets:()=>i,contentTitle:()=>o,default:()=>u,frontMatter:()=>s,metadata:()=>l,toc:()=>p});var a=n(87462),r=(n(67294),n(3905));const s={id:"ktor",title:"Ktor Matchers",slug:"ktor-matchers.html",sidebar_label:"Ktor"},o=void 0,l={unversionedId:"assertions/ktor",id:"version-5.2.x/assertions/ktor",title:"Ktor Matchers",description:"Code is kept on a separate repository and on a different group: io.kotest.extensions.",source:"@site/versioned_docs/version-5.2.x/assertions/ktor.md",sourceDirName:"assertions",slug:"/assertions/ktor-matchers.html",permalink:"/docs/5.2.x/assertions/ktor-matchers.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.2.x/assertions/ktor.md",tags:[],version:"5.2.x",frontMatter:{id:"ktor",title:"Ktor Matchers",slug:"ktor-matchers.html",sidebar_label:"Ktor"},sidebar:"assertions",previous:{title:"Json",permalink:"/docs/5.2.x/assertions/json-matchers.html"},next:{title:"Android",permalink:"/docs/5.2.x/assertions/android-matchers.html"}},i={},p=[{value:"Test Application Response",id:"test-application-response",level:3},{value:"HttpResponse",id:"httpresponse",level:3}],d={toc:p};function u(t){let{components:e,...n}=t;return(0,r.kt)("wrapper",(0,a.Z)({},d,n,{components:e,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Code is kept on a ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/kotest/kotest-assertions-ktor"},"separate repository")," and on a different group: ",(0,r.kt)("inlineCode",{parentName:"p"},"io.kotest.extensions"),"."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Full Dependency")),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"http://search.maven.org/#search%7Cga%7C1%7Ckotest-assertions-ktor"},(0,r.kt)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-assertions-ktor.svg?label=latest%20release"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-assertions-ktor/"},(0,r.kt)("img",{src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-assertions-ktor.svg?label=latest%20snapshot"}))),(0,r.kt)("blockquote",null,(0,r.kt)("p",{parentName:"blockquote"},(0,r.kt)("inlineCode",{parentName:"p"},'implementation("io.kotest.extensions:kotest-assertions-ktor:version")')),(0,r.kt)("p",{parentName:"blockquote"},(0,r.kt)("inlineCode",{parentName:"p"},'implementation "io.kotest.extensions:kotest-assertions-ktor:version"'))),(0,r.kt)("p",null,"Matchers for ",(0,r.kt)("a",{parentName:"p",href:"https://ktor.io/"},"Ktor")," are provided by the ",(0,r.kt)("inlineCode",{parentName:"p"},"kotest-assertions-ktor")," module."),(0,r.kt)("h3",{id:"test-application-response"},"Test Application Response"),(0,r.kt)("p",null,"The following matchers are used when testing via the ktor server testkit."),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Matcher"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveStatus(HttpStatusCode)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response had the given http status code")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveContent(content)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response has the given body")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveContentType(ContentType)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response has the given Content Type")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveHeader(name, value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given name=value header")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveCookie(name, value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given cookie")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveCacheControl(value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given cache control header")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveETag(value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given etag header")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"TestApplicationResponse.shouldHaveContentEncoding(value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given content encoding header")))),(0,r.kt)("h3",{id:"httpresponse"},"HttpResponse"),(0,r.kt)("p",null,"The following matchers can be used against responses from the ktor http client."),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Matcher"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveStatus(HttpStatusCode)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response had the given http status code")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveContentType(ContentType)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response has the given Content Type")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveHeader(name, value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given name=value header")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveVersion(HttpProtocolVersion)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response used the given protocol version")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveCacheControl(value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given cache control header")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveETag(value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given etag header")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"HttpResponse.shouldHaveContentEncoding(value)")),(0,r.kt)("td",{parentName:"tr",align:null},"Asserts that the response included the given content encoding header")))))}u.isMDXComponent=!0}}]);