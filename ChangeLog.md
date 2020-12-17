# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2020-12-17

### Added

- New classes and interfaces to simplify the request and response handling.
- Support for the new **hosts file** format (Revision 1). The new format adds a numeric **TYPE** column. The old format remains supported.

### Changed

- Throw meaningful exceptions.
- Simplify the interfaces.
- Clean up the magic constants.
- Update the documentation.

### Fixed

- The *password hash rounds* field of the private key generator should be immutable.

### Security

- Improve sanity checking of the response messages.
- Upgrade the JUnit version in pom.xml to fix a security issue with the older version.

## [0.1.0] - 2020-11-25

First public release.

### Added

- Wanhive EndPoint development library in java
