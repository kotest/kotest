module.exports = {
   docs: [
      'quickstart',
      'changelog',
      'thirdparty',
      'blogs',
   ],
   proptest: [
      'proptest/index',
      'proptest/testfunctions',
      'proptest/gens',
      'proptest/genslist',
      'proptest/genops',
      'proptest/proptestconfig',
      'proptest/customgens',
      'proptest/globalconfig',
   ],
   assertions: [
      'assertions/index',
      'assertions/matchers',
      'assertions/exceptions',
      'assertions/clues',
      'assertions/soft_assertions',
      'assertions/nondeterministic',
      'assertions/inspectors',
      'assertions/assertion_mode',
      {
         type: "category",
         label: "Matcher Modules",
         collapsed: false,
         items: [
            'assertions/android_matchers',
            'assertions/arrow',
            'assertions/compiler',
            'assertions/core',
            'assertions/json',
            'assertions/jsoup',
            'assertions/klock',
            'assertions/konform',
            'assertions/kotlinx_datetime',
            'assertions/ktor',
            'assertions/sql-matchers',
         ]
      }
   ],
   framework: [
      'framework/index',
      'framework/styles',
      'framework/conditional_evaluation',
      'framework/isolation_mode',
      'framework/exceptions',
      'framework/data_driven_testing',
      'framework/listeners',
      {
         type: "category",
         label: "Extensions",
         collapsed: false,
         items: [
            'framework/extensions/spring',
            'framework/extensions/ktor',
            'framework/extensions/system_extensions',
            'framework/extensions/test_containers',
            'framework/extensions/mockserver',
            'framework/extensions/junit_xml',
            'framework/extensions/allure',
            'framework/extensions/instant',
            'framework/extensions/koin',
            'framework/extensions/robolectric',
         ]
      },
      'framework/plugins',
      {
         type: "category",
         label: "Integrations",
         collapsed: false,
         items: [
            'framework/integrations/mocks',
            'framework/integrations/jacoco',
         ]
      },
      {
         type: "category",
         label: "Ordering",
         collapsed: false,
         items: [
            'framework/spec_ordering',
            'framework/test_ordering',
         ]
      },
      'framework/tags',
      {
         type: "category",
         label: "Resources",
         collapsed: false,
         items: [
            'framework/autoclose',
            'framework/tempfile',
         ]
      },
      {
         type: "category",
         label: "Configuration",
         collapsed: false,
         items: [
            'framework/test_case_config',
            'framework/project_config',
            'framework/framework_config_props',
         ]
      },
      'framework/test_extensions',
      'framework/test_factories',
      'framework/timeout',
   ],
};
