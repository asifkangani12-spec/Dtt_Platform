package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final BouncycastleLibraryAccessors laccForBouncycastleLibraryAccessors = new BouncycastleLibraryAccessors(owner);
    private final CommonsLibraryAccessors laccForCommonsLibraryAccessors = new CommonsLibraryAccessors(owner);
    private final FirebaseLibraryAccessors laccForFirebaseLibraryAccessors = new FirebaseLibraryAccessors(owner);
    private final HibernateLibraryAccessors laccForHibernateLibraryAccessors = new HibernateLibraryAccessors(owner);
    private final JacksonLibraryAccessors laccForJacksonLibraryAccessors = new JacksonLibraryAccessors(owner);
    private final JakartaLibraryAccessors laccForJakartaLibraryAccessors = new JakartaLibraryAccessors(owner);
    private final JavaLibraryAccessors laccForJavaLibraryAccessors = new JavaLibraryAccessors(owner);
    private final SpringLibraryAccessors laccForSpringLibraryAccessors = new SpringLibraryAccessors(owner);
    private final SpringdocLibraryAccessors laccForSpringdocLibraryAccessors = new SpringdocLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

        /**
         * Creates a dependency provider for classgraph (io.github.classgraph:classgraph)
     * with versionRef 'classgraph'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getClassgraph() {
            return create("classgraph");
    }

        /**
         * Creates a dependency provider for httpclient4 (org.apache.httpcomponents:httpclient)
     * with versionRef 'httpclient4'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getHttpclient4() {
            return create("httpclient4");
    }

        /**
         * Creates a dependency provider for httpclient5 (org.apache.httpcomponents.client5:httpclient5)
     * with versionRef 'httpclient5'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getHttpclient5() {
            return create("httpclient5");
    }

        /**
         * Creates a dependency provider for httpcore5 (org.apache.httpcomponents.core5:httpcore5)
     * with versionRef 'httpclient5core'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getHttpcore5() {
            return create("httpcore5");
    }

        /**
         * Creates a dependency provider for imgscalr (org.imgscalr:imgscalr-lib)
     * with versionRef 'imgscalr'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getImgscalr() {
            return create("imgscalr");
    }

        /**
         * Creates a dependency provider for jasypt (com.github.ulisesbocchio:jasypt-spring-boot-starter)
     * with versionRef 'jasypt'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJasypt() {
            return create("jasypt");
    }

        /**
         * Creates a dependency provider for jsch (com.jcraft:jsch)
     * with versionRef 'jsch'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJsch() {
            return create("jsch");
    }

        /**
         * Creates a dependency provider for json (org.json:json)
     * with versionRef 'json'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJson() {
            return create("json");
    }

        /**
         * Creates a dependency provider for minio (io.minio:minio)
     * with versionRef 'minio'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMinio() {
            return create("minio");
    }

        /**
         * Creates a dependency provider for pdfbox (org.apache.pdfbox:pdfbox)
     * with versionRef 'pdfbox'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPdfbox() {
            return create("pdfbox");
    }

        /**
         * Creates a dependency provider for postgresql (org.postgresql:postgresql)
     * with versionRef 'postgresql'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("postgresql");
    }

        /**
         * Creates a dependency provider for sentry (io.sentry:sentry-spring-boot-starter-jakarta)
     * with versionRef 'sentry'.
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSentry() {
            return create("sentry");
    }

    /**
     * Returns the group of libraries at bouncycastle
     */
    public BouncycastleLibraryAccessors getBouncycastle() {
        return laccForBouncycastleLibraryAccessors;
    }

    /**
     * Returns the group of libraries at commons
     */
    public CommonsLibraryAccessors getCommons() {
        return laccForCommonsLibraryAccessors;
    }

    /**
     * Returns the group of libraries at firebase
     */
    public FirebaseLibraryAccessors getFirebase() {
        return laccForFirebaseLibraryAccessors;
    }

    /**
     * Returns the group of libraries at hibernate
     */
    public HibernateLibraryAccessors getHibernate() {
        return laccForHibernateLibraryAccessors;
    }

    /**
     * Returns the group of libraries at jackson
     */
    public JacksonLibraryAccessors getJackson() {
        return laccForJacksonLibraryAccessors;
    }

    /**
     * Returns the group of libraries at jakarta
     */
    public JakartaLibraryAccessors getJakarta() {
        return laccForJakartaLibraryAccessors;
    }

    /**
     * Returns the group of libraries at java
     */
    public JavaLibraryAccessors getJava() {
        return laccForJavaLibraryAccessors;
    }

    /**
     * Returns the group of libraries at spring
     */
    public SpringLibraryAccessors getSpring() {
        return laccForSpringLibraryAccessors;
    }

    /**
     * Returns the group of libraries at springdoc
     */
    public SpringdocLibraryAccessors getSpringdoc() {
        return laccForSpringdocLibraryAccessors;
    }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class BouncycastleLibraryAccessors extends SubDependencyFactory {

        public BouncycastleLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for pkix (org.bouncycastle:bcpkix-jdk18on)
         * with versionRef 'bouncycastle'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getPkix() {
                return create("bouncycastle.pkix");
        }

            /**
             * Creates a dependency provider for provider (org.bouncycastle:bcprov-jdk18on)
         * with versionRef 'bouncycastle'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getProvider() {
                return create("bouncycastle.provider");
        }

    }

    public static class CommonsLibraryAccessors extends SubDependencyFactory {

        public CommonsLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for fileupload (commons-fileupload:commons-fileupload)
         * with versionRef 'commons.fileupload'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getFileupload() {
                return create("commons.fileupload");
        }

            /**
             * Creates a dependency provider for io (commons-io:commons-io)
         * with versionRef 'commons.io'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getIo() {
                return create("commons.io");
        }

            /**
             * Creates a dependency provider for lang3 (org.apache.commons:commons-lang3)
         * with versionRef 'commons.lang3'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getLang3() {
                return create("commons.lang3");
        }

    }

    public static class FirebaseLibraryAccessors extends SubDependencyFactory {

        public FirebaseLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for admin (com.google.firebase:firebase-admin)
         * with versionRef 'firebase.admin'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAdmin() {
                return create("firebase.admin");
        }

    }

    public static class HibernateLibraryAccessors extends SubDependencyFactory {

        public HibernateLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for core (org.hibernate.orm:hibernate-core)
         * with versionRef 'hibernate'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("hibernate.core");
        }

            /**
             * Creates a dependency provider for validator (org.hibernate.validator:hibernate-validator)
         * with version '8.0.1.Final'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getValidator() {
                return create("hibernate.validator");
        }

    }

    public static class JacksonLibraryAccessors extends SubDependencyFactory {

        public JacksonLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for annotations (com.fasterxml.jackson.core:jackson-annotations)
         * with versionRef 'jackson'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getAnnotations() {
                return create("jackson.annotations");
        }

            /**
             * Creates a dependency provider for core (com.fasterxml.jackson.core:jackson-core)
         * with versionRef 'jackson'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("jackson.core");
        }

            /**
             * Creates a dependency provider for databind (com.fasterxml.jackson.core:jackson-databind)
         * with versionRef 'jackson'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getDatabind() {
                return create("jackson.databind");
        }

    }

    public static class JakartaLibraryAccessors extends SubDependencyFactory {

        public JakartaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for el (jakarta.el:jakarta.el-api)
         * with version '5.0.1'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getEl() {
                return create("jakarta.el");
        }

            /**
             * Creates a dependency provider for servlet (jakarta.servlet:jakarta.servlet-api)
         * with version '6.0.0'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getServlet() {
                return create("jakarta.servlet");
        }

            /**
             * Creates a dependency provider for validation (jakarta.validation:jakarta.validation-api)
         * with version '3.0.2'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getValidation() {
                return create("jakarta.validation");
        }

    }

    public static class JavaLibraryAccessors extends SubDependencyFactory {

        public JavaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jwt (com.auth0:java-jwt)
         * with versionRef 'java.jwt'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJwt() {
                return create("java.jwt");
        }

    }

    public static class SpringLibraryAccessors extends SubDependencyFactory {
        private final SpringBootLibraryAccessors laccForSpringBootLibraryAccessors = new SpringBootLibraryAccessors(owner);
        private final SpringVaultLibraryAccessors laccForSpringVaultLibraryAccessors = new SpringVaultLibraryAccessors(owner);

        public SpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for kafka (org.springframework.kafka:spring-kafka)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getKafka() {
                return create("spring.kafka");
        }

        /**
         * Returns the group of libraries at spring.boot
         */
        public SpringBootLibraryAccessors getBoot() {
            return laccForSpringBootLibraryAccessors;
        }

        /**
         * Returns the group of libraries at spring.vault
         */
        public SpringVaultLibraryAccessors getVault() {
            return laccForSpringVaultLibraryAccessors;
        }

    }

    public static class SpringBootLibraryAccessors extends SubDependencyFactory {
        private final SpringBootDataLibraryAccessors laccForSpringBootDataLibraryAccessors = new SpringBootDataLibraryAccessors(owner);

        public SpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for actuator (org.springframework.boot:spring-boot-starter-actuator)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getActuator() {
                return create("spring.boot.actuator");
        }

            /**
             * Creates a dependency provider for devtools (org.springframework.boot:spring-boot-devtools)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getDevtools() {
                return create("spring.boot.devtools");
        }

            /**
             * Creates a dependency provider for mail (org.springframework.boot:spring-boot-starter-mail)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getMail() {
                return create("spring.boot.mail");
        }

            /**
             * Creates a dependency provider for security (org.springframework.boot:spring-boot-starter-security)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getSecurity() {
                return create("spring.boot.security");
        }

            /**
             * Creates a dependency provider for starter (org.springframework.boot:spring-boot-starter)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getStarter() {
                return create("spring.boot.starter");
        }

            /**
             * Creates a dependency provider for test (org.springframework.boot:spring-boot-starter-test)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getTest() {
                return create("spring.boot.test");
        }

            /**
             * Creates a dependency provider for undertow (org.springframework.boot:spring-boot-starter-undertow)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getUndertow() {
                return create("spring.boot.undertow");
        }

            /**
             * Creates a dependency provider for validation (org.springframework.boot:spring-boot-starter-validation)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getValidation() {
                return create("spring.boot.validation");
        }

            /**
             * Creates a dependency provider for web (org.springframework.boot:spring-boot-starter-web)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getWeb() {
                return create("spring.boot.web");
        }

        /**
         * Returns the group of libraries at spring.boot.data
         */
        public SpringBootDataLibraryAccessors getData() {
            return laccForSpringBootDataLibraryAccessors;
        }

    }

    public static class SpringBootDataLibraryAccessors extends SubDependencyFactory {

        public SpringBootDataLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for jpa (org.springframework.boot:spring-boot-starter-data-jpa)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getJpa() {
                return create("spring.boot.data.jpa");
        }

    }

    public static class SpringVaultLibraryAccessors extends SubDependencyFactory {

        public SpringVaultLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for config (org.springframework.cloud:spring-cloud-starter-vault-config)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getConfig() {
                return create("spring.vault.config");
        }

            /**
             * Creates a dependency provider for core (org.springframework.vault:spring-vault-core)
         * with no version specified
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getCore() {
                return create("spring.vault.core");
        }

    }

    public static class SpringdocLibraryAccessors extends SubDependencyFactory {

        public SpringdocLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

            /**
             * Creates a dependency provider for openapi (org.springdoc:springdoc-openapi-starter-webmvc-ui)
         * with versionRef 'springdoc'.
             * This dependency was declared in catalog libs.versions.toml
             */
            public Provider<MinimalExternalModuleDependency> getOpenapi() {
                return create("springdoc.openapi");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final CommonsVersionAccessors vaccForCommonsVersionAccessors = new CommonsVersionAccessors(providers, config);
        private final FirebaseVersionAccessors vaccForFirebaseVersionAccessors = new FirebaseVersionAccessors(providers, config);
        private final GrpcVersionAccessors vaccForGrpcVersionAccessors = new GrpcVersionAccessors(providers, config);
        private final JavaVersionAccessors vaccForJavaVersionAccessors = new JavaVersionAccessors(providers, config);
        private final NettyVersionAccessors vaccForNettyVersionAccessors = new NettyVersionAccessors(providers, config);
        private final SpringVersionAccessors vaccForSpringVersionAccessors = new SpringVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: bouncycastle (1.79)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getBouncycastle() { return getVersion("bouncycastle"); }

            /**
             * Returns the version associated to this alias: classgraph (4.8.150)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getClassgraph() { return getVersion("classgraph"); }

            /**
             * Returns the version associated to this alias: hibernate (6.4.4.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHibernate() { return getVersion("hibernate"); }

            /**
             * Returns the version associated to this alias: httpclient4 (4.5.14)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHttpclient4() { return getVersion("httpclient4"); }

            /**
             * Returns the version associated to this alias: httpclient5 (5.4.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHttpclient5() { return getVersion("httpclient5"); }

            /**
             * Returns the version associated to this alias: httpclient5core (5.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHttpclient5core() { return getVersion("httpclient5core"); }

            /**
             * Returns the version associated to this alias: imgscalr (4.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getImgscalr() { return getVersion("imgscalr"); }

            /**
             * Returns the version associated to this alias: jackson (2.18.6)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJackson() { return getVersion("jackson"); }

            /**
             * Returns the version associated to this alias: jasypt (3.0.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJasypt() { return getVersion("jasypt"); }

            /**
             * Returns the version associated to this alias: jsch (0.1.55)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJsch() { return getVersion("jsch"); }

            /**
             * Returns the version associated to this alias: json (20240303)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJson() { return getVersion("json"); }

            /**
             * Returns the version associated to this alias: minio (8.6.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMinio() { return getVersion("minio"); }

            /**
             * Returns the version associated to this alias: pdfbox (3.0.2)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getPdfbox() { return getVersion("pdfbox"); }

            /**
             * Returns the version associated to this alias: postgresql (42.7.3)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getPostgresql() { return getVersion("postgresql"); }

            /**
             * Returns the version associated to this alias: protobuf (3.25.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getProtobuf() { return getVersion("protobuf"); }

            /**
             * Returns the version associated to this alias: sentry (7.16.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSentry() { return getVersion("sentry"); }

            /**
             * Returns the version associated to this alias: springdoc (2.8.14)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getSpringdoc() { return getVersion("springdoc"); }

            /**
             * Returns the version associated to this alias: undertow (2.3.21.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getUndertow() { return getVersion("undertow"); }

        /**
         * Returns the group of versions at versions.commons
         */
        public CommonsVersionAccessors getCommons() {
            return vaccForCommonsVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.firebase
         */
        public FirebaseVersionAccessors getFirebase() {
            return vaccForFirebaseVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.grpc
         */
        public GrpcVersionAccessors getGrpc() {
            return vaccForGrpcVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.java
         */
        public JavaVersionAccessors getJava() {
            return vaccForJavaVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.netty
         */
        public NettyVersionAccessors getNetty() {
            return vaccForNettyVersionAccessors;
        }

        /**
         * Returns the group of versions at versions.spring
         */
        public SpringVersionAccessors getSpring() {
            return vaccForSpringVersionAccessors;
        }

    }

    public static class CommonsVersionAccessors extends VersionFactory  {

        public CommonsVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: commons.fileupload (1.6.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getFileupload() { return getVersion("commons.fileupload"); }

            /**
             * Returns the version associated to this alias: commons.io (2.15.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getIo() { return getVersion("commons.io"); }

            /**
             * Returns the version associated to this alias: commons.lang3 (3.18.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLang3() { return getVersion("commons.lang3"); }

    }

    public static class FirebaseVersionAccessors extends VersionFactory  {

        public FirebaseVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: firebase.admin (9.3.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getAdmin() { return getVersion("firebase.admin"); }

    }

    public static class GrpcVersionAccessors extends VersionFactory  {

        public GrpcVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: grpc.netty (1.75.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getNetty() { return getVersion("grpc.netty"); }

    }

    public static class JavaVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        public JavaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the version associated to this alias: java (21)
         * If the version is a rich version and that its not expressible as a
         * single version string, then an empty string is returned.
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("java"); }

            /**
             * Returns the version associated to this alias: java.jwt (3.9.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJwt() { return getVersion("java.jwt"); }

    }

    public static class NettyVersionAccessors extends VersionFactory  {

        private final NettyCodecVersionAccessors vaccForNettyCodecVersionAccessors = new NettyCodecVersionAccessors(providers, config);
        public NettyVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: netty.handler (4.1.118.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHandler() { return getVersion("netty.handler"); }

        /**
         * Returns the group of versions at versions.netty.codec
         */
        public NettyCodecVersionAccessors getCodec() {
            return vaccForNettyCodecVersionAccessors;
        }

    }

    public static class NettyCodecVersionAccessors extends VersionFactory  implements VersionNotationSupplier {

        public NettyCodecVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the version associated to this alias: netty.codec (4.1.125.Final)
         * If the version is a rich version and that its not expressible as a
         * single version string, then an empty string is returned.
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> asProvider() { return getVersion("netty.codec"); }

            /**
             * Returns the version associated to this alias: netty.codec.http (4.1.129.Final)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getHttp() { return getVersion("netty.codec.http"); }

    }

    public static class SpringVersionAccessors extends VersionFactory  {

        private final SpringDependencyVersionAccessors vaccForSpringDependencyVersionAccessors = new SpringDependencyVersionAccessors(providers, config);
        public SpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: spring.boot (3.5.10)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getBoot() { return getVersion("spring.boot"); }

            /**
             * Returns the version associated to this alias: spring.cloud (2024.0.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getCloud() { return getVersion("spring.cloud"); }

            /**
             * Returns the version associated to this alias: spring.framework (6.2.11)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getFramework() { return getVersion("spring.framework"); }

            /**
             * Returns the version associated to this alias: spring.kafka (3.2.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKafka() { return getVersion("spring.kafka"); }

        /**
         * Returns the group of versions at versions.spring.dependency
         */
        public SpringDependencyVersionAccessors getDependency() {
            return vaccForSpringDependencyVersionAccessors;
        }

    }

    public static class SpringDependencyVersionAccessors extends VersionFactory  {

        public SpringDependencyVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: spring.dependency.mgmt (1.1.7)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMgmt() { return getVersion("spring.dependency.mgmt"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {
        private final SpringPluginAccessors paccForSpringPluginAccessors = new SpringPluginAccessors(providers, config);

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Returns the group of plugins at plugins.spring
         */
        public SpringPluginAccessors getSpring() {
            return paccForSpringPluginAccessors;
        }

    }

    public static class SpringPluginAccessors extends PluginFactory {

        public SpringPluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Creates a plugin provider for spring.boot to the plugin id 'org.springframework.boot'
             * with versionRef 'spring.boot'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getBoot() { return createPlugin("spring.boot"); }

            /**
             * Creates a plugin provider for spring.dependency to the plugin id 'io.spring.dependency-management'
             * with versionRef 'spring.dependency.mgmt'.
             * This plugin was declared in catalog libs.versions.toml
             */
            public Provider<PluginDependency> getDependency() { return createPlugin("spring.dependency"); }

    }

}
