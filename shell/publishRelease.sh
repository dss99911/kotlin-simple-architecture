./gradlew -PincludingModuleName=kotlin-simple-architecture-annotation :kotlin-simple-architecture-annotation:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-shared:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native :gradle-plugin:kotlin-simple-architecture-gradle-plugin-api-native:publish && \
./gradlew -PincludingModuleName=gradle-plugin:kotlin-simple-architecture-gradle-plugin :gradle-plugin:kotlin-simple-architecture-gradle-plugin:publishPlugins && \
./gradlew -PincludingModuleName=kotlin-simple-architecture :kotlin-simple-architecture:publish
#todo :kotlin-simple-architecture:publish is fail at first time, but after try one more time. it's working.