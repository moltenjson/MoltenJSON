[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/ReflxctionDev/SimpleJSON.svg)](https://jitpack.io/#ReflxctionDev/SimpleJSON)
# SimpleJSON
A simple wrapper for [Google GSON's API](https://github.com/google/gson), to simplify writing [JSON](http://json.org/) data and reading it

# Features
* Fast and efficient
* Easy reading from any JSON file
* Easy, clean and safe writing to any JSON file
* Ability to read JSON files from any URLs in fast and efficient ways
* Ability to write content from any JSON URLs locally in easy and safe ways


# Adding the dependency
## Maven

Add this to your **pom.xml**:
```xml
<repositories>
    <!-- Adding JitPack to your repositories -->
    <repository>
    	<id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    
    <!-- Adding the Maven dependency -->
    <dependency>
        <groupId>com.github.ReflxctionDev</groupId>
        <artifactId>SimpleJSON</artifactId>
        <version>1.1</version>
    </dependency>
</dependencies>
```

## Gradle

Add this to your **build.gradle**:

```gradle
allprojects {
    repositories {
	    maven { 'url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.ReflxctionDev:SimpleJSON:1.1'
}
```

# Download
If you don't use Maven or Gradle, then you should consider using [the JAR file](https://github.com/ReflxctionDev/SimpleJSON/releases/tag/1.0-SNAPSHOT) instead.

# Examples
For examples, take a look at the [examples](https://github.com/ReflxctionDev/SimpleJSON/tree/master/src/main/java/examples) package.

# Planned features
- [ ] Add ability to use encryption and decryption when writing