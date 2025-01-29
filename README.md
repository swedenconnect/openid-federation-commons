# OpenID Federation Commons

This repository contains modules for openid federation.

## Quickstart - Modules

Add GitHub repository
```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo1.maven.org/maven2</url>
    </repository>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/swedenconnect/*</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```

To start using openid-federation you can either use the spring boot parent module.

```xml

<parent>
    <groupId>se.swedenconnect.oidf</groupId>
    <artifactId>openid-federation-parent</artifactId>
    <version>check-latest-version</version>
    <relativePath/>
</parent>
```

OR import it to get versions

```xml

<dependencyManagement>
    <dependency>
        <groupId>se.swedenconnect.oidf</groupId>
        <artifactId>openid-federation-parent</artifactId>
        <version>check-latest-version</version>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

```xml
<dependencies>
    <dependency>
        <groupId>se.swedenconnect.oidf</groupId>
        <artifactId>client</artifactId>
    </dependency>
</dependencies>
```