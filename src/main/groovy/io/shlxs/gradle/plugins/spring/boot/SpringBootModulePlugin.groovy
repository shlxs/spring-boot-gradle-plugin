package io.shlxs.gradle.plugins.spring.boot

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPlugin
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class SpringBootModulePlugin implements Plugin<ProjectInternal> {
    public static final String PROPERTY_SPRING_BOOT_VERSION = 'springBootVersion'
    public static final String VERSION_LATEST_RELEASE = 'latest.release'

    @Override
    void apply(ProjectInternal project) {
        project.configure(project) {
            project.plugins.apply(JavaPlugin.class)
            project.plugins.apply(SpringBootPlugin.class)
            applyMavenBom(project)
            applyBaseDependencies(project)
            applyConfigurationProcessor(project)
        }
    }

    private static applyMavenBom(ProjectInternal project) {
        final springBootVersion = project.hasProperty(PROPERTY_SPRING_BOOT_VERSION) ?
                project.property(PROPERTY_SPRING_BOOT_VERSION) : (
                project.hasProperty('spring-boot.version') ? project.property('spring-boot.version') : VERSION_LATEST_RELEASE
        )

        final dependencyManagementExtension = project.extensions.getByType(DependencyManagementExtension.class)
        dependencyManagementExtension.imports({
            mavenBom "org.springframework.boot:spring-boot-dependencies:${springBootVersion}"
        })
    }

    private static applyBaseDependencies(ProjectInternal project) {
        project.dependencies {
            implementation('org.springframework.boot:spring-boot-starter-web') {
                exclude group: 'org.springframework.boot', module: 'spring-boot-starter-tomcat'
            }
            implementation 'org.jolokia:jolokia-core'
            implementation 'io.micrometer:micrometer-registry-prometheus'
            implementation 'org.springframework.boot:spring-boot-starter-jetty'
            implementation 'org.springframework.boot:spring-boot-starter-actuator'
            testImplementation 'org.springframework.boot:spring-boot-starter-test'
        }
    }

    private static applyConfigurationProcessor(ProjectInternal project) {
        project.dependencies {
            compileOnly('org.springframework.boot:spring-boot-configuration-processor')
            compileOnly('org.projectlombok:lombok')
            annonatitonProcessor('org.projectlombok:lombok')
            testCompileOnly('org.projectlombok:lombok')
            testAnnotationProcessor('org.projectlombok:lombok')
        }
    }

}