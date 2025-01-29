# OpenID Federation Commons

This repository contains modules for openid federation.

## Quickstart - Modules

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