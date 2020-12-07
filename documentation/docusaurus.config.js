module.exports = {
   title: 'Kotest',
   tagline: 'Flexible and elegant multiplatform test framework, assertions, and property test library for Kotlin',
   url: 'https://kotest.io',
   baseUrl: '/',
   onBrokenLinks: 'throw',
   onBrokenMarkdownLinks: 'warn',
   favicon: 'img/favicon.ico',
   organizationName: 'kotest', // Usually your GitHub org/user name.
   projectName: 'kotest', // Usually your repo name.
   themeConfig: {
      navbar: {
         title: 'Kotest',
         logo: {
            alt: 'Kotest',
            src: 'img/logo.png',
         },
         items: [
            {
               type: 'doc',
               docId: 'quickstart',
               label: 'Quick Start',
               position: 'left'
            },
            {
               type: 'doc',
               docId: 'framework/index',
               label: 'Framework',
               position: 'left'
            },
            {
               type: 'doc',
               docId: 'assertions/index',
               label: 'Assertions',
               position: 'left'
            },
            {
               type: 'doc',
               docId: 'proptest/index',
               label: 'Property Testing',
               position: 'left'
            },
            {
               type: 'doc',
               docId: 'intellij/intelli',
               label: 'Intellij Plugin',
               position: 'left'
            },
            {
               href: 'https://github.com/kotest/kotest/issues',
               label: 'Issue Tracker',
               position: 'right'
            },
            {
               href: 'https://github.com/facebook/docusaurus',
               label: 'GitHub',
               position: 'right',
            },
         ],
      },
      footer: {
         style: 'dark',
         links: [
            {
               title: 'Docs',
               items: [
                  {
                     label: 'Style Guide',
                     to: 'docs/',
                  }
               ],
            },
            {
               title: 'Community',
               items: [
                  {
                     label: 'Slack',
                     href: 'https://kotlinlang.slack.com/archives/CT0G9SD7Z',
                  },
                  {
                     label: 'Github',
                     href: 'https://github.com/kotest/kotest',
                  },
                  {
                     label: 'Stack Overflow',
                     href: 'https://stackoverflow.com/questions/tagged/kotest',
                  },
               ],
            },
            {
               title: 'More',
               items: [
                  {
                     label: 'B',
                     href: 'https://github.com/kotest/kotest',
                  },
               ],
            },
         ],
         copyright: `Copyright © ${new Date().getFullYear()} Kotest, Inc. Built with Docusaurus.`,
      },
   },
   presets: [
      [
         '@docusaurus/preset-classic',
         {
            docs: {
               sidebarPath: require.resolve('./sidebars.js'),
               editUrl: 'https://github.com/kotest/kotest',
            }
         },
      ],
   ],
};
