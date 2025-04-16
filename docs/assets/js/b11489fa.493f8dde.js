"use strict";(self.webpackChunkkotestdocs=self.webpackChunkkotestdocs||[]).push([[1227],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>m});var o=n(67294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function i(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},a=Object.keys(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(o=0;o<a.length;o++)n=a[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var l=o.createContext({}),c=function(e){var t=o.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},p=function(e){var t=c(e.components);return o.createElement(l.Provider,{value:t},e.children)},u={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},d=o.forwardRef((function(e,t){var n=e.components,r=e.mdxType,a=e.originalType,l=e.parentName,p=i(e,["components","mdxType","originalType","parentName"]),d=c(n),m=r,g=d["".concat(l,".").concat(m)]||d[m]||u[m]||a;return n?o.createElement(g,s(s({ref:t},p),{},{components:n})):o.createElement(g,s({ref:t},p))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var a=n.length,s=new Array(a);s[0]=d;var i={};for(var l in t)hasOwnProperty.call(t,l)&&(i[l]=t[l]);i.originalType=e,i.mdxType="string"==typeof e?e:r,s[1]=i;for(var c=2;c<a;c++)s[c]=n[c];return o.createElement.apply(null,s)}return o.createElement.apply(null,n)}d.displayName="MDXCreateElement"},5001:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>s,default:()=>u,frontMatter:()=>a,metadata:()=>i,toc:()=>c});var o=n(87462),r=(n(67294),n(3905));const a={id:"statistics",title:"Statistics",sidebar_label:"Statistics",slug:"property-test-statistics.html"},s=void 0,i={unversionedId:"proptest/statistics",id:"version-5.7.x/proptest/statistics",title:"Statistics",description:"Sometimes you may like to know the types of values being generated by Kotest, to ensure that your generators are",source:"@site/versioned_docs/version-5.7.x/proptest/statistics.md",sourceDirName:"proptest",slug:"/proptest/property-test-statistics.html",permalink:"/docs/5.7.x/proptest/property-test-statistics.html",draft:!1,editUrl:"https://github.com/kotest/kotest/blob/master/documentation/versioned_docs/version-5.7.x/proptest/statistics.md",tags:[],version:"5.7.x",frontMatter:{id:"statistics",title:"Statistics",sidebar_label:"Statistics",slug:"property-test-statistics.html"},sidebar:"proptest",previous:{title:"Shrinking",permalink:"/docs/5.7.x/proptest/property-test-shrinking.html"},next:{title:"Global Configuration",permalink:"/docs/5.7.x/proptest/property-test-global-config.html"}},l={},c=[{value:"Labels",id:"labels",level:2},{value:"Report Mode",id:"report-mode",level:3},{value:"Checking Coverage of Statistics",id:"checking-coverage-of-statistics",level:3},{value:"Custom Reports",id:"custom-reports",level:3}],p={toc:c};function u(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,o.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"Sometimes you may like to know the types of values being generated by Kotest, to ensure that your generators are\nconfigured in the way you expect. Property test ",(0,r.kt)("em",{parentName:"p"},"statistics")," are designed to fulfil this need."),(0,r.kt)("p",null,"The function ",(0,r.kt)("inlineCode",{parentName:"p"},"collect")," is the entry point to statistcs and is used to count categories of values.\nWe use this by invoking it inside a property test with the category we want to increment."),(0,r.kt)("p",null,"For example, lets say we wanted to gather statistics on the values of ",(0,r.kt)("inlineCode",{parentName:"p"},"RoundingMode")," used by a ",(0,r.kt)("inlineCode",{parentName:"p"},"BigDecimal"),". We would\ninvoke ",(0,r.kt)("inlineCode",{parentName:"p"},"checkAll")," as normal, passing the rounding mode to the collect function."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"checkAll(Arb.enum<RoundingMode>(), Arb.bigDecimal()) { mode, decimal ->\n  collect(mode)\n  // test here\n}\n")),(0,r.kt)("p",null,"Now after the test is completed, Kotest will output the test name, and the counts/percentages for each category:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Statistics: [collecting stats] (1000 iterations, 1 args)\n\nHALF_DOWN                                                     142 (14%)\nHALF_UP                                                       141 (14%)\nCEILING                                                       132 (13%)\nFLOOR                                                         122 (12%)\nUP                                                            119 (12%)\nUNNECESSARY                                                   119 (12%)\nHALF_EVEN                                                     118 (12%)\nDOWN                                                          107 (11%)\n")),(0,r.kt)("p",null,"The category we use does not have to be an enum. It can be any object, and you can wrap in conditionals if you want more\ncontrol. For example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'checkAll(Arb.int()) { k ->\n  when {\n    k % 2 == 0 -> collect("EVEN")\n    else -> collect("ODD")\n  }\n  // test here\n}\n')),(0,r.kt)("h2",{id:"labels"},"Labels"),(0,r.kt)("p",null,"Sometimes you may wish to have orthogonal sets of statistics. For example,\nfor a simple number test, we might want to confirm that a certain percentage are even numbers, and a certain percentage\nare negative. One way would be to have EVEN_POS, EVEN_NEG, ODD_POS, ODD_NEG:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'checkAll(Arb.int()) { k ->\n  when {\n    k > 0 && k % 2 == 0 -> collect("EVEN_POS")\n    k % 2 == 0 -> collect("EVEN_NEG")\n    k > 0 -> collect("ODD_POS")\n    else -> collect("ODD_NEG")\n  }\n  // test here\n}\n')),(0,r.kt)("p",null,"This gives us one set of outputs:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"EVEN_POS                                                       142 (27%)\nEVEN_NEG                                                       141 (23%)\nODD_POS                                                        132 (24%)\nODD_NEG                                                        122 (26%)\n")),(0,r.kt)("p",null,"However, as the combinations grow this will become unwieldy, so Kotest supports labelled statistics. You can think of\nthis as distinct sets of statistics. To use labels, just pass the label name as the first arg to the collect method."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},'checkAll(Arb.int()) { k ->\n  when {\n    k % 2 == 0 -> collect("even_odd", "EVEN")\n    else -> collect("even_odd", "ODD")\n  }\n  when {\n    k > 0 -> collect("pos_neg", "POS")\n    else -> collect("pos_neg", "NEG")\n  }\n  // test here\n}\n')),(0,r.kt)("p",null,"Now, Kotest will output multiple sets of statistics, with the label name in the title:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Statistics: [collecting labelled stats] (1000 iterations, 1 args) [even_odd]\n\nODD                                                           520 (52%)\nEVEN                                                          480 (48%)\n\n\nStatistics: [collecting labelled stats] (1000 iterations, 1 args) [pos_neg]\n\nNEG                                                           527 (53%)\nPOS                                                           473 (47%)\n")),(0,r.kt)("h3",{id:"report-mode"},"Report Mode"),(0,r.kt)("p",null,"By default, statistics are printed for every property test. There are four modes which can be configured using the global configuration object ",(0,r.kt)("inlineCode",{parentName:"p"},"PropertyTesting"),"."),(0,r.kt)("p",null,"The possible options are:"),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:null},"Mode"),(0,r.kt)("th",{parentName:"tr",align:null},"Function"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"PropertyTesting.statisticsReportMode = StatisticsReportMode.OFF"),(0,r.kt)("td",{parentName:"tr",align:null},"disable all statistics reporting")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"PropertyTesting.statisticsReportMode = StatisticsReportMode.OFF"),(0,r.kt)("td",{parentName:"tr",align:null},"enables all statistics reporting")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"PropertyTesting.statisticsReportMode = StatisticsReportMode.SUCCESS"),(0,r.kt)("td",{parentName:"tr",align:null},"output statistics only on successful tests")),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:null},"PropertyTesting.statisticsReportMode = StatisticsReportMode.FAILED"),(0,r.kt)("td",{parentName:"tr",align:null},"output statistics only on failed tests")))),(0,r.kt)("h3",{id:"checking-coverage-of-statistics"},"Checking Coverage of Statistics"),(0,r.kt)("p",null,"If you wish to programmatically assert that certain values are being generated, then you can use the specify constraints\nthat must be met."),(0,r.kt)("p",null,"For example, in our previous rounding example, we can check that at least 10% of inputs are covering HALF_DOWN, and 10%\nare covering FLOOR using ",(0,r.kt)("inlineCode",{parentName:"p"},"withCoveragePercentages"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"withCoveragePercentages(mapOf(RoundingMode.HALF_DOWN to 10.0, RoundingMode.FLOOR to 10.0)) {\n  checkAll(Arb.enum<RoundingMode>(), Arb.bigDecimal()) { mode, decimal ->\n    collect(mode)\n    // use the mode / decimal\n  }\n}\n")),(0,r.kt)("p",null,"If we want to check by absolute numbers rather than percentages, we can use ",(0,r.kt)("inlineCode",{parentName:"p"},"withCoverageCounts"),":"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"withCoverageCounts(mapOf(RoundingMode.HALF_DOWN to 75, RoundingMode.FLOOR to 75)) {\n  checkAll(Arb.enum<RoundingMode>(), Arb.bigDecimal()) { mode, decimal ->\n    collect(mode)\n    // use the mode / decimal\n  }\n}\n")),(0,r.kt)("h3",{id:"custom-reports"},"Custom Reports"),(0,r.kt)("p",null,"You can customize the report format, or generate reports from the raw data, by using your own instance of ",(0,r.kt)("inlineCode",{parentName:"p"},"StatisticsReporter"),".\nThis is configured via the global configuration object ",(0,r.kt)("inlineCode",{parentName:"p"},"PropertyTesting"),"."),(0,r.kt)("p",null,"For example:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"object MyStatisticsReporter : object : StatisticsReporter { ... }\nPropertyTesting.statisticsReporter = MyStatisticsReporter\n")))}u.isMDXComponent=!0}}]);