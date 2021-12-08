# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.10.0] - 2021-12-08

### Changed

- Major package update (not backward compatible).

## [0.9.0] - 2021-11-18

### Added

- The **Message.packets** method that returns the number of messages required to transmit a chunk of data.

## [0.8.0] - 2021-04-16

### Changed

- Rename **Executor.hasMessage** to **Executor.hasIncomingMessage**.
- Update documentation.

## [0.7.0] - 2021-03-06

### Changed

- Limit the time allowed to establish a socket connection (connect timeout) in **WanhiveClient.connect**.
- Clean up the example client.
- Update the documentation.

## [0.6.0] - 2021-02-18

### Added

- A hosts file template.

### Changed

- Rename the **WHClient** class to **WanhiveClient**.
- Update the documentation.

### Fixed

- Refactor the **Message** class to fix the CodeQL (static code analyzer) build failure.

## [0.5.0] - 2021-02-01

### Added

- The pathname **WanhiveHosts.IN_MEMORY** for in-memory database.

### Changed

- Major package restructuring (not backward compatible) to ensure proper encapsulation.
- Setters in the **Message** class use the builder pattern.
- **Message.MTU** is the default length of the new Messages.

### Removed

- The **Message.freeze** method.
- The public access modifier of the **Message.getBuffer** method.

### Fixed

- **WHClient.receive** should return internally consistent message.

## [0.4.0] - 2021-01-16

### Changed

- Refactor the **ClientFactory** class.

### Fixed

- Use type casts for narrow conversions.

## [0.3.0] - 2021-01-01

### Added

- **Client.execute** method that sends out a request to the remote host and retrieves the response. Previously, the same process required two separate calls (send and receive).
- Throw exceptions with detail message.

### Changed

- **ObjectSerializer**, **ClientFactory** and **Executor** implementations.
- The public method names of **ProfileMap**.

### Fixed

- **Executor** should silently discard all the incoming messages if no **Receiver** provided.

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
