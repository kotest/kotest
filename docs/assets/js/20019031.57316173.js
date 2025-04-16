"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[58202],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>k});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var a=r.createContext({}),l=function(e){var t=r.useContext(a),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},p=function(e){var t=l(e.components);return r.createElement(a.Provider,{value:t},e.children)},m={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},u=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,a=e.parentName,p=c(e,["components","mdxType","originalType","parentName"]),u=l(n),k=o,d=u["".concat(a,".").concat(k)]||u[k]||m[k]||i;return n?r.createElement(d,s(s({ref:t},p),{},{components:n})):r.createElement(d,s({ref:t},p))}));function k(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,s=new Array(i);s[0]=u;var c={};for(var a in t)hasOwnProperty.call(t,a)&&(c[a]=t[a]);c.originalType=e,c.mdxType="string"==typeof e?e:o,s[1]=c;for(var l=2;l<i;l++)s[l]=n[l];return r.createElement.apply(null,s)}return r.createElement.apply(null,n)}u.displayName="MDXCreateElement"},20138:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>a,contentTitle:()=>s,default:()=>m,frontMatter:()=>i,metadata:()=>c,toc:()=>l});var r=n(87462),o=(n(67294),n(3905));const i={id:"wiremock",title:"WireMock",sidebar_label:"WireMock",slug:"wiremock.html"},s=void 0,c={unversionedId:"extensions/wiremock",id:"version-5.5.x/extensions/wiremock",title:"WireMock",description:"WireMock",source:"@site/versioned_docs/version-5.5.x/extensions/wiremock.md",sourceDirName:"extensions",slug:"/extensions/wiremock.html",permalink:"/docs/5.5.x/extensions/wiremock.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.5.x/extensions/wiremock.md",tags:[],version:"5.5.x",frontMatter:{id:"wiremock",title:"WireMock",sidebar_label:"WireMock",slug:"wiremock.html"},sidebar:"extensions",previous:{title:"Koin",permalink:"/docs/5.5.x/extensions/koin.html"},next:{title:"Test Clock",permalink:"/docs/5.5.x/extensions/test_clock.html"}},a={},l=[{value:"WireMock",id:"wiremock",level:2}],p={toc:l};function m(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"wiremock"},"WireMock"),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://github.com/tomakehurst/wiremock"},"WireMock")," is a library which provides HTTP response stubbing, matchable on\nURL, header and body content patterns etc."),(0,o.kt)("p",null,"Kotest provides a module ",(0,o.kt)("inlineCode",{parentName:"p"},"kotest-extensions-wiremock")," for integration with wiremock."),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-wiremock"},(0,o.kt)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-wiremock.svg?label=latest%20release"})),"\n",(0,o.kt)("a",{parentName:"p",href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-wiremock/"},(0,o.kt)("img",{src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-wiremock.svg?label=latest%20snapshot"}))),(0,o.kt)("p",null,"To begin, add the following dependency to your build:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre"},"io.kotest.extensions:kotest-extensions-wiremock:{version}\n")),(0,o.kt)("p",null,"Having this dependency in the classpath brings ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockListener")," into scope.\n",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockListener")," manages  the lifecycle of a ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockServer")," during your test."),(0,o.kt)("p",null,"For example:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'\nclass SomeTest : FunSpec({\n  val customerServiceServer = WireMockServer(9000)\n  listener(WireMockListener(customerServiceServer, ListenerMode.PER_SPEC))\n\n  test("let me get customer information") {\n    customerServiceServer.stubFor(\n      WireMock.get(WireMock.urlEqualTo("/customers/123"))\n        .willReturn(WireMock.ok())\n    )\n\n    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection\n    connection.responseCode shouldBe 200\n  }\n\n    //  ------------OTHER TEST BELOW ----------------\n})\n')),(0,o.kt)("p",null,"In above example we created an instance of ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockListener")," which starts a ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockServer")," before running the tests\nin the spec and stops it after completing all the tests in the spec."),(0,o.kt)("p",null,"You can use ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockServer.perSpec(customerServiceServer)")," to achieve same result."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'\nclass SomeTest : FunSpec({\n  val customerServiceServer = WireMockServer(9000)\n  listener(WireMockListener(customerServiceServer, ListenerMode.PER_TEST))\n\n  test("let me get customer information") {\n    customerServiceServer.stubFor(\n      WireMock.get(WireMock.urlEqualTo("/customers/123"))\n        .willReturn(WireMock.ok())\n    )\n\n    val connection = URL("http://localhost:9000/customers/123").openConnection() as HttpURLConnection\n    connection.responseCode shouldBe 200\n  }\n\n  //  ------------OTHER TEST BELOW ----------------\n})\n')),(0,o.kt)("p",null,"In above example we created an instance of ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockListener")," which starts a ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockServer")," before running every test\nin the spec and stops it after completing every test in the spec.\nYou can use ",(0,o.kt)("inlineCode",{parentName:"p"},"WireMockServer.perTest(customerServiceServer)")," to achieve same result."))}m.isMDXComponent=!0}}]);