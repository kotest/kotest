"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[82896],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>g});var s=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);t&&(s=s.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,s)}return n}function r(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function a(e,t){if(null==e)return{};var n,s,o=function(e,t){if(null==e)return{};var n,s,o={},i=Object.keys(e);for(s=0;s<i.length;s++)n=i[s],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(s=0;s<i.length;s++)n=i[s],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var l=s.createContext({}),p=function(e){var t=s.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):r(r({},t),e)),n},c=function(e){var t=p(e.components);return s.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return s.createElement(s.Fragment,{},t)}},d=s.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,l=e.parentName,c=a(e,["components","mdxType","originalType","parentName"]),d=p(n),g=o,m=d["".concat(l,".").concat(g)]||d[g]||u[g]||i;return n?s.createElement(m,r(r({ref:t},c),{},{components:n})):s.createElement(m,r({ref:t},c))}));function g(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,r=new Array(i);r[0]=d;var a={};for(var l in t)hasOwnProperty.call(t,l)&&(a[l]=t[l]);a.originalType=e,a.mdxType="string"==typeof e?e:o,r[1]=a;for(var p=2;p<i;p++)r[p]=n[p];return s.createElement.apply(null,r)}return s.createElement.apply(null,n)}d.displayName="MDXCreateElement"},12275:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>r,default:()=>u,frontMatter:()=>i,metadata:()=>a,toc:()=>p});var s=n(87462),o=(n(67294),n(3905));const i={id:"pitest",title:"Pitest",sidebar_label:"Pitest",slug:"pitest.html"},r=void 0,a={unversionedId:"extensions/pitest",id:"version-5.6.x/extensions/pitest",title:"Pitest",description:"The Mutation Testing tool Pitest is integrated with Kotest via an extension module.",source:"@site/versioned_docs/version-5.6.x/extensions/pitest.md",sourceDirName:"extensions",slug:"/extensions/pitest.html",permalink:"/docs/5.6.x/extensions/pitest.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.6.x/extensions/pitest.md",tags:[],version:"5.6.x",frontMatter:{id:"pitest",title:"Pitest",sidebar_label:"Pitest",slug:"pitest.html"},sidebar:"extensions",previous:{title:"Test Clock",permalink:"/docs/5.6.x/extensions/test_clock.html"},next:{title:"BlockHound",permalink:"/docs/5.6.x/extensions/blockhound.html"}},l={},p=[{value:"Gradle configuration",id:"gradle-configuration",level:2},{value:"Maven configuration",id:"maven-configuration",level:2}],c={toc:p};function u(e){let{components:t,...n}=e;return(0,o.kt)("wrapper",(0,s.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"The Mutation Testing tool ",(0,o.kt)("a",{parentName:"p",href:"https://pitest.org/"},"Pitest")," is integrated with Kotest via an extension module."),(0,o.kt)("h2",{id:"gradle-configuration"},"Gradle configuration"),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"http://search.maven.org/#search%7Cga%7C1%7Ckotest-extensions-pitest"},(0,o.kt)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-pitest.svg?label=latest%20release"}))),(0,o.kt)("p",null,"After ",(0,o.kt)("a",{parentName:"p",href:"https://gradle-pitest-plugin.solidsoft.info/"},"configuring")," Pitest,\nadd the ",(0,o.kt)("inlineCode",{parentName:"p"},"io.kotest.extensions:kotest-extensions-pitest")," module to your dependencies as well:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'    testImplementation("io.kotest.extensions:kotest-extensions-pitest:<version>")\n')),(0,o.kt)("p",null,"Note: Since pitest is an extension, we use a different maven group name (io.kotest.extensions) from the core modules."),(0,o.kt)("p",null,"After doing that, we need to inform Pitest that we're going to use ",(0,o.kt)("inlineCode",{parentName:"p"},"Kotest")," as a ",(0,o.kt)("inlineCode",{parentName:"p"},"testPlugin"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'// Assuming that you have already configured the Gradle/Maven extension\nconfigure<PitestPluginExtension> {\n    // testPlugin.set("Kotest")    // needed only with old PIT <1.6.7, otherwise having kotest-extensions-pitest on classpath is enough\n    targetClasses.set(listOf("my.company.package.*"))\n}\n')),(0,o.kt)("p",null,"This should set everything up, and running ",(0,o.kt)("inlineCode",{parentName:"p"},"./gradlew pitest")," will generate reports in the way you configured."),(0,o.kt)("h2",{id:"maven-configuration"},"Maven configuration"),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"http://search.maven.org/#search%7Cga%7C1%7Ckotest-extensions-pitest"},(0,o.kt)("img",{src:"https://img.shields.io/maven-central/v/io.kotest.extensions/kotest-extensions-pitest.svg?label=latest%20release"}))),(0,o.kt)("p",null,"First of all, you need to configure the ",(0,o.kt)("a",{parentName:"p",href:"https://pitest.org/quickstart/maven/"},"Maven Pitest plugin"),":"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-xml"},"<plugin>\n    <groupId>org.pitest</groupId>\n    <artifactId>pitest-maven</artifactId>\n    <version>${pitest-maven.version}</version>\n    <configuration>\n        <targetClasses>...</targetClasses>\n        <coverageThreshold>...</coverageThreshold>\n        ... other configurations as needed        \n    </configuration>\n</plugin>\n")),(0,o.kt)("p",null,"Then add the dependency on Pitest Kotest extension:"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-xml"},"<dependencies>\n  ... the other Kotest dependencies like kotest-runner-junit5-jvm \n  <dependency>\n    <groupId>io.kotest.extensions</groupId>\n    <artifactId>kotest-extensions-pitest</artifactId>\n    <version>${kotest-extensions-pitest.version}</version>\n    <scope>test</scope>\n  </dependency>\n</dependencies>\n")),(0,o.kt)("p",null,"This should be enough to be able to run Pitest and get the reports as described in the ",(0,o.kt)("a",{parentName:"p",href:"https://pitest.org/quickstart/maven/"},"Maven Pitest plugin"),"."))}u.isMDXComponent=!0}}]);