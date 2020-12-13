# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Classes and Interfaces to simplify request and response handling.
- The new **hosts file** format. The new format adds a numeric **TYPE** column. The old format remains supported.

### Changed

- Simplify the public interfaces.
- Update the code documentation.
- Clean up the magic constants.
- Rename and update the code of conduct file.

### Fixed

- SRP-6a based private key generator should not modify it's password hash rounds field.

### Security

- Strict sanity checking of the response messages.
- Upgrade the junit version in pom.xml to fix a security issue with the older version.

## [0.1.0] - 2020-11-25

First public release.

### Added

- Wanhive EndPoint development library in java