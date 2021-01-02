#todo migrate to teamcity after libraries are deployed
source ~/.path_profile

./gradlew -Penvironment=production :sample-backend:build && \
scp sample-backend/build/libs/sample-backend-all.jar hyun-server:~/app/sample-backend/sample-backend-all.jar && \
ssh hyun-server "sudo systemctl restart sample-backend"