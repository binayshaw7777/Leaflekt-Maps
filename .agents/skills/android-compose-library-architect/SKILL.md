---
name: android-compose-library-architect
description: Shape reusable library APIs for Compose modules. Design public composables, split sample-app code from reusable library code, review naming and extensibility.
---

# Android Compose Library Architect

Purpose:
- Shape reusable library APIs for Compose modules.

When to use:
- Designing public composables
- Splitting sample-app code from reusable library code
- Reviewing naming and extensibility

Workflow:
1. Inspect current public API surface.
2. Prefer config/data classes over long unstructured argument lists.
3. Preserve future extension points for accessibility, animation, and publishing.
4. Keep sample-only concerns out of the library.

Outputs:
- API proposal
- File ownership list
- Risks and follow-up items
