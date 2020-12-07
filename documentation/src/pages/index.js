import React from 'react';
import clsx from 'clsx';
import Layout from '@theme/Layout';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import styles from './styles.module.css';

const features = [
   {
      title: 'Test Framework',
      imageUrl: 'img/undraw_docusaurus_mountain.svg',
      description: (
         <>
            The Kotest test framework enables test to be laid out in a fluid way and execute them on the JVM or
            Javascript.

            <br/><br/>

            With built in coroutine support at every level, the ability to use functions as test lifecycle callbacks,
            extensive extension points,
            advanced conditional evaluation, powerful data driven testing, and more.

            <br/><br/>

            <img src="https://img.shields.io/maven-central/v/io.kotest/kotest-framework-engine.svg?label=release"
                 alt="version badge"/>
            &nbsp;
            <a href="https://oss.sonatype.org/content/repositories/snapshots/io/kotest">
               <img
                  src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"
                  alt="version badge"/>
            </a>
         </>
      ),
   },
   {
      title: 'Assertions Library',
      imageUrl: 'img/undraw_docusaurus_tree.svg',
      description: (
         <>
            The Kotest assertions library is a Kotlin-first multi-platform assertions library with over 300 rich
            assertions.

            <br/><br/>

            It comes equipped with collection inspectors, non-determistic test helpers, soft assertions, modules for
            arrow, json, kotlinx-datetime and much more.

            <br/><br/>

            <img src="https://img.shields.io/maven-central/v/io.kotest/kotest-assertions-core.svg?label=release"
                 alt="version badge"/>
            &nbsp;
            <a href="https://oss.sonatype.org/content/repositories/snapshots/io/kotest">
               <img
                  src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"
                  alt="version badge"/>
            </a>
         </>
      ),
   },
   {
      title: 'Property Testing',
      imageUrl: 'img/undraw_docusaurus_react.svg',
      description: (
         <>
            The Kotest proptest module is an advanced multi-platform property test library with over 50 built in
            generators.

            <br/><br/>

            It supports failure shrinking, the ability to easily create and compose new generators; both exhaustive and
            arbitrary checks,
            repeatable random seeds, coverage metrics, and more.

            <br/><br/>

            <img src="https://img.shields.io/maven-central/v/io.kotest/kotest-property.svg?label=release"
                 alt="version badge"/>
            &nbsp;
            <a href="https://oss.sonatype.org/content/repositories/snapshots/io/kotest">
               <img
                  src="https://img.shields.io/nexus/s/https/oss.sonatype.org/io.kotest/kotest-framework-engine.svg?label=snapshot"
                  alt="version badge"/>
            </a>
         </>
      ),
   },
];

function Feature({imageUrl, title, description}) {
   const imgUrl = useBaseUrl(imageUrl);
   return (
      <div className={clsx('col col--4', styles.feature)}>
         {imgUrl && (
            <div className="text--center">
               <img className={styles.featureImage} src={imgUrl} alt={title}/>
            </div>
         )}
         <h3>{title}</h3>
         <p>{description}</p>
      </div>
   );
}

function Home() {
   const context = useDocusaurusContext();
   const {siteConfig = {}} = context;
   return (
      <Layout
         title="Kotest"
         description="Flexible, powerful and elegant kotlin test framework with multiplatform support">
         <header className={clsx('hero hero--primary', styles.heroBanner)}>
            <div className="container">
               <h1 className="hero__title">{siteConfig.title}</h1>
               <p className="hero__subtitle">{siteConfig.tagline}</p>
               <div className={styles.buttons}>
                  <Link
                     className={clsx(
                        'button button--outline button--secondary button--lg',
                        styles.getStarted,
                     )}
                     to={useBaseUrl('docs/')}>
                     Get Started
                  </Link>
               </div>
            </div>
         </header>
         <main>
            <section className={styles.features}>
               <div className="container">
                  <div className="row">
                     {features.map((props, idx) => (
                        <Feature key={idx} {...props} />
                     ))}
                  </div>
               </div>
            </section>
            <section className={styles.features}>
               <div className="container">
                  <div className="row">
                     Each subproject is provided independently so you can pick and mix which modules to use if you
                     don't want to go _all in_ on Kotest
                  </div>
                  <div>
                     <div className="row">

                        <a href="docs/changelog">See the changelog</a> for latest updates.<br/>
                        See our quickstart guide to get up and running.

                        [![GitHub
                        stars](https://img.shields.io/github/stars/kotest/kotest.svg?style=social&label=Star&maxAge=2592000)](https://GitHub.com/kotest/kotest/stargazers/)
                        [<img
                        src="https://img.shields.io/maven-central/v/io.kotest/kotest-framework-api-jvm.svg?label=latest%20release"/>](http://search.maven.org/#search|ga|1|kotest)
                        ![GitHub](https://img.shields.io/github/license/kotest/kotest)
                        [![kotest @
                        kotlinlang.slack.com](https://img.shields.io/static/v1?label=kotlinlang&message=kotest&color=blue&logo=slack)](https://kotlinlang.slack.com/archives/CT0G9SD7Z)

                        Community
                        ---------
                        * [Stack Overflow](http://stackoverflow.com/questions/tagged/kotest) (don't forget to use the
                        tag "kotest".)
                        * [Kotest channel](https://kotlinlang.slack.com/messages/kotest) in the Kotlin Slack (get an
                        invite [here](http://slack.kotlinlang.org/))
                        * [Contribute](https://github.com/kotest/kotest/wiki/contribute)

                        Read more about Kotest from third party [blogs and articles](doc/blogs.md).

                     </div>
                  </div>
               </div>
            </section>
            .
         </main>
      </Layout>
   );
}

export default Home;
