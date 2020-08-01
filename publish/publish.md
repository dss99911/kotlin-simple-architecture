### Publish Step
1. increase version

2. execute command below for local

```
./gradlew -PincludingModuleName=kotlin-simple-architecture-annotation :kotlin-simple-architecture-annotation:publishToMavenLocal && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared:publishToMavenLocal && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api:publishToMavenLocal && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native:publishToMavenLocal && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin :gradle-plugin:kotlin-simple-architecture-gradle-plugin:publishToMavenLocal && \
./gradlew -PincludingModuleName=kotlin-simple-architecture :kotlin-simple-architecture:publishToMavenLocal

```

 3. execute command below for remote

```
./gradlew -PincludingModuleName=kotlin-simple-architecture-annotation :kotlin-simple-architecture-annotation:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin :gradle-plugin:kotlin-simple-architecture-gradle-plugin:publishPlugins && \
./gradlew -PincludingModuleName=kotlin-simple-architecture :kotlin-simple-architecture:publish


```