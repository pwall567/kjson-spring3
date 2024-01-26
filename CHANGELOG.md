# Change Log

The format is based on [Keep a Changelog](http://keepachangelog.com/).

The major and minor version numbers of this repository (but not patch numbers) match the version numbers of the
[`kjson`](https://github.com/pwall567/kjson) library used by this Spring library.

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
