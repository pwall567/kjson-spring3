# kjson-spring3

[![Build Status](https://github.com/pwall567/kjson-spring3/actions/workflows/build.yml/badge.svg)](https://github.com/pwall567/kjson-spring3/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/static/v1?label=Kotlin&message=v2.0.21&color=7f52ff&logo=kotlin&logoColor=7f52ff)](https://github.com/JetBrains/kotlin/releases/tag/v2.0.21)
[![Maven Central](https://img.shields.io/maven-central/v/io.kjson/kjson-spring3?label=Maven%20Central)](https://central.sonatype.com/artifact/io.kjson/kjson-spring3)

Spring Boot 3 JSON message converter for [`kjson`](https://github.com/pwall567/kjson).

## Background

Many users of the [`kjson`](https://github.com/pwall567/kjson) library will seek to use the library in conjunction with
the [Spring Framework](https://spring.io/projects/spring-framework).
The `kjson-spring3` library provides a JSON converter class that will use `kjson` as the serialization / deserialization
mechanism.

This library is a copy of the [`kjson-spring`](https://github.com/pwall567/kjson-spring) library, converted to Spring
Boot 3.x and Spring 6.x.

## Quick Start

**NOTE:** Version 7.1.1 of this library uses the Spring Boot Auto-Configuration mechanism, meaning that the references
to component scanning in the notes below may be ignored (and will be removed from future versions).

To direct a Spring Boot 3 application to use `kjson` for serialization and deserialization of HTTP(S) requests and
responses, simply include the `kjson-spring3` library in the build, and then ensure that the `io.kjson.spring` package
is included in the Spring component scan:
```kotlin
@ComponentScan("io.kjson.spring")
```
or:
```kotlin
@ComponentScan(basePackageClasses = [JSONSpring::class])
```
or, when using Spring XML configuration:
```xml
    <context:component-scan base-package="io.kjson.spring"/>
```
Note that the default Spring behaviour is to scan the package in which the `@ComponentScan`
(or `@SpringBootApplication`) occurs.
When one or more packages or classes are specified on a `@ComponentScan`, only the specified package(s) will be scanned;
to retain the default behaviour, the current package must be also specified, along with `io.kjson.spring`.

## Configuration

The `kjson` serialization and deserialization functions all take an optional
[`JSONConfig`](https://github.com/pwall567/kjson/blob/main/USERGUIDE.md#configuration) object.
The `JSONConfig` to be used by the `kjson-spring3` library may be provided in the usual Spring manner:
```kotlin
@Configuration
open class SpringAppConfig {

    @Bean open fun jsonConfig(): JSONConfig {
        return JSONConfig {
            allowExtra = true
        }
    }

}
```
This example shows just the `allowExtra` option being set; any of the configuration options, including custom
serialization and deserialization, may be used.
If no `JSONConfig` is provided, the `JSONConfig.defaultConfig` will be used.

## Client REST Calls

Client REST calls using the `RestTemplate` class can also make use of the `kjson` serialization and deserialization
functions.
When the `RestTemplate` is obtained from the default `RestTemplateBuilder`, it will be configured with all of the
`HttpMessageConverter` instances currently configured &ndash; that will include the `kjson` converter added during
auto-configuration.

The following line will get the default `RestTemplateBuilder`:
```kotlin
    @Autowired lateinit var restTemplateBuilder: RestTemplateBuilder
```
And then, the following will get a `RestTemplate` with the converters configured in the `RestTemplateBuilder`:
```kotlin
        val restTemplate = restTemplateBuilder.build()
```

Alternatively, the `RestTemplate` may be constructed with the converter specified explicitly:
```kotlin
    @Autowired lateinit var jsonSpring: JSONSpring
```
and:
```kotlin
        val restTemplate = RestTemplate(listOf(jsonSpring))
```

## Logging

`kjson-spring` can be configured to log all messages processed by the JSON converter, input and output.
To make use of this functionality, the `jsonLogFactory` must be configured, as follows:
```kotlin
    @Bean open fun jsonLogFactory(): LoggerFactory<*> = getDefaultLoggerFactory()
```

The `LoggerFactory` in this case is from the [log-front-api](https://github.com/pwall567/log-front-api) library;
this is a logging fa&ccedil;ade library which will delegate to any of a number of implementations.
The example above uses the default logger factory class from the
[log-front-kotlin](https://github.com/pwall567/log-front-kotlin) library, and the usual implementation uses `slf4j` if
those classes are present (they generally are in a Spring application), or the Java Logging framework if a configuration
file for that system is specified, or if no other logging mechanism is available, logs to the standard output.

For the majority of users who do not wish to learn another logging library, specifying the log factory as in the example
above will meet most requirements.

The name of the `Logger` may also be specified:
```kotlin
    @Bean open fun jsonLogName(): String = "JSON Logging"
```
The default name is the name of the class instantiating the `Logger`, which in this case is
`io.kjson.spring.JSONSpring`.

And the `Level` to be used when logging the messages may be specified by:
```kotlin
    @Bean open fun jsonLogLevel(): Level = Level.INFO
```
The default is `DEBUG`, but when errors are encountered in deserialization, the input will be logged with `Level`
`ERROR`, followed by the error message, regardless of the level specified in the above bean.

## Log Eliding

When logging JSON content, it is often important to ensure that Personally Identifiable Information (PII) is not
included in the logged data.
The logging functionality of `kjson-spring` allows the specification of an optional "exclude" set of property names.
The values of properties with these names will be replaced by `****` in the logs, wherever they appear in a JSON
structure.
```kotlin
    @Bean open fun jsonLogExclude(): Collection<String> = setOf("creditCardNumber", "licenceNumber")
```

The following will log all input and output with `Level` `INFO`, eliding all fields named `accountNumber`:
```kotlin
import org.springframework.context.annotation.Configuration
import io.kstuff.log.Level
import io.kstuff.log.Log
import io.kstuff.log.LogggerFactory

@Configuration
open class JSONConfiguration {
    @Bean open fun jsonLogFactory(): LoggerFactory<*> = getDefaultLoggerFactory()
    @Bean open fun jsonLogLevel(): Level = Level.INFO
    @Bean open fun jsonLogExclude(): Set<String> = setOf("accountNumber")
}
```

## Dependency Specification

The latest version of the library is 9.9 (the version number of this library matches the version of `kjson` with which
it was built), and it may be obtained from the Maven Central repository.

This version was built using version 6.2.7 of Spring, and version 3.5.0 of Spring Boot.

### Maven
```xml
    <dependency>
      <groupId>io.kjson</groupId>
      <artifactId>kjson-spring3</artifactId>
      <version>9.9</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'io.kjson:kjson-spring3:9.9'
```
### Gradle (kts)
```kotlin
    implementation("io.kjson:kjson-spring3:9.9")
```

Peter Wall

2025-06-10
