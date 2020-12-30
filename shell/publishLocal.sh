./gradlew -PbuildByLibrary=false -PincludingModuleName=api:library:kotlin-simple-api-annotation api:library:kotlin-simple-api-annotation:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service-shared :api:plugin:kotlin-simple-api-gradle-service-shared:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service :api:plugin:kotlin-simple-api-gradle-service:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service-native :api:plugin:kotlin-simple-api-gradle-service-native:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=api:plugin:kotlin-simple-api-gradle :api:plugin:kotlin-simple-api-gradle:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=api:library:kotlin-simple-api-client :api:library:kotlin-simple-api-client:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=api:library:kotlin-simple-api-backend :api:library:kotlin-simple-api-backend:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=framework:kotlin-simple-architecture-gradle :framework:kotlin-simple-architecture-gradle:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=framework:kotlin-simple-architecture-client :framework:kotlin-simple-architecture-client:publishToMavenLocal && \
./gradlew -PbuildByLibrary=false -PincludingModuleName=framework:kotlin-simple-architecture-backend :framework:kotlin-simple-architecture-backend:publishToMavenLocal