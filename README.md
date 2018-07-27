[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
![Release](https://jitpack.io/v/ReflxctionDev/SimpleJSON.svg)
# SimpleJSON
A simple wrapper for [Google GSON's API](https://github.com/google/gson), to simplify writing [JSON](http://json.org/) data and reading it

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

...

<dependencies>
    <!-- Adding the Maven dependency -->
    <dependency>
        <groupId>com.github.ReflxctionDev</groupId>
        <artifactId>SimpleJSON</artifactId>
        <version>1.0-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

## Gradle

Add this to your **build.gradle**:

```groovy
allprojects {
    repositories {
	    maven {
	      url 'https://jitpack.io'
	    }
    }
}

...

dependencies {
    compile 'com.github.ReflxctionDev:SimpleJSON:1.0-SNAPSHOT'
}
```

# Examples
For examples, take a look at the [examples](https://github.com/ReflxctionDev/SimpleJSON/tree/master/src/test/java/examples) package.