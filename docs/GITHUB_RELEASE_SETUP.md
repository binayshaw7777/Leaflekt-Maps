# GitHub Release Setup

These settings must be configured in the GitHub repository. They cannot be enforced from the codebase alone.

## Branch

- set the default branch to `master`
- protect `master`
- require pull request review if your workflow needs review gates

## Actions

- enable GitHub Actions
- set workflow permissions to `Read and write permissions`
- allow `GITHUB_TOKEN` to create releases and tags

## Security

- enable private vulnerability reporting
- enable Dependabot alerts
- enable secret scanning, if available

## Maven Central

- configure Sonatype credentials and signing secrets
- verify both published coordinates:
  - `io.github.binayshaw7777:leaflekt-core:<version>`
  - `io.github.binayshaw7777:leaflekt-compose:<version>`

## Release Operation

1. bump `VERSION`
2. merge or push to `master`
3. wait for `.github/workflows/release-master.yml`
4. verify:
   - GitHub tag `v<version>`
   - GitHub Release
   - Maven Central artifacts
