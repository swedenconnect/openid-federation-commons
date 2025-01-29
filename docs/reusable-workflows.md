# Reusable workflows

## Review

**Parameters**

| Key                      | Description                                                         | Default Value |
|--------------------------|---------------------------------------------------------------------|---------------|
| inputs.require-gh-docker | Set to true if review requires access to other github docker images | false         | 

## Docker Image Deploy

**Parameters**


| Key                  | Description                                                                                                      | Default Value |
|----------------------|------------------------------------------------------------------------------------------------------------------|---------------|
| inputs.service-path  | Required path for the service                                                                                    | ''            |
| inputs.frontend-path | Optional path for frontend of the services, setting this parameter will embed the frontend into the docker image | ''            |

## Maven release (to GitHub)

**Parameters**


| Key                         | Description                                               | Default Value |
|-----------------------------|-----------------------------------------------------------|---------------|
| inputs.auto-update-revision | Updates revision property and push to main if set to true | false         |

## Examples

### Publish

```yaml
name: Publication of Relying Party docker image
on:
  push:
    branches:
      - main
    paths:
      - 'openid-relying-party-demo/**'
jobs:
  publish-docker:
    uses: swedenconnect/openid-federation-commons/.github/workflows/reusable-docker-publish.yml@main
    with:
      service-path: openid-relying-party-demo
      frontend-path: openid-relying-party-demo/frontend
 ```

### Review

```yaml
name: Automated review steps
on:
  pull_request:
    types: [ opened, synchronize, reopened ]

jobs:
  publish-docker:
    uses: swedenconnect/openid-federation-commons/.github/workflows/reusable-review.yml@main
```

### Maven Release (to GitHub)

```yaml
name: Automated release of maven artifacts
on:
  push:
    tags:
      - 'v*'
jobs:
  release:
    uses: swedenconnect/openid-federation-commons/.github/workflows/reusable-mvn-release.yml@main
    secrets: inherit
    with:
      auto-update-revision: true
```