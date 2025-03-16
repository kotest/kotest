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
            The Kotest test framework enables test to be laid out in a fluid way and execute them on JVM, Javascript,
            or native platforms.

            <br/><br/>

            With built in coroutine support at every level, the ability to use functions as test lifecycle callbacks,
            extensive extension points,
            advanced conditional evaluation, powerful data driven testing, and more.

            <br/><br/>

            <a href="/docs/framework/framework.html">Read more</a>
         </>
      ),
   },
   {
      title: 'Assertions Library',
      imageUrl: 'img/undraw_docusaurus_tree.svg',
      description: (
         <>
            The Kotest assertions library is a Kotlin-first multiplatform assertions library with over 300 rich
            assertions.

            <br/><br/>

            It comes equipped with collection inspectors, non-determistic test helpers, soft assertions, modules for
            arrow, json, kotlinx-datetime and much more.

            <br/><br/>

            <a href="/docs/assertions/assertions.html">Read more</a>
         </>
      ),
   },
   {
      title: 'Property Testing',
      imageUrl: 'img/undraw_docusaurus_react.svg',
      description: (
         <>
            The Kotest property testing module is an advanced multiplatform property test library with over 50 built in
            generators.

            <br/><br/>

            It supports failure shrinking, the ability to easily create and compose new generators; both exhaustive and
            arbitrary checks,
            repeatable random seeds, coverage metrics, and more.

            <br/><br/>

            <a href="/docs/proptest/property-based-testing.html">Read more</a>
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
               <p className={clsx(styles.heroSlogan)}>
                  Kotest is a flexible and elegant <strong>multiplatform</strong> test framework
                  for <strong>Kotlin</strong> with extensive <strong>assertions</strong> and integrated <strong>property
                  testing</strong>
               </p>
               <div className={styles.buttons}>
                  <Link
                     className={clsx(
                        'button button--outline button--secondary button--lg',
                        styles.gettingStartedButton,
                     )}
                     to={useBaseUrl('docs/quickstart')}>
                     Get Started
                  </Link>

                  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

                  <iframe
                     src="https://ghbtns.com/github-btn.html?user=kotest&repo=kotest&type=star&count=true&size=large"
                     frameBorder="0" scrolling="0" width="170" height="30" title="GitHub"/>

               </div>
            </div>
         </header>
         <main>
            <section className={styles.features}>
               <div className="container">
                  <div className="row">
                     <a href="https://kotlinlang.slack.com/archives/CT0G9SD7Z">
                        <img
                           src="https://img.shields.io/static/v1?label=kotlinlang&message=kotest&color=blue&logo=slack&style=for-the-badge"
                           alt="Slack"/>
                     </a>

                     &nbsp;

                     <a href="https://search.maven.org/search?q=g:io.kotest%20OR%20g:io.kotest.extensions">
                        <img
                           src="https://img.shields.io/maven-central/v/io.kotest/kotest-property.svg?label=release&style=for-the-badge"
                           alt="version badge"/>
                     </a>

                     &nbsp;

                     <a href="https://oss.sonatype.org/content/repositories/snapshots/io/kotest/">
                        <img
                           src="https://img.shields.io/nexus/snapshots/https/s01.oss.sonatype.org/io.kotest/kotest-framework-api.svg?label=latest%20snapshot&style=for-the-badge"
                           alt="version badge"/>
                     </a>

                     &nbsp;

                     <a href="https://github.com/kotest/kotest/blob/master/LICENSE">
                        <img
                           src="https://img.shields.io/badge/license-apache2.0-green?style=for-the-badge"
                           alt="license"/>
                     </a>

                     &nbsp;

                     <a href="https://stackoverflow.com/questions/tagged/kotest">
                        <img
                           src="https://img.shields.io/badge/stackoverflow-kotest-blue?style=for-the-badge"
                           alt="stack overflow"/>
                     </a>
                  </div>
                  <div className="row">
                     {features.map((props, idx) => (
                        <Feature key={idx} {...props} />
                     ))}
                  </div>
               </div>
            </section>
         </main>
      </Layout>
   );
}

export default Home;
