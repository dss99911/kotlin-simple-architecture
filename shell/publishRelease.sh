./gradlew -PbuildByLibrary=true -PincludingModuleName=api:library:kotlin-simple-api-annotation api:library:kotlin-simple-api-annotation:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service-shared :api:plugin:kotlin-simple-api-gradle-service-shared:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service :api:plugin:kotlin-simple-api-gradle-service:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=api:plugin:kotlin-simple-api-gradle-service-native :api:plugin:kotlin-simple-api-gradle-service-native:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=api:plugin:kotlin-simple-api-gradle :api:plugin:kotlin-simple-api-gradle:publishPlugins && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=api:library:kotlin-simple-api-client :api:library:kotlin-simple-api-client:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=api:library:kotlin-simple-api-backend :api:library:kotlin-simple-api-backend:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=framework:kotlin-simple-architecture-gradle :framework:kotlin-simple-architecture-gradle:publishPlugins && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=framework:kotlin-simple-architecture-client :framework:kotlin-simple-architecture-client:publish && \
./gradlew -PbuildByLibrary=true -PincludingModuleName=framework:kotlin-simple-architecture-backend :framework:kotlin-simple-architecture-backend:publish