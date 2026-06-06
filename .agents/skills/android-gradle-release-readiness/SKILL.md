---
name: android-gradle-release-readiness
description: Keep module structure, naming, and build configuration ready for future publishing. Handle new Gradle modules, library metadata, namespace and artifact readiness.
---

# Android Gradle Release Readiness

Purpose:
- Keep module structure, naming, and build configuration ready for future publishing.

When to use:
- Adding new Gradle modules
- Preparing library metadata
- Reviewing namespace and artifact readiness

Workflow:
1. Confirm module boundaries.
2. Keep reusable dependencies inside the library and sample-only ones in the app.
3. Prepare package/namespace choices that will not need churn later.
4. Verify buildability after structural changes.

Outputs:
- Gradle change list
- Packaging notes
- Verification results
