"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[57814],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>m});var o=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},a=Object.keys(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var l=o.createContext({}),c=function(e){var t=o.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},p=function(e){var t=c(e.components);return o.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},y=o.forwardRef((function(e,t){var n=e.components,r=e.mdxType,a=e.originalType,l=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),y=c(n),m=r,k=y["".concat(l,".").concat(m)]||y[m]||u[m]||a;return n?o.createElement(k,s(s({ref:t},p),{},{components:n})):o.createElement(k,s({ref:t},p))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var a=n.length,s=new Array(a);s[0]=y;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:r,s[1]=i;for(var c=2;c<a;c++)s[c]=n[c];return o.createElement.apply(null,s)}return o.createElement.apply(null,n)}y.displayName="MDXCreateElement"},72677:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>s,default:()=>u,frontMatter:()=>a,metadata:()=>i,toc:()=>c});var o=n(87462),r=(n(67294),n(3905));const a={id:"mocks",title:"Mocking and Kotest",sidebar_label:"Mocking",slug:"mocking.html"},s=void 0,i={unversionedId:"framework/integrations/mocks",id:"version-5.6.x/framework/integrations/mocks",title:"Mocking and Kotest",description:"Kotest itself has no mock features. However, you can plug-in your favourite mocking library with ease!",source:"@site/versioned_docs/version-5.6.x/framework/integrations/mocks.md",sourceDirName:"framework/integrations",slug:"/framework/integrations/mocking.html",permalink:"/docs/5.6.x/framework/integrations/mocking.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.6.x/framework/integrations/mocks.md",tags:[],version:"5.6.x",frontMatter:{id:"mocks",title:"Mocking and Kotest",sidebar_label:"Mocking",slug:"mocking.html"},sidebar:"framework",previous:{title:"Retry",permalink:"/docs/5.6.x/assertions/retry.html"},next:{title:"Jacoco",permalink:"/docs/5.6.x/framework/integrations/jacoco.html"}},l={},c=[{value:"Option 1 - setup mocks before tests",id:"option-1---setup-mocks-before-tests",level:3},{value:"Option 2 - reset mocks after tests",id:"option-2---reset-mocks-after-tests",level:3},{value:"Positioning the listeners",id:"positioning-the-listeners",level:3},{value:"Option 3 - Tweak the IsolationMode",id:"option-3---tweak-the-isolationmode",level:3}],p={toc:c};function u(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,o.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Kotest itself has no mock features. However, you can plug-in your favourite mocking library with ease!"),(0,r.kt)("p",null,"Let's take for example ",(0,r.kt)("a",{parentName:"p",href:"https://mockk.io"},"mockk"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTest : FunSpec({\n\n    val repository = mockk<MyRepository>()\n    val target = MyService(repository)\n\n    test("Saves to repository") {\n        every { repository.save(any()) } just Runs\n        target.save(MyDataClass("a"))\n        verify(exactly = 1) { repository.save(MyDataClass("a")) }\n    }\n\n})\n')),(0,r.kt)("p",null,"This example works as expected, but what if we add more tests that use that ",(0,r.kt)("em",{parentName:"p"},"mockk"),"?"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTest : FunSpec({\n\n    val repository = mockk<MyRepository>()\n    val target = MyService(repository)\n\n    test("Saves to repository") {\n        every { repository.save(any()) } just Runs\n        target.save(MyDataClass("a"))\n        verify(exactly = 1) { repository.save(MyDataClass("a")) }\n    }\n\n    test("Saves to repository as well") {\n        every { repository.save(any()) } just Runs\n        target.save(MyDataClass("a"))\n        verify(exactly = 1) { repository.save(MyDataClass("a")) }\n    }\n\n})\n')),(0,r.kt)("p",null,"The above snippet will cause an exception!"),(0,r.kt)("blockquote",null,(0,r.kt)("p",{parentName:"blockquote"},"2 matching calls found, but needs at least 1 and at most 1 calls")),(0,r.kt)("p",null,"This will happen because the mocks are not restarted between invocations. By default, Kotest isolates tests by creating\n",(0,r.kt)("a",{parentName:"p",href:"/docs/5.6.x/framework/isolation-mode.html"},"a single instance of the spec")," for all the tests to run."),(0,r.kt)("p",null,"This leads to mocks being reused. But how can we fix this?"),(0,r.kt)("h3",{id:"option-1---setup-mocks-before-tests"},"Option 1 - setup mocks before tests"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTest : FunSpec({\n\n    lateinit var repository: MyRepository\n    lateinit var target: MyService\n\n    beforeTest {\n        repository = mockk()\n        target = MyService(repository)\n    }\n\n    test("Saves to repository") {\n        // ...\n    }\n\n    test("Saves to repository as well") {\n        // ...\n    }\n\n})\n')),(0,r.kt)("h3",{id:"option-2---reset-mocks-after-tests"},"Option 2 - reset mocks after tests"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTest : FunSpec({\n\n    val repository = mockk<MyRepository>()\n    val target = MyService(repository)\n\n    afterTest {\n        clearMocks(repository)\n    }\n\n    test("Saves to repository") {\n        // ...\n    }\n\n    test("Saves to repository as well") {\n        // ...\n    }\n\n})\n')),(0,r.kt)("h3",{id:"positioning-the-listeners"},"Positioning the listeners"),(0,r.kt)("p",null,"As for any function that is executed inside the Spec definition, you can place listeners at the end"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTest : FunSpec({\n\n    val repository = mockk<MyRepository>()\n    val target = MyService(repository)\n\n\n    test("Saves to repository") {\n        // ...\n    }\n\n    test("Saves to repository as well") {\n        // ...\n    }\n\n    afterTest {\n        clearMocks(repository)  // <---- End of file, better readability\n    }\n\n})\n')),(0,r.kt)("h3",{id:"option-3---tweak-the-isolationmode"},"Option 3 - Tweak the IsolationMode"),(0,r.kt)("p",null,"Depending on the usage, playing with the IsolationMode for a given Spec might be a good option as well.\nHead over to ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.6.x/framework/isolation-mode.html"},"isolation mode documentation")," if you want to understand it better."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTest : FunSpec({\n\n    val repository = mockk<MyRepository>()\n    val target = MyService(repository)\n\n\n    test("Saves to repository") {\n        // ...\n    }\n\n    test("Saves to repository as well") {\n        // ...\n    }\n\n    isolationMode = IsolationMode.InstancePerTest\n\n})\n')))}u.isMDXComponent=!0}}]);