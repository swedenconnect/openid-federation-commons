# Resuable workflows

## Review

**Parameters**

| Key                      | Description                                                         | Default Value |
|--------------------------|---------------------------------------------------------------------|---------------|
| inputs.require-gh-docker | Set to true if review requires access to other github docker images | false         | 

## Docker Image Deploy

| Key                  | Description                                                                                                      | Default Value |
|----------------------|------------------------------------------------------------------------------------------------------------------|---------------|
| inputs.service-path  | Required path for the service                                                                                    | ''            |
| inputs.frontend-path | Optional path for frontend of the services, setting this parameter will embed the frontend into the docker image | ''            |

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
    uses: swedenconnect/openid-federation-commons/.github/workflows/resuable-publish.yml@main
    with:
      service-path: openid-relying-party-demo
      frontend-path: openid-relying-party-demo/frontend
 ```

### Review
```yaml
name: Automated review steps
on:
  pull_request:
    types: [opened, synchronize, reopened]

jobs:
  publish-docker:
    uses: swedenconnect/openid-federation-commons/.github/workflows/resuable-review.yml@main
```