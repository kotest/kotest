"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[14994],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>h});var s=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);t&&(s=s.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,s)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?r(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):r(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function a(e,t){if(null==e)return{};var n,s,o=function(e,t){if(null==e)return{};var n,s,o={},r=Object.keys(e);for(s=0;s<r.length;s++)n=r[s],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(s=0;s<r.length;s++)n=r[s],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=s.createContext({}),p=function(e){var t=s.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},c=function(e){var t=p(e.components);return s.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return s.createElement(s.Fragment,{},t)}},d=s.forwardRef((function(e,t){var n=e.components,o=e.mdxType,r=e.originalType,l=e.parentName,c=a(e,["components","mdxType","originalType","parentName"]),d=p(n),h=o,g=d["".concat(l,".").concat(h)]||d[h]||u[h]||r;return n?s.createElement(g,i(i({ref:t},c),{},{components:n})):s.createElement(g,i({ref:t},c))}));function h(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var r=n.length,i=new Array(r);i[0]=d;var a={};for(var l in t)hasOwnProperty.call(t,l)&&(a[l]=t[l]);a.originalType=e,a.mdxType="string"==typeof e?e:o,i[1]=a;for(var p=2;p<r;p++)i[p]=n[p];return s.createElement.apply(null,i)}return s.createElement.apply(null,n)}d.displayName="MDXCreateElement"},28698:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>u,frontMatter:()=>r,metadata:()=>a,toc:()=>p});var s=n(87462),o=(n(67294),n(3905));const r={id:"spring",title:"Spring",sidebar_label:"Spring",slug:"spring.html"},i=void 0,a={unversionedId:"extensions/spring",id:"version-5.9.x/extensions/spring",title:"Spring",description:"Kotest offers a Spring extension that allows you to test code that uses the Spring framework for dependency injection.",source:"@site/versioned_docs/version-5.9.x/extensions/spring.md",sourceDirName:"extensions",slug:"/extensions/spring.html",permalink:"/docs/extensions/spring.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.9.x/extensions/spring.md",tags:[],version:"5.9.x",frontMatter:{id:"spring",title:"Spring",sidebar_label:"Spring",slug:"spring.html"},sidebar:"extensions",previous:{title:"Introduction",permalink:"/docs/extensions/extensions.html"},next:{title:"Ktor",permalink:"/docs/extensions/ktor.html"}},l={},p=[{value:"Constructor Injection",id:"constructor-injection",level:3},{value:"TestContexts",id:"testcontexts",level:3},{value:"Test Method Callbacks",id:"test-method-callbacks",level:3},{value:"Final Classes",id:"final-classes",level:3}],c={toc:p};function u(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,s.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"Kotest offers a Spring extension that allows you to test code that uses the Spring framework for dependency injection."),(0,o.kt)("admonition",{type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"If you prefer to see an example rather than read docs, then there is a sample project using spring webflux ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/kotest/kotest-examples-spring-webflux"},"here"))),(0,o.kt)("p",null,"In order to use this extension, you need to add ",(0,o.kt)("inlineCode",{parentName:"p"},"io.kotest.extensions:kotest-extensions-spring")," module to your test compile path. The latest version can always be found on maven central ",(0,o.kt)("a",{parentName:"p",href:"https://search.maven.org/search?q=a:kotest-extensions-spring%20AND%20g:io.kotest.extensions"},"here"),"."),(0,o.kt)("p",null,"Note: The maven group id differs from the core test framework (io.kotest.extensions)."),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://search.maven.org/artifact/io.kotest.extensions/kotest-extensions-spring"},(0,o.kt)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-spring.svg?label=latest%20release"})),"\n",(0,o.kt)("a",{parentName:"p",href:"https://oss.sonatype.org/content/repositories/snapshots/io/kotest/extensions/kotest-extensions-spring/"},(0,o.kt)("img",{src:"https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest.extensions/kotest-extensions-spring.svg?label=latest%20snapshot"}))),(0,o.kt)("p",null,"The Spring extension requires you to activate it for all test classes, or per test class. To activate it globally,\nregister the ",(0,o.kt)("inlineCode",{parentName:"p"},"SpringExtension")," in ",(0,o.kt)("a",{parentName:"p",href:"/docs/framework/project-config.html"},"project config"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"class ProjectConfig : AbstractProjectConfig() {\n   override fun extensions() = listOf(SpringExtension)\n}\n")),(0,o.kt)("p",null,"To activate it per test class:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MyTestSpec : FunSpec() {\n   override fun extensions() = listOf(SpringExtension)\n}\n")),(0,o.kt)("p",null,"In order to let Spring know which configuration class to use, you must annotate your Spec classes with ",(0,o.kt)("inlineCode",{parentName:"p"},"@ContextConfiguration"),".\nThis should point to a class annotated with the Spring ",(0,o.kt)("inlineCode",{parentName:"p"},"@Configuration")," annotation. Alternatively, you can use\n",(0,o.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-framework/reference/testing/annotations/integration-spring/annotation-activeprofiles.html"},(0,o.kt)("inlineCode",{parentName:"a"},"@ActiveProfiles"))," to\npoint to a ",(0,o.kt)("a",{parentName:"p",href:"https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html"},"specific application context file"),"."),(0,o.kt)("admonition",{type:"note"},(0,o.kt)("p",{parentName:"admonition"},"In Kotest 4.3 and earlier, the Spring extension was called ",(0,o.kt)("inlineCode",{parentName:"p"},"SpringListener"),". This extension has now been deprecated in favour of ",(0,o.kt)("inlineCode",{parentName:"p"},"SpringExtension"),". The usage is the same, but the SpringExtension has more functionality.")),(0,o.kt)("h3",{id:"constructor-injection"},"Constructor Injection"),(0,o.kt)("p",null,"For constructor injection, Kotest automatically registers a ",(0,o.kt)("inlineCode",{parentName:"p"},"SpringAutowireConstructorExtension"),"\nwhen the spring module is added to the build, assuming auto scan is enabled (see ",(0,o.kt)("a",{parentName:"p",href:"../framework/project-config.html"},"Project Config"),"). If Auto scan is\ndisabled, you will need to manually load the extension in your Project config."),(0,o.kt)("p",null,"This extension will intercept each call to create a Spec instance\nand will autowire the beans declared in the primary constructor."),(0,o.kt)("p",null,"The following example is a test class which requires a service called ",(0,o.kt)("inlineCode",{parentName:"p"},"UserService")," in its primary constructor. This service\nclass is just a regular spring bean which has been annotated with @Component."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'@ContextConfiguration(classes = [(Components::class)])\nclass SpringAutowiredConstructorTest(service: UserService) : WordSpec() {\n  init {\n    "SpringExtension" should {\n      "have autowired the service" {\n        service.repository.findUser().name shouldBe "system_user"\n      }\n    }\n  }\n}\n')),(0,o.kt)("h3",{id:"testcontexts"},"TestContexts"),(0,o.kt)("p",null,"The Spring extensions makes available the ",(0,o.kt)("inlineCode",{parentName:"p"},"TestContextManager")," via the coroutine context that tests execute in. You can\ngain a handle to this instance through the ",(0,o.kt)("inlineCode",{parentName:"p"},"testContextManager()")," extension method."),(0,o.kt)("p",null,"From this you can get the ",(0,o.kt)("inlineCode",{parentName:"p"},"testContext")," that Spring is using."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MySpec(service: UserService) : WordSpec() {\n  init {\n    "SpringExtension" should {\n      "provide the test context manager" {\n         println("The context is " + testContextManager().testContext)\n      }\n    }\n  }\n}\n')),(0,o.kt)("h3",{id:"test-method-callbacks"},"Test Method Callbacks"),(0,o.kt)("p",null,"Spring has various test callbacks such as ",(0,o.kt)("inlineCode",{parentName:"p"},"beforeTestMethod")," that are based around the idea that tests are methods.\nThis assumption is fine for legacy test frameworks like JUnit but not applicable to modern test frameworks like Kotest where tests are functions."),(0,o.kt)("p",null,"Therefore, when using a ",(0,o.kt)("a",{parentName:"p",href:"/docs/framework/testing-styles.html"},"spec style")," that is nested, you can customize when the test method callbacks are fired.\nBy default, this is on the leaf node. You can set these to fire on the root nodes by passing a SpringTestLifecycleMode argument to the extension:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},"class ProjectConfig : AbstractProjectConfig() {\n   override fun extensions() = listOf(SpringTestExtension(SpringTestLifecycleMode.Root))\n}\n")),(0,o.kt)("h3",{id:"final-classes"},"Final Classes"),(0,o.kt)("p",null,"When using a final class, you may receive a warning from Kotest:"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"Using SpringListener on a final class. If any Spring annotation fails to work, try making this class open")),(0,o.kt)("p",null,"If you wish, you can disable this warning by setting the system property ",(0,o.kt)("inlineCode",{parentName:"p"},"kotest.listener.spring.ignore.warning")," to true."))}u.isMDXComponent=!0}}]);