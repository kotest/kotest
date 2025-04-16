"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[69494],{3905:(e,t,n)=>{n.d(t,{Zo:()=>u,kt:()=>m});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},l=Object.keys(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var o=a.createContext({}),p=function(e){var t=a.useContext(o),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},u=function(e){var t=p(e.components);return a.createElement(o.Provider,{value:t},e.children)},g={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},d=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,l=e.originalType,o=e.parentName,u=i(e,["components","mdxType","originalType","parentName"]),d=p(n),m=r,c=d["".concat(o,".").concat(m)]||d[m]||g[m]||l;return n?a.createElement(c,s(s({ref:t},u),{},{components:n})):a.createElement(c,s({ref:t},u))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var l=n.length,s=new Array(l);s[0]=d;var i={};for(var o in t)hasOwnProperty.call(t,o)&&(i[o]=t[o]);i.originalType=e,i.mdxType="string"==typeof e?e:r,s[1]=i;for(var p=2;p<l;p++)s[p]=n[p];return a.createElement.apply(null,s)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},16746:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>o,contentTitle:()=>s,default:()=>g,frontMatter:()=>l,metadata:()=>i,toc:()=>p});var a=n(87462),r=(n(67294),n(3905));const l={id:"tags",title:"Grouping Tests with Tags",slug:"tags.html",sidebar_label:"Grouping Tests"},s=void 0,i={unversionedId:"framework/tags",id:"version-5.2.x/framework/tags",title:"Grouping Tests with Tags",description:"Sometimes you don't want to run all tests and Kotest provides tags to be able to determine which",source:"@site/versioned_docs/version-5.2.x/framework/tags.md",sourceDirName:"framework",slug:"/framework/tags.html",permalink:"/docs/5.2.x/framework/tags.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.2.x/framework/tags.md",tags:[],version:"5.2.x",frontMatter:{id:"tags",title:"Grouping Tests with Tags",slug:"tags.html",sidebar_label:"Grouping Tests"},sidebar:"framework",previous:{title:"Test Ordering",permalink:"/docs/5.2.x/framework/test-ordering.html"},next:{title:"Closing resources automatically",permalink:"/docs/5.2.x/framework/autoclose.html"}},o={},p=[{value:"Marking Tests",id:"marking-tests",level:2},{value:"Running with Tags",id:"running-with-tags",level:2},{value:"Tag Expression Operators",id:"tag-expression-operators",level:2},{value:"Tagging All Tests",id:"tagging-all-tests",level:2},{value:"Tagging a Spec",id:"tagging-a-spec",level:2},{value:"Gradle",id:"gradle",level:2}],u={toc:p};function g(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,a.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Sometimes you don't want to run all tests and Kotest provides tags to be able to determine which\ntests are executed at runtime. Tags are objects inheriting from ",(0,r.kt)("inlineCode",{parentName:"p"},"io.kotest.core.Tag"),"."),(0,r.kt)("p",null,"For example, to group tests by operating system you could define the following tags:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"object Linux : Tag()\nobject Windows: Tag()\n")),(0,r.kt)("p",null,"Alternatively, tags can be defined using the ",(0,r.kt)("inlineCode",{parentName:"p"},"NamedTag")," class. When using this class, observe the following rules:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"A tag must not be null or blank."),(0,r.kt)("li",{parentName:"ul"},"A tag must not contain whitespace."),(0,r.kt)("li",{parentName:"ul"},"A tag must not contain ISO control characters."),(0,r.kt)("li",{parentName:"ul"},"A tag must not contain any of the following characters:",(0,r.kt)("ul",{parentName:"li"},(0,r.kt)("li",{parentName:"ul"},"!: exclamation mark"),(0,r.kt)("li",{parentName:"ul"},"(: left paren"),(0,r.kt)("li",{parentName:"ul"},"): right paren"),(0,r.kt)("li",{parentName:"ul"},"&: ampersand"),(0,r.kt)("li",{parentName:"ul"},"|: pipe")))),(0,r.kt)("p",null,"For example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val tag = NamedTag("Linux")\n')),(0,r.kt)("h2",{id:"marking-tests"},"Marking Tests"),(0,r.kt)("p",null,"Test cases can then be marked with tags using the ",(0,r.kt)("inlineCode",{parentName:"p"},"config")," function:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'import io.kotest.specs.StringSpec\n\nclass MyTest : StringSpec() {\n  init {\n    "should run on Windows".config(tags = setOf(Windows)) {\n      // ...\n    }\n\n    "should run on Linux".config(tags = setOf(Linux)) {\n      // ...\n    }\n\n    "should run on Windows and Linux".config(tags = setOf(Windows, Linux)) {\n      // ...\n    }\n  }\n}\n')),(0,r.kt)("h2",{id:"running-with-tags"},"Running with Tags"),(0,r.kt)("p",null,"Then by invoking the test runner with a system property of ",(0,r.kt)("inlineCode",{parentName:"p"},"kotest.tags")," you can control which tests are run. The expression to be\npassed in is a simple boolean expression using boolean operators: ",(0,r.kt)("inlineCode",{parentName:"p"},"&"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"|"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"!"),", with parenthesis for association."),(0,r.kt)("p",null,"For example, ",(0,r.kt)("inlineCode",{parentName:"p"},"Tag1 & (Tag2 | Tag3)")),(0,r.kt)("p",null,"Provide the simple names of tag object (without package) when you run the tests.\nPlease pay attention to the use of upper case and lower case! If two tag objects have the same simple name (in different name spaces) they are treated as the same tag."),(0,r.kt)("p",null,"Example: To run only test tagged with ",(0,r.kt)("inlineCode",{parentName:"p"},"Linux"),", but not tagged with ",(0,r.kt)("inlineCode",{parentName:"p"},"Database"),", you would invoke\nGradle like this:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},'gradle test -Dkotest.tags="Linux & !Database"\n')),(0,r.kt)("p",null,"Tags can also be included/excluded in runtime (for example, if you're running a project configuration instead of properties) through the ",(0,r.kt)("inlineCode",{parentName:"p"},"RuntimeTagExtension"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'RuntimeTagExpressionExtension.expression = "Linux & !Database"\n')),(0,r.kt)("h2",{id:"tag-expression-operators"},"Tag Expression Operators"),(0,r.kt)("p",null,"Operators (in descending order of precedence)"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Operator"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"),(0,r.kt)("th",{parentName:"tr",align:null},"Example"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"!"),(0,r.kt)("td",{parentName:"tr",align:null},"not"),(0,r.kt)("td",{parentName:"tr",align:null},"!macos")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"&"),(0,r.kt)("td",{parentName:"tr",align:null},"and"),(0,r.kt)("td",{parentName:"tr",align:null},"linux & integration")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"|"),(0,r.kt)("td",{parentName:"tr",align:null},"or"),(0,r.kt)("td",{parentName:"tr",align:null},"windows ","|"," microservice")))),(0,r.kt)("h2",{id:"tagging-all-tests"},"Tagging All Tests"),(0,r.kt)("p",null,"You can add a tag to all tests in a spec using the tags function in the spec itself. For example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'class MyTestClass : FunSpec({\n\n  tags(Linux, Mysql)\n\n  test("my test") { } // automatically marked with the above tags\n})\n')),(0,r.kt)("admonition",{type:"caution"},(0,r.kt)("p",{parentName:"admonition"},"When tagging tests in this way, the spec class will still need to be instantiated in order to examine the tags on each test, because the test itself may define further tags.")),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},"If no root tests are active at runtime, the ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/framework/lifecycle-hooks.html"},"beforeSpec")," and ",(0,r.kt)("a",{parentName:"p",href:"/docs/5.2.x/framework/lifecycle-hooks.html"},"afterSpec")," callbacks will ",(0,r.kt)("em",{parentName:"p"},"not")," be invoked.")),(0,r.kt)("h2",{id:"tagging-a-spec"},"Tagging a Spec"),(0,r.kt)("p",null,"There are two annotations you can add to a spec class itself - @Tags and @RequiresTag - which accept one or more tag names as their arguments."),(0,r.kt)("p",null,"The first tag - @Tags - will be applied to all tests in the class, however this will only stop a spec from being instantiated if we can guarantee\nthat no tests would be executed (because a tag is being explicitly excluded)."),(0,r.kt)("p",null,"Consider the following example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'@Tags("Linux")\nclass MyTestClass : FunSpec({\n\n  tags(UnitTest)\n\n  beforeSpec { println("Before") }\n\n  test("A").config(tags = setOf(Mysql)) {}\n  test("B").config(tags = setOf(Postgres)) {}\n  test("C") {}\n})\n')),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Runtime Tags"),(0,r.kt)("th",{parentName:"tr",align:null},"Spec Created"),(0,r.kt)("th",{parentName:"tr",align:null},"Callbacks"),(0,r.kt)("th",{parentName:"tr",align:null},"Outcome"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=Linux"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"A, B, C are executed because all tests inherit the ",(0,r.kt)("inlineCode",{parentName:"td"},"Linux")," tag from the annotation")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=Linux & Mysql"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"A is executed only because all tests have the ",(0,r.kt)("inlineCode",{parentName:"td"},"Linux")," tag, but only A has the ",(0,r.kt)("inlineCode",{parentName:"td"},"Mysql")," tag")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=!Linux"),(0,r.kt)("td",{parentName:"tr",align:null},"no"),(0,r.kt)("td",{parentName:"tr",align:null},"no"),(0,r.kt)("td",{parentName:"tr",align:null},"No tests are executed, and the MyTestClass is not instantiated because we can exclude it based on the tags annotation")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=!UnitTest"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"no"),(0,r.kt)("td",{parentName:"tr",align:null},"No tests are executed because all tests inherit ",(0,r.kt)("inlineCode",{parentName:"td"},"UnitTest")," from the tags function. MyTestClass is instantiated in order to retrieve the tags defined in the class. The beforeSpec callback is not executed because there are no active tests.")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=Mysql"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"A is executed only, because that is the only test marked with ",(0,r.kt)("inlineCode",{parentName:"td"},"Mysql"))),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=!Mysql"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"B, C are executed only, because A is excluded by being marked with ",(0,r.kt)("inlineCode",{parentName:"td"},"Mysql"))),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"kotest.tags=Linux & !Mysql"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"yes"),(0,r.kt)("td",{parentName:"tr",align:null},"B, C are executed only, because all tests inherit ",(0,r.kt)("inlineCode",{parentName:"td"},"Linux")," from the annotation, but A is excluded by the ",(0,r.kt)("inlineCode",{parentName:"td"},"Mysql")," tag")))),(0,r.kt)("p",null,"The second tag - @RequiresTag - only checks that all the referenced tags are present and if not, will skip the spec."),(0,r.kt)("p",null,"For example, the following spec would be skipped and not instantiated unless the Linux and Mysql tags were\nspecified at runtime."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'@RequiresTag("Linux", "Mysql")\nclass MyTestClass : FunSpec()\n')),(0,r.kt)("admonition",{type:"note"},(0,r.kt)("p",{parentName:"admonition"},'Note that when you use these annotations you pass the tag string name, not the tag itself. This is due to Kotlin annotations only allow "primitive" arguments')),(0,r.kt)("h2",{id:"gradle"},"Gradle"),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Special attention is needed in your gradle configuration")),(0,r.kt)("p",null,"To use System Properties (-Dx=y), your gradle must be configured to propagate them to the test executors, and an extra configuration must be added to your tests:"),(0,r.kt)("p",null,"Groovy:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-groovy"},"test {\n    //... Other configurations ...\n    systemProperties = System.properties\n}\n")),(0,r.kt)("p",null,"Kotlin Gradle DSL:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"val test by tasks.getting(Test::class) {\n    // ... Other configurations ...\n    systemProperties = System.getProperties().associate { it.key.toString() to it.value }\n}\n")),(0,r.kt)("p",null,"This will guarantee that the system property is correctly read by the JVM."))}g.isMDXComponent=!0}}]);