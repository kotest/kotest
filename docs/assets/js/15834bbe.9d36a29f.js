"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[39778],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>p});var a=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)n=i[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var o=a.createContext({}),m=function(e){var t=a.useContext(o),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},c=function(e){var t=m(e.components);return a.createElement(o.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},d=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,o=e.parentName,c=l(e,["components","mdxType","originalType","parentName"]),d=m(n),p=r,h=d["".concat(o,".").concat(p)]||d[p]||u[p]||i;return n?a.createElement(h,s(s({ref:t},c),{},{components:n})):a.createElement(h,s({ref:t},c))}));function p(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,s=new Array(i);s[0]=d;var l={};for(var o in t)hasOwnProperty.call(t,o)&&(l[o]=t[o]);l.originalType=e,l.mdxType="string"==typeof e?e:r,s[1]=l;for(var m=2;m<i;m++)s[m]=n[m];return a.createElement.apply(null,s)}return a.createElement.apply(null,n)}d.displayName="MDXCreateElement"},23486:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>o,contentTitle:()=>s,default:()=>u,frontMatter:()=>i,metadata:()=>l,toc:()=>m});var a=n(87462),r=(n(67294),n(3905));const i={title:"JSON Schema Matchers",slug:"json-schema-matchers.html",sidebar_label:"Schema matchers"},s=void 0,l={unversionedId:"assertions/json/schema",id:"version-5.8.x/assertions/json/schema",title:"JSON Schema Matchers",description:"| Matcher             | Description                                                                                                                                         | Targets       |",source:"@site/versioned_docs/version-5.8.x/assertions/json/schema.md",sourceDirName:"assertions/json",slug:"/assertions/json/json-schema-matchers.html",permalink:"/docs/5.8.x/assertions/json/json-schema-matchers.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.8.x/assertions/json/schema.md",tags:[],version:"5.8.x",frontMatter:{title:"JSON Schema Matchers",slug:"json-schema-matchers.html",sidebar_label:"Schema matchers"},sidebar:"assertions",previous:{title:"Matching content",permalink:"/docs/5.8.x/assertions/json/content-json-matchers.html"},next:{title:"Ktor",permalink:"/docs/5.8.x/assertions/ktor-matchers.html"}},o={},m=[{value:"Parsing Schema",id:"parsing-schema",level:2},{value:"Building Schema",id:"building-schema",level:2},{value:"Array",id:"array",level:3},{value:"Length (minItems and maxItems)",id:"length-minitems-and-maxitems",level:4},{value:"Uniqueness",id:"uniqueness",level:4},{value:"Validating",id:"validating",level:2}],c={toc:m};function u(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,a.Z)({},c,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Matcher"),(0,r.kt)("th",{parentName:"tr",align:null},"Description"),(0,r.kt)("th",{parentName:"tr",align:"left"},"Targets"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("inlineCode",{parentName:"td"},"shouldMatchSchema")),(0,r.kt)("td",{parentName:"tr",align:null},"Validates that a ",(0,r.kt)("inlineCode",{parentName:"td"},"String")," or ",(0,r.kt)("inlineCode",{parentName:"td"},"kotlinx.serialization.JsonElement")," matches a ",(0,r.kt)("inlineCode",{parentName:"td"},"JsonSchema"),". See description below for details on constructing schemas."),(0,r.kt)("td",{parentName:"tr",align:"left"},"Multiplatform")))),(0,r.kt)("h2",{id:"parsing-schema"},"Parsing Schema"),(0,r.kt)("p",null,"A subset of ",(0,r.kt)("a",{parentName:"p",href:"https://json-schema.org/"},"JSON Schemas")," can be defined either by parsing a textual schema. Example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val parsedSchema = parseSchema(\n  """\n  {\n  "$id": "https://example.com/geographical-location.schema.json",  // will  be ignored\n  "$schema": "https://json-schema.org/draft/2020-12/schema",       // will be ignored\n  "title": "Longitude and Latitude Values",                        // will be ignored\n  "description": "A geographical coordinate.",                     // will be ignored\n  "required": [ "latitude", "longitude" ],\n  "type": "object",\n  "properties": {\n    "latitude": {\n      "type": "number",\n      "minimum": -90,\n      "maximum": 90\n    },\n    "longitude": {\n      "type": "number",\n      "minimum": -180,\n      "maximum": 180\n    }\n  }\n}\n  """\n)\n')),(0,r.kt)("p",null,"or using Kotest's built-in DSL:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'val addressSchema = jsonSchema {\n  obj {   // object is reserved, obj was chosen over jsonObject for brevity but could be changed ofc, or jsonObject could be added as alternative.\n    withProperty("street", required = true) { string() }\n    withProperty("zipCode", required = true) {\n      integer {\n        beEven() and beInRange(10000..99999)   // supports constructing a matcher that will be used to test values\n      }\n    }\n    additionalProperties = false   // triggers failure if other properties are defined in actual\n  }\n}\n\nval personSchema = jsonSchema {\n  obj {\n    withProperty("name", required = true) { string() }\n    withProperty("address") { addressSchema() } // Schemas can re-use other schemas \ud83c\udf89\n  }\n}\n')),(0,r.kt)("h2",{id:"building-schema"},"Building Schema"),(0,r.kt)("h3",{id:"array"},"Array"),(0,r.kt)("p",null,"Arrays are used for ordered elements. In JSON, each element in an array may be of a different type."),(0,r.kt)("h4",{id:"length-minitems-and-maxitems"},"Length (minItems and maxItems)"),(0,r.kt)("p",null,"The length of the array can be specified using the ",(0,r.kt)("inlineCode",{parentName:"p"},"minItems")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"maxItems")," keywords. The value of each keyword must be a\nnon-negative number and defaults are 0 and Int.MAX_VALUE"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"val lengthBoundedSchema = jsonSchema {\n  array(minItems = 0, maxItems = 1) { number() }\n}\n")),(0,r.kt)("h4",{id:"uniqueness"},"Uniqueness"),(0,r.kt)("p",null,"A schema can ensure that each of the items in an array is unique. Simply set the ",(0,r.kt)("inlineCode",{parentName:"p"},"uniqueItems")," keyword to true."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"val uniqueArray = jsonSchema {\n  array(uniqueItems = true) { number() }\n}\n")),(0,r.kt)("p",null,"\u26a0\ufe0f Note that Kotest only supports a subset of JSON schema currently. Currently, missing support for:"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"$defs and $refs"),(0,r.kt)("li",{parentName:"ul"},"Recursive schemas"),(0,r.kt)("li",{parentName:"ul"},"Parsing of schema composition"),(0,r.kt)("li",{parentName:"ul"},"string.format"),(0,r.kt)("li",{parentName:"ul"},"array.prefixItems,"),(0,r.kt)("li",{parentName:"ul"},"array.contains,"),(0,r.kt)("li",{parentName:"ul"},"array.items = false"),(0,r.kt)("li",{parentName:"ul"},"array.maxContains"),(0,r.kt)("li",{parentName:"ul"},"array.minContains"),(0,r.kt)("li",{parentName:"ul"},"array.uniqueItems"),(0,r.kt)("li",{parentName:"ul"},"enum")),(0,r.kt)("h2",{id:"validating"},"Validating"),(0,r.kt)("p",null,"Once a schema has been defined, you can validate ",(0,r.kt)("inlineCode",{parentName:"p"},"String")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"kotlinx.serialization.JsonElement")," against it:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'"{}" shouldMatchSchema personSchema\n\n// fails with:\n// $.name => Expected string, but was undefined\n\n""" { "name": "Emil", "age": 34 } """\n// Passes, since address isn\'t required and `additionalProperties` are allowed\n')))}u.isMDXComponent=!0}}]);