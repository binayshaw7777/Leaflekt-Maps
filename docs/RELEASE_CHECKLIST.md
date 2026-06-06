# Release Checklist

## Version

- [ ] `VERSION` bumped exactly once for the release
- [ ] version follows SemVer (`MAJOR.MINOR.PATCH`)
- [ ] release notes match the changes in this version

## Library Build

- [ ] `:leaflekt-core:build` passes
- [ ] `:leaflekt-compose:build` passes
- [ ] both modules publish to `mavenLocal`
- [ ] sources jars are generated

## Maven Central Readiness

- [ ] Sonatype credentials are configured
- [ ] signing secrets are configured
- [ ] `leaflekt-core` coordinates are stable
- [ ] `leaflekt-compose` coordinates are stable
- [ ] README uses Maven Central coordinates

## Documentation

- [ ] public API examples match the current code
- [ ] release install snippet is correct
- [ ] feature list matches current SDK behavior
- [ ] security policy exists
- [ ] repository license is explicit and committed
- [ ] known limitations are documented

## Security

- [ ] no secrets committed to the repository
- [ ] workflow permissions are scoped to minimum required access
- [ ] release automation runs only on `master`
- [ ] branch protection is enabled for `master`
- [ ] GitHub Actions allowed actions policy is reviewed
- [ ] GitHub private vulnerability reporting is enabled

## GitHub Repository Settings

- [ ] default branch is `master`
- [ ] Actions are enabled
- [ ] workflow permission allows `contents: write`
- [ ] maintainer can create releases

## Release Automation

- [ ] push to `master` with a new `VERSION` creates tag `v<version>`
- [ ] the workflow creates a GitHub Release for the same tag
- [ ] the workflow publishes both KMP artifacts
- [ ] repeated pushes without a new version do not create duplicate releases

## Post Release

- [ ] both Maven Central artifacts resolve successfully
- [ ] sample dependency install works in a fresh consumer project
- [ ] GitHub Release notes look correct
