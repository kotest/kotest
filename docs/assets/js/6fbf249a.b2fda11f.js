"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[73424],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>m});var s=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);t&&(s=s.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,s)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?r(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):r(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function a(e,t){if(null==e)return{};var n,s,o=function(e,t){if(null==e)return{};var n,s,o={},r=Object.keys(e);for(s=0;s<r.length;s++)n=r[s],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(s=0;s<r.length;s++)n=r[s],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=s.createContext({}),l=function(e){var t=s.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=l(e.components);return s.createElement(c.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return s.createElement(s.Fragment,{},t)}},d=s.forwardRef((function(e,t){var n=e.components,o=e.mdxType,r=e.originalType,c=e.parentName,p=a(e,["components","mdxType","originalType","parentName"]),d=l(n),m=o,h=d["".concat(c,".").concat(m)]||d[m]||u[m]||r;return n?s.createElement(h,i(i({ref:t},p),{},{components:n})):s.createElement(h,i({ref:t},p))}));function m(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var r=n.length,i=new Array(r);i[0]=d;var a={};for(var c in t)hasOwnProperty.call(t,c)&&(a[c]=t[c]);a.originalType=e,a.mdxType="string"==typeof e?e:o,i[1]=a;for(var l=2;l<r;l++)i[l]=n[l];return s.createElement.apply(null,i)}return s.createElement.apply(null,n)}d.displayName="MDXCreateElement"},36213:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>u,frontMatter:()=>r,metadata:()=>a,toc:()=>l});var s=n(87462),o=(n(67294),n(3905));const r={id:"test_containers_46",title:"Testcontainers",sidebar_label:"Testcontainers",slug:"test_containers_46.html"},i=void 0,a={unversionedId:"extensions/test_containers_46",id:"version-5.4.x/extensions/test_containers_46",title:"Testcontainers",description:"Testcontainers",source:"@site/versioned_docs/version-5.4.x/extensions/test_containers_46x.md",sourceDirName:"extensions",slug:"/extensions/test_containers_46.html",permalink:"/docs/5.4.x/extensions/test_containers_46.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.4.x/extensions/test_containers_46x.md",tags:[],version:"5.4.x",frontMatter:{id:"test_containers_46",title:"Testcontainers",sidebar_label:"Testcontainers",slug:"test_containers_46.html"}},c={},l=[{value:"Testcontainers",id:"testcontainers",level:2}],p={toc:l};function u(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,s.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"testcontainers"},"Testcontainers"),(0,o.kt)("p",null,"The ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/testcontainers/testcontainers-java"},"Testcontainers")," project provides lightweight, ephemeral instances of common databases, elasticsearch, kafka, Selenium web browsers, or anything else that can run in a Docker container, ideal for use inside tests."),(0,o.kt)("p",null,"This module provides integration for using Testcontainers with kotest.\nTo use add the below dependency to your Gradle build file."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-groovy"},"io.kotest.extensions:kotest-extensions-testcontainers:${kotest.version}\n")),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-testcontainers"},(0,o.kt)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-testcontainers.svg?label=latest%20release"})),"\n",(0,o.kt)("a",{parentName:"p",href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-testcontainers/"},(0,o.kt)("img",{src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-testcontainers.svg?label=latest%20snapshot"}))),(0,o.kt)("p",null,"Note: The group id is different (io.kotest.extensions) from the main kotest dependencies (io.kotest)."),(0,o.kt)("p",null,"For Maven, you will need these dependencies:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-xml"},"<dependency>\n    <groupId>io.kotest.extensions</groupId>\n    <artifactId>kotest-extensions-testcontainers</artifactId>\n    <version>${kotest.version}</version>\n    <scope>test</scope>\n</dependency>\n")),(0,o.kt)("p",null,"Having these dependencies in test classpath will bring extension methods into scope which let you convert any ",(0,o.kt)("inlineCode",{parentName:"p"},"Startable")," such as a ",(0,o.kt)("inlineCode",{parentName:"p"},"DockerContainer")," into a kotest ",(0,o.kt)("inlineCode",{parentName:"p"},"TestListener"),", which you can register with Kotest and then Kotest will manage lifecycle of container for you."),(0,o.kt)("p",null,"For example:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'import io.kotest.core.spec.style.FunSpec\nimport io.kotest.extensions.testcontainers.perTest\nimport org.testcontainers.containers.GenericContainer\n\nclass DatabaseRepositoryTest : FunSpec({\n   val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")\n   listener(redisContainer.perTest()) //converts container to listener and registering it with Kotest.\n\n   test("some test which assume to have redis container running") {\n      //\n   }\n})\n')),(0,o.kt)("p",null,"In above example, the ",(0,o.kt)("inlineCode",{parentName:"p"},"perTest()")," extension method converts the container into a ",(0,o.kt)("inlineCode",{parentName:"p"},"TestListener"),", which starts the\nredis container before each test and stops it after test. Similarly if you want to reuse the container for all tests\nin a single spec class you can use ",(0,o.kt)("inlineCode",{parentName:"p"},"perSpec()")," extension method, which converts the container into a ",(0,o.kt)("inlineCode",{parentName:"p"},"TestListener"),"\nwhich starts the container before running any test in the spec, and stops it after all tests, thus a single container is\nused by all tests in spec class."))}u.isMDXComponent=!0}}]);