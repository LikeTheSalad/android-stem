Change Log
==========

<!-- CHANGELOG_INSERT -->

## Version 3.3.1 (2026-06-01)

* Update dependency org.xmlunit:xmlunit-core to v2.12.0 ([#309](https://github.com/LikeTheSalad/android-stem/pull/309))
* Update dependency io.mockk:mockk to v1.14.11 ([#308](https://github.com/LikeTheSalad/android-stem/pull/308))
* Update plugin wire to v6.4.0 ([#307](https://github.com/LikeTheSalad/android-stem/pull/307))
* Update plugin wire to v6.3.0 ([#306](https://github.com/LikeTheSalad/android-stem/pull/306))
* Update Gradle to v9.5.1 ([#305](https://github.com/LikeTheSalad/android-stem/pull/305))
* Update dependency com.android.tools.build:gradle to v9.2.1 ([#304](https://github.com/LikeTheSalad/android-stem/pull/304))
* Update android to v9.2.1 ([#303](https://github.com/LikeTheSalad/android-stem/pull/303))

## Version 3.3.0 (2026-05-01)

* Update Gradle to v9.5.0 ([#299](https://github.com/LikeTheSalad/android-stem/pull/299))
* Update dependency org.junit:junit-bom to v5.14.4 ([#298](https://github.com/LikeTheSalad/android-stem/pull/298))
* Update dependency com.google.code.gson:gson to v2.14.0 ([#296](https://github.com/LikeTheSalad/android-stem/pull/296))
* Update plugin kotlin to v2.3.21 ([#295](https://github.com/LikeTheSalad/android-stem/pull/295))
* Update dependency com.android.tools.build:gradle to v9.2.0 ([#294](https://github.com/LikeTheSalad/android-stem/pull/294))
* Update android to v9.2.0 ([#293](https://github.com/LikeTheSalad/android-stem/pull/293))
* Update dependency com.android.tools.build:gradle to v9.1.1 ([#292](https://github.com/LikeTheSalad/android-stem/pull/292))
* Update android to v9.1.1 ([#291](https://github.com/LikeTheSalad/android-stem/pull/291))
* Adding permissions ([#290](https://github.com/LikeTheSalad/android-stem/pull/290))
* Clean up ([#289](https://github.com/LikeTheSalad/android-stem/pull/289))
* Migrate to GitHub tools ([#288](https://github.com/LikeTheSalad/android-stem/pull/288))

### Features

* Adding Gradle config cache support ([#300](https://github.com/LikeTheSalad/android-stem/pull/300))

## Version 3.2.0 (2026-04-04)

* Making tasks cacheable
* Bumping up dependencies

## Version 3.1.1 (2026-01-21)

* Fix bug #264
* Clean up internal utils

## Version 3.1.0 (2026-01-18)

* Fix bug #255 by ensuring that "stemProvider" dependencies are not transitive.

## Version 3.0.0 (2025-11-02)

* [BREAKING] Removed `com.likethesalad.stem-library` plugin for libraries. It's replaced by adding those libraries
  as `stemProvider` dependencies on the consumer app.
* [BREAKING] Minimum Android Gradle Plugin version required: `8.4.0`.
* [BREAKING] The resolver task has been renamed from `resolve[variant]Placeholders` to `[variant]ResolvePlaceholders`.
* Improved stability by relying on new AGP apis that are better suited to Stem's use case.
* Improved placeholder extension config via Kotlin scripts.

## Version 2.12.0 (2024-11-10)

* Adding placeholder delimiter customization config params.

Version 2.10.1 *(04-08-2024)*
---

* Supporting strings with html tags that contain quotes.
* Keeping quotes as they are defined in the original strings.

Version 2.10.0 *(28-07-2024)*
---

* Keeping string body html tags.

Version 2.9.0 *(05-05-2024)*
---

* Adding explicit dependency on ExtractSupportedLocalesTask.

Version 2.8.0 *(15-03-2024)*
---

* Supporting tasks added in AGP 8.3.0

Version 2.7.0 *(07-01-2024)*
---

* Updating utilities.
* Refactor to adapt to new utilities.
* Using version catalogs.

Version 2.5.0 *(15-08-2023)*
---

* Fix #43 by adding support to AGP's 7.4.0+ APIs.

Version 2.4.1 *(20-05-2023)*
---

* Updating resource locator to gather generated resources lazily.

Version 2.4.0 *(26-04-2023)*
---

* Made Stem aware of mapSourceSetPaths task (Fix #39)

Version 2.3.0 *(07-01-2023)*
---

* Made Stem aware of extractDeeplinks task
* Improving AGP 7.3+ sourceSet set up
* Improving tests
* Clean up deprecated code

Version 2.2.3 *(09-10-2022)*
---

* Fix #33

Version 2.2.2 *(31-08-2022)*
---

* Fix #27 by supporting Windows' File path separators when filtering input resources.

Version 2.2.1 *(27-08-2022)*
---

* Fix #29

Version 2.2.0 *(15-08-2022)*
---

* Fix #26 by supporting string resources with custom namespaces.

Version 2.1.0 *(16-05-2022)*
---

* Added configuration param `includeLocalizedOnlyTemplates` ref #21.
* Fix #20.
* Fix #22.

Version 2.0.0 *(19-02-2022)*
---

* Rebrand to "Stem".
* Removed "template_" prefix.
* Created library templates provider plugin.
* Removed configuration params.

Version 1.3.0 *(19-12-2021)*
---

* Fix: #10 keeping generic strings outside of language specific resolved files.
* New: Raw resource collection has been moved outside this project.
* New: Deprecated `keepResolvedFiles` and `useDependenciesRes` configurations.
* New: Updated minimum Android Gradle Plugin version to 5.6.4.

Version 1.2.2 *(11-04-2021)*
---

* New: Reorganizing modules.
* Fix: Injecting tasks' dependencies to avoid compatibility issues with Gradle 7.

Version 1.2.1 *(25-08-2020)*
---

* New: Added support for gradle generated strings.

Version 1.2.0 *(09-05-2020)*
---

* New: Added new configuration parameter `useDependenciesRes` that allows to gather strings from the app's dependencies
  when resolving strings.

Version 1.1.0 *(05-01-2020)*
---

* New: Generating resolved.xml files inside the `app/build` folder by default and so keeping them hidden from the
  working dir and the VCS.
* New: Improved flavors support to now work with multi-dimensional flavors.
