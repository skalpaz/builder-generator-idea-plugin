Builder Generator Idea plugin ![Java CI with Gradle](https://github.com/mjedynak/builder-generator-idea-plugin/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)
===============
Plugin for IntelliJ IDEA that adds ability to generate builder for a class and switch between them.
Switching between builder and source class is similar to 'Go To Test' action.
Generated builder class does not use reflection, only setter methods or constructor.


Customization
===============
- Build project:
  - In project's root folder, run: ```gradle jar```
  - Find jar generated file at location: ```/build/libs/```


- Install plugin in IntelliJ:
  - Go to Settings > Plugins > "gearwheel" > Install plugin from Disk
  - Select jar file


- Add custom logs:
  ```Java
    CustomLogger.displayStickyNotification(
                this.getClass().getSimpleName(),
                new Object() {}.getClass().getEnclosingMethod().getName(),
                "TextToDisplay");
  ``` 