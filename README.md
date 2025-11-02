# Android Stem

[![version](https://img.shields.io/gradle-plugin-portal/v/com.likethesalad.stem)](https://plugins.gradle.org/plugin/com.likethesalad.stem)

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/donate/?hosted_button_id=VJWEQ767PEYCA)
[<img src="https://cdn.ko-fi.com/cdn/kofi2.png?v=3" alt="ko-fi" width="100px"/>](https://ko-fi.com/N4N31QU53)

---

## What It Is

**Android Stem** is a Gradle plugin that resolves placeholders in XML string resources at **compile time**.  
No Java or Kotlin code is required — the plugin generates fully resolved strings accessible just like any other resource
in your app.

If you want to do this:

#### Input

```xml

<resources>
    <string name="app_name">My App Name</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

#### Output

```xml
<!-- Auto-generated during compilation -->
<resources>
    <string name="welcome_message">Welcome to My App Name</string>
</resources>
```

— without writing any additional code, **Android Stem** is for you.

---

## How to Use

### 1. Add It to Your Project

Add the plugin to your Android application's `build.gradle[.kts]` file:

```kotlin
plugins {
    id("com.android.application") version "8.4.0" // Minimum supported AGP version
    id("com.likethesalad.stem") version "LATEST_VERSION"
}
```

Find the latest version [here](https://plugins.gradle.org/plugin/com.likethesalad.stem).

---

### 2. Add Templates

A **template** is a string that references one or more placeholders.  
You can define templates in your app's `res` directories or import them from external libraries.

#### Defining Templates in Your App

```xml

<resources>
    <string name="app_name">My App Name</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

The string `welcome_message` is a **template** because it includes a placeholder (`${app_name}`).  
Templates and regular strings can coexist in the same XML files, such as `values/strings.xml`, or in separate ones.

**Localization** and **flavor-specific templates** are supported — you can define them under:

- `main/res/values-[lang]/` for localized templates, or
- `[flavor]/res/values/` for variant-specific templates.

Templates can also reference other templates, which will be resolved recursively.

> Both values and templates can be overridden per **language** or **flavor**.  
> For instance, if you override `app_name` in a flavor's `values` folder, all templates using `${app_name}` will reflect
> the new value.  
> Similarly, language-specific overrides (e.g., `values-es`) can provide localized template translations.

---

#### Using Strings from Libraries

You can import templates from other modules or external libraries by defining them as `stemProvider` dependencies:

```kotlin
plugins {
    id("com.likethesalad.stem") version "LATEST_VERSION"
}

dependencies {
    stemProvider(project(":my-submodule-with-templates"))
    stemProvider("an.external.lib:with-templates:0.0.0")
}
```

---

### 3. Running It

The placeholder resolution process runs automatically during your app's compilation.  
You can trigger it in multiple ways:

- **In Android Studio:**
    - Click the **Run** button ![Play button](./assets/run_button.png "Play button")
    - Or click the **Make Project** button ![Make button](./assets/make_button.png "Make button")
- **From the command line:**
  ```bash
  ./gradlew build
  # or
  ./gradlew assemble
  # or to run only the placeholder task:
  ./gradlew [BUILD_VARIANT]ResolvePlaceholders
  ```

More details under [Running It Manually](#running-it-manually).

---

## Configuration

Stem's default configuration should suit most projects, but you can adjust its behavior via your
module's `build.gradle[.kts]`:

```kotlin
androidStem {
    // By default, Stem searches for templates only within "values" to use as a reference for all languages.
    // Enable this flag if your translations (e.g., values-es) contain templates not present in the default folder.
    includeLocalizedOnlyTemplates = false // Default: false

    // Customize the placeholder format.
    placeholder {
        start = "\${" // Default
        end = "}"     // Default
    }
}
```

> Enabling `includeLocalizedOnlyTemplates` may slow down processing for large projects.

---

## Use Case Examples

### 1. Simple Case

```xml
<!-- strings.xml -->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

Output:

```xml
<!-- Auto-generated -->
<resources>
    <string name="welcome_message">Welcome to Test</string>
</resources>
```

---

### 2. Multiple Files

```xml
<!-- strings.xml -->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
    <string name="app_version_name">The version for ${app_name} is ${my_version}</string>
</resources>
```

```xml
<!-- my_configs.xml -->
<resources>
    <string name="my_version">1.0.0</string>
</resources>
```

Output:

```xml
<!-- Auto-generated -->
<resources>
    <string name="app_version_name">The version for Test is 1.0.0</string>
    <string name="welcome_message">Welcome to Test</string>
</resources>
```

---

### 3. Multiple Languages

Default:

```xml
<!-- values/strings.xml -->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

Spanish:

```xml
<!-- values-es/any_file.xml -->
<resources>
    <string name="welcome_message">Bienvenido a ${app_name}</string>
</resources>
```

Output:

```xml
<!-- values -->
<string name="welcome_message">Welcome to Test</string>
```

```xml
<!-- values-es -->
<string name="welcome_message">Bienvenido a Test</string>
```

---

### 4. Flavors

Default:

```xml

<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
    <string name="app_version_name">The version for ${app_name} is ${my_version}</string>
    <string name="my_version">1.0.0</string>
</resources>
```

Flavor (`demo`):

```xml

<resources>
    <string name="app_name">Demo app</string>
</resources>
```

Output for `demo` build:

```xml
<!-- Auto-generated -->
<resources>
    <string name="app_version_name">The version for Demo app is 1.0.0</string>
    <string name="welcome_message">Welcome to Demo app</string>
</resources>
```

> You can override both **values** and **templates** in flavors or localized folders — all combinations are supported.

---

## Running It Manually

To run only the placeholder resolution task (without building the app):

```bash
./gradlew [BUILD_VARIANT]ResolvePlaceholders
```

Examples:

```bash
./gradlew debugResolvePlaceholders
./gradlew demoDebugResolvePlaceholders
```

---

## Donations ♥

If you find this plugin useful, please consider supporting its development — even a small contribution helps maintain
and improve it.  
If you can't donate, sharing it with your developer friends is also a great way to help!

```xml

<string>"Thanks for your support, ${your_beautiful_name}!"</string>
```

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/donate/?hosted_button_id=VJWEQ767PEYCA)
[<img src="https://cdn.ko-fi.com/cdn/kofi2.png?v=3" alt="ko-fi" width="100px"/>](https://ko-fi.com/N4N31QU53)

---

## License

    MIT License
    
    Copyright (c) 2019 LikeTheSalad.
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
