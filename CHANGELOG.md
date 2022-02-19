Change Log
==========

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

 * New: Added new configuration parameter `useDependenciesRes` that allows to gather strings from the app's dependencies when resolving strings.

Version 1.1.0 *(05-01-2020)*
---

 * New: Generating resolved.xml files inside the `app/build` folder by default and so keeping them hidden from the working dir and the VCS.
 * New: Improved flavors support to now work with multi-dimensional flavors.