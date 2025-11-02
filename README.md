![version](https://img.shields.io/maven-central/v/com.likethesalad.android/stem-plugin)
[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Android%20String%20XML%20Reference-green.svg?style=flat )]( https://android-arsenal.com/details/1/7967 )

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/donate/?hosted_button_id=VJWEQ767PEYCA)
[<img src="https://cdn.ko-fi.com/cdn/kofi2.png?v=3" alt="ko-fi" width="100px"/>](https://ko-fi.com/N4N31QU53)

# Android Stem

Table of Contents
---

What it is
---
Android Stem is a Gradle plugin that resolves placeholders
of XML strings referenced into other XML strings
at compile time. You won't have to write any Java or Kotlin code into your
project to make it work, and you will still be able to access to the 'resolved'
strings the same way as with any other manually added string to your
XML files.

In other words, if you're looking to do something like this:

#### Input:

```xml

<resources>
    <string name="app_name">My App Name</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

#### Output:

```xml
<!-- Auto generated during compilation -->
<resources>
    <string name="welcome_message">Welcome to My App Name</string>
</resources>
```

Without having to write any Java or Kotlin code, then Android Stem might help you.

How to use
---

### 1. Add it to your project
---

You'll need to add the Stem Gradle plugin into your Android application `build.gradle[.kts]` file:

```kotlin
plugins {
    id("com.android.application") version "8.4.0" // 8.4.0 or greater. The minimum supported AGP version is 8.4.0.
    id("com.likethesalad.stem") version "LATEST_VERSION"
}
```

You can find the latest version [here](https://plugins.gradle.org/plugin/com.likethesalad.stem).

### 2. Add templates

A template is a string that references one or more placeholders. You can define them within your application's res dirs,
or you can "import" them from external libraries, more details below.

#### Adding them into your application's `res` folder

```xml 

<resources>
    <string name="app_name">My App Name</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

The string `my_message` is a template, because it has a "placeholder" for the string named `app_name`. Templates
and placeholders can live in any XML file within your `values` folders, and can be defined amongst other, regular
strings. So for example, you can define templates within your existing `main/res/values/strings.xml` file, as with any
other string, or you can create a new XML file just for templates. It's up to you.

Localization and variant-aware templates are also supported. Meaning that you can define templates
within `main/res/values-[lang]/[some_file].xml`, to have localized templates per language, as well as
within `[flavor]/res/values/[some_file].xml`, to have variant-aware templates, in case your app has multiple flavors for
example.

Finally, you can even reference templates as placeholders for other templates. This will make the templates to get
resolved in order, so that no placeholders are left unresolved.

> Both **values and templates** can be overridden for a different **language** and
> also for a different **flavor**. So for example, if you have templates in your
> project with the app name placeholder (e.g. ${app_name}) and you
> need to create a flavor with a different app name value, you just have
> to override the '*app_name*' string inside the flavor's 'values' folder
> and that's it. Now for this flavor you'll get all the old strings but with
> the new app_name value.

> Same for languages, based on the example above, if you need to translate your
> '*my_message*' string to spanish for example, you just have to override the template
> '*my_message*', inside the 'values-es' folder, and you'll get the
> translated '*my_message*' string localized for the resolved resources.

#### Using strings from libraries

You might want to use/import strings from submodules or external Android libraries instead of having to define them
within your application's resources. To do so, you'll need to define those libraries as `stemProvider` dependencies into
your application's `build.gradle[.kts]` file, as shown below:

```kotlin
plugins {
    id("com.likethesalad.stem") version "LATEST_VERSION"
}

dependencies {
    stemProvider(project(":my-submodule-with-templates"))
    stemProvider("an.external.lib:with-templates:0.0.0")
}
```

### 3. Running it

The process that resolves the string templates is executed automatically during
your app's compilation process, therefore, there's many ways of running it. Some of those could be:

- By pressing on the "play" button of Android Studio: ![Play button](./assets/run_button.png "Play button")
- Or, by pressing on the "make" button on Android Studio: ![Make button](./assets/make_button.png "Make button")
- Or, if you prefer command line, then you can run it by calling the build command: `./gradlew build` or the assemble
  command: `./gradlew assemble` or by calling the specific task to resolve the strings which has the following
  format: `./gradlew [BUILD_VARIANT]ResolvePlaceholders` more info on this command below under
  "**Running it manually **".

Configuration
---

Stem should work out of the box as you'd expect it to, however, depending on each case, some projects
might have special needs for which some parts of how Stem works might need to be adjusted to meet those needs.
Stem can be configured in your `build.gradle[.kts]` file where Stem is applied, as shown below along with the currently
available configurable parameters.

```kotlin
// build.gradle.kts file where "com.likethesalad.stem" is applied.

androidStem {
    // Even though Stem resolves your templates no matter their localization, it searches for templates amongst your 
    // string resources within the folder "values" only by default (which will be taken as reference for other languages 
    // when resolving them).
    // This is because, ideally, if you have a string that will need a ${placeholder} in it, it should have it
    // inside any of that same string's different languages, including the default one ("values"). 
    // So in order to avoid searching for the same templates across different languages, which could mean
    // an expensive processing operation depending on the project, Stem by default only checks the default
    // strings when looking for templates, and then based on what it finds within the default strings, it applies
    // the placeholder resolving process to all other languages.
    //
    // However, in some cases some projects might have a string withing the "values" folder that has no ${placeholders}
    // in it, but its translations do (check issue #21 for context). For those cases, you
    // could enable this flag so that Stem looks for templates within string resources in any language-specific 
    // values folders as well. Please bear in mind that this might cause performance penalties.
    includeLocalizedOnlyTemplates = false // disabled by default


    // The "placeholder" config allows to change the format of placeholders which by default look like this: ${placeholder}.
    placeholder {
        start = "\${" // This is the default value.
        end = "}" // This is the default value
    }
}

```

Use case examples
---
Here's a couple of examples for some of the use cases supported by Android Stem

### 1. Simple use case

Within our `app/main/res/values` folder, we have the following file:

```xml
<!--strings.xml-->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

After building our project we get:

```xml
<!--Auto generated during compilation-->
<resources>
    <string name="welcome_message">Welcome to Test</string>
</resources>
```

### 2. Multi files use case

Within our `app/main/res/values` folder, we have the following files:

```xml
<!--strings.xml-->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
    <string name="app_version_name">The version for ${app_name} is ${my_version}</string>
</resources>
```

```xml
<!--my_configs.xml-->
<resources>
    <string name="my_version">1.0.0</string>
</resources>
```

After building our project we get:

```xml
<!--Auto generated during compilation-->
<resources>
    <string name="app_version_name">The version for Test is 1.0.0</string>
    <string name="welcome_message">Welcome to Test</string>
</resources>
```

So no matter which file contains a template or a value used in a template, as long as it's within your app's values
folders, then the plugin will find it.

### 3. Multi languages use case

Within our `app/main/res/values` folder, we have the following file:

```xml
<!--strings.xml-->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
</resources>
```

Then, Within our `app/main/res/values-es` folder, we have the following file:

```xml
<!--any_file.xml-->
<resources>
    <string name="welcome_message">Bienvenido a ${app_name}</string>
</resources>
```

After building our project, what we get for our default `values` folder is:

```xml
<!--Auto generated during compilation-->
<resources>
    <string name="welcome_message">Welcome to Test</string>
</resources>
```

And then what we get for our spanish `values-es` folder is:

```xml
<!--Auto generated during compilation-->
<resources>
    <string name="welcome_message">Bienvenido a Test</string>
</resources>
```

### 4. Flavors use case

Let's say we've defined a flavor in our project, named `demo`, then:

Within our `app/main/res/values` folder, we have the following files:

```xml
<!--strings.xml-->
<resources>
    <string name="app_name">Test</string>
    <string name="welcome_message">Welcome to ${app_name}</string>
    <string name="app_version_name">The version for ${app_name} is ${my_version}</string>
</resources>
```

```xml
<!--my_configs.xml-->
<resources>
    <string name="my_version">1.0.0</string>
</resources>
```

And for our `app/demo/res/values` folder we add the following file:

```xml
<!--any_file.xml-->
<resources>
    <string name="app_name">Demo app</string>
</resources>
```

After building the `demo` variant of our project, we'll get for such variant:

```xml
<!--Auto generated during compilation-->
<resources>
    <string name="app_version_name">The version for Demo app is 1.0.0</string>
    <string name="welcome_message">Welcome to Demo app</string>
</resources>
```

We see that the `app_name` value has been overridden by the demo's app_name, this doesn't only happen for values but
also for templates, we can also override templates within our demo's resources.

> Those were some of the use cases that you can achieve using Android Stem, there's more of them such as overriding
> flavors' multi languages from the base values folder and also working with multi-dimension flavors. You can play
> around with it, it all should work the way you'd expect it to work.

Running it manually
---
If you want to just run the gradle task that resolves
the templates without having to build your project, you can do so
by running: `[BUILD_VARIANT]ResolvePlaceholders` depending
on your build configuration. For example, to run it for the debug variant,
you'll have to run: `debugResolvePlaceholders`, or if you have flavors
set up in your application, e.g. say you have 'demo' as a flavor defined,
then you can run `demoDebugResolvePlaceholders` to generate the strings
for the demo flavor on the debug variant and so on.

Donations ♥
---
If this plugin is useful for you, and if it's within your possibilities, please consider making a one-off donation that
will help keeping the development of new features and bug support. And if you can't make a donation right now,
you could also support this plugin by sharing it with your dev friends and colleagues!

```xml

<string>"Thanks for your support, ${your_beautiful_name}!"</string>
```

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donate_LG.gif)](https://www.paypal.com/donate/?hosted_button_id=VJWEQ767PEYCA)
[<img src="https://cdn.ko-fi.com/cdn/kofi2.png?v=3" alt="ko-fi" width="100px"/>](https://ko-fi.com/N4N31QU53)

License
---

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
