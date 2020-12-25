./gradlew -PincludingModuleName=kotlin-simple-architecture-annotation :kotlin-simple-architecture-annotation:publishToMavenLocal && \
./gradlew -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service-shared :api:plugin:kotlin-simple-api-gradle-service-shared:publishToMavenLocal && \
./gradlew -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service :api:plugin:kotlin-simple-api-gradle-service:publishToMavenLocal && \
./gradlew -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service-native :api:plugin:kotlin-simple-api-gradle-service-native:publishToMavenLocal && \
./gradlew -PincludingModuleName=api:plugin:kotlin-simple-api-gradle :api:plugin:kotlin-simple-api-gradle:publishToMavenLocal && \
./gradlew -PincludingModuleName=api:library:kotlin-simple-api-client :api:library:kotlin-simple-api:publishToMavenLocal
./gradlew -PincludingModuleName=api:library:kotlin-simple-api-backend :api:library:kotlin-simple-api:publishToMavenLocal