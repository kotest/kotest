"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[41733],{3905:(e,t,n)=>{n.d(t,{Zo:()=>m,kt:()=>d});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function s(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?s(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):s(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},s=Object.keys(e);for(a=0;a<s.length;a++)n=s[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var s=Object.getOwnPropertySymbols(e);for(a=0;a<s.length;a++)n=s[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var l=a.createContext({}),c=function(e){var t=a.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},m=function(e){var t=c(e.components);return a.createElement(l.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},u=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,s=e.originalType,l=e.parentName,m=o(e,["components","mdxType","originalType","parentName"]),u=c(n),d=r,h=u["".concat(l,".").concat(d)]||u[d]||p[d]||s;return n?a.createElement(h,i(i({ref:t},m),{},{components:n})):a.createElement(h,i({ref:t},m))}));function d(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var s=n.length,i=new Array(s);i[0]=u;var o={};for(var l in t)hasOwnProperty.call(t,l)&&(o[l]=t[l]);o.originalType=e,o.mdxType="string"==typeof e?e:r,i[1]=o;for(var c=2;c<s;c++)i[c]=n[c];return a.createElement.apply(null,i)}return a.createElement.apply(null,n)}u.displayName="MDXCreateElement"},66471:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>p,frontMatter:()=>s,metadata:()=>o,toc:()=>c});var a=n(87462),r=(n(67294),n(3905));const s={title:"JSON Schema Matchers",slug:"json-schema-matchers.html",sidebar_label:"Schema matchers"},i=void 0,o={unversionedId:"assertions/json/schema",id:"version-5.3.x/assertions/json/schema",title:"JSON Schema Matchers",description:"| Matcher             | Description                                                                                                                                         | Targets       |",source:"@site/versioned_docs/version-5.3.x/assertions/json/schema.md",sourceDirName:"assertions/json",slug:"/assertions/json/json-schema-matchers.html",permalink:"/docs/5.3.x/assertions/json/json-schema-matchers.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.3.x/assertions/json/schema.md",tags:[],version:"5.3.x",frontMatter:{title:"JSON Schema Matchers",slug:"json-schema-matchers.html",sidebar_label:"Schema matchers"},sidebar:"assertions",previous:{title:"Matching content",permalink:"/docs/5.3.x/assertions/json/content-json-matchers.html"},next:{title:"Ktor",permalink:"/docs/5.3.x/assertions/ktor-matchers.html"}},l={},c=[{value:"Defining Schemas",id:"defining-schemas",level:2},{value:"Validating",id:"validating",level:2}],m={toc:c};function p(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,a.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Matcher"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Targets"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"shouldMatchSchema")),(0,r.kt)("td",{parentName:"tr",align:null},"Validates that a ",(0,r.kt)("inlineCode",{parentName:"td"},"String")," or ",(0,r.kt)("inlineCode",{parentName:"td"},"kotlinx.serialization.JsonElement")," matches a ",(0,r.kt)("inlineCode",{parentName:"td"},"JsonSchema"),". See description below for details on constructing schemas."),(0,r.kt)("td",{parentName:"tr",align:"left"},"Multiplatform")))),(0,r.kt)("h2",{id:"defining-schemas"},"Defining Schemas"),(0,r.kt)("p",null,"A subset of ",(0,r.kt)("a",{parentName:"p",href:"https://json-schema.org/"},"JSON Schemas")," can be defined either by parsing a textual schema. Example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val parsedSchema = parseSchema(\n  """\n  {\n  "$id": "https://example.com/geographical-location.schema.json",  // will  be ignored\n  "$schema": "https://json-schema.org/draft/2020-12/schema",       // will be ignored\n  "title": "Longitude and Latitude Values",                        // will be ignored\n  "description": "A geographical coordinate.",                     // will be ignored\n  "required": [ "latitude", "longitude" ],\n  "type": "object",\n  "properties": {\n    "latitude": {\n      "type": "number",\n      "minimum": -90,\n      "maximum": 90\n    },\n    "longitude": {\n      "type": "number",\n      "minimum": -180,\n      "maximum": 180\n    }\n  }\n}\n  """\n)\n')),(0,r.kt)("p",null,"or using Kotest's built-in DSL:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val addressSchema = jsonSchema {\n  obj {   // object is reserved, obj was chosen over jsonObject for brevity but could be changed ofc, or jsonObject could be added as alternative.\n    withProperty("street", required = true) { string() }\n    withProperty("zipCode", required = true) {\n      integer {\n        beEven() and beInRange(10000..99999)   // supports constructing a matcher that will be used to test values\n      }\n    }\n    additionalProperties = false   // triggers failure if other properties are defined in actual\n  }\n}\n\nval personSchema = jsonSchema {\n  obj {\n    withProperty("name", required = true) { string() }\n    withProperty("address") { addressSchema() } // Schemas can re-use other schemas \ud83c\udf89\n  }\n}\n')),(0,r.kt)("p",null,"\u26a0\ufe0f Note that Kotest only supports a subset of JSON schema currently. Currently missing support for:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"$defs and $refs"),(0,r.kt)("li",{parentName:"ul"},"Recursive schemas"),(0,r.kt)("li",{parentName:"ul"},"Parsing of schema composition"),(0,r.kt)("li",{parentName:"ul"},"string.format"),(0,r.kt)("li",{parentName:"ul"},"array.prefixItems,"),(0,r.kt)("li",{parentName:"ul"},"array.contains,"),(0,r.kt)("li",{parentName:"ul"},"array.items = false"),(0,r.kt)("li",{parentName:"ul"},"array.maxContains"),(0,r.kt)("li",{parentName:"ul"},"array.minContains"),(0,r.kt)("li",{parentName:"ul"},"array.uniqueItems"),(0,r.kt)("li",{parentName:"ul"},"enum")),(0,r.kt)("h2",{id:"validating"},"Validating"),(0,r.kt)("p",null,"Once a schema has been defined, you can validate ",(0,r.kt)("inlineCode",{parentName:"p"},"String")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization.JsonElement")," against it:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'"{}" shouldMatchSchema personSchema\n\n// fails with:\n// $.name => Expected string, but was undefined\n\n""" { "name": "Emil", "age": 34 } """\n// Passes, since address isn\'t required and `additionalProperties` are allowed\n')))}p.isMDXComponent=!0}}]);