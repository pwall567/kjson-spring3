# Change Log

The format is based on [Keep a Changelog](http://keepachangelog.com/).

The major and minor version numbers of this repository (but not patch numbers) match the version numbers of the
[`kjson`](https://github.com/pwall567/kjson) library used by this Spring library.

## [9.9] - 2025-06-10
### Changed
- `pom.xml`: updated dependency versions

## [9.7] - 2025-03-20
### Changed
- `pom.xml`: updated dependency versions
- tests: switched to use `log-front-testk` library

## [9.6] - 2025-02-09
### Changed
- `pom.xml`: updated dependency versions

## [9.5] - 2025-02-03
### Changed
- `pom.xml`: updated Kotlin version to 2.0.21, updated dependency versions

## [9.3] - 2024-12-18
### Changed
- `pom.xml`: updated dependency version

## [9.2] - 2024-12-17
### Changed
- `pom.xml`: Updated version of kjson
- tests : switched to `should-test` library

## [9.1] - 2024-11-03
### Added
- `build.yml`, `deploy.yml`: converted project to GitHub Actions
### Changed
- `pom.xml`: Updated version of kjson
- `pom.xml`: updated Kotlin version to 1.9.24
### Removed
- `.travis.yml`

## [7.5] - 2024-02-14
### Changed
- `JSONSpring`: Improved logging (always log input that fails parsing as level ERROR)
- `pom.xml`: Updated version of kjson

## [7.1.3] - 2024-01-26
### Added
- `org.springframework.boot.autoconfigure.AutoConfiguration.imports`: New autoconfiguration file for Spring Boot 3
  (many thanks to Yixiong Jiang)

## [7.1.2] - 2023-12-14
### Changed
- `JSONSpringAutoConfiguration`: Added priority annotation

## [7.1.1] - 2023-12-14
### Added
- `JSONSpringAutoConfiguration`: Remove the requirement for `@componentScan`
### Changed
- `JSONSpring`: Change to use new auto-configuration

## [7.1] - 2023-10-15
### Changed
- `pom.xml`: Updated version of kjson

## [7.0] - 2023-07-31
### Changed
- `JSONSpring`: switched to use simpler parse function
- `pom.xml`: Updated version of kjson

## [6.1] - 2023-07-25
### Changed
- `pom.xml`: Updated version of kjson

## [6.0] - 2023-07-09
### Changed
- `pom.xml`: Updated version of kjson

## [5.0] - 2023-07-07
### Changed
- `pom.xml`: Updated version of kjson

## [4.4.2] - 2023-06-14
### Added
- all: Initial version (copied from `kjson-spring`)
### Changed
- `pom.xml`: Updated versions of Spring, Spring Boot and Java
