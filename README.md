# quarkus-bug-15014 project

This is a reproducer of the bug https://github.com/quarkusio/quarkus/issues/15014.

`./mvnw clean verify -Dquarkus.version=1.11.2.Final` // Succeed

`./mvnw clean verify -Dquarkus.version=1.12.0.CR1` // Fails

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/.