xcodebuild \
    -scheme kotlinIOS \
    -workspace "$(pwd)/${0%/*}/../sample/sample-native/kotlinIOS.xcworkspace" \
    -sdk iphonesimulator \
    -configuration Debug \
    build \
    CONFIGURATION_BUILD_DIR="$(pwd)/${0%/*}/../sample/sample-native/build" && \
xcrun simctl install booted "$(pwd)/${0%/*}/../sample/sample-native/build/kotlinIOS.app" && \
xcrun simctl launch booted "kim.jeonghyeon.kotlinIOS" && \
open /Applications/Xcode.app/Contents/Developer/Applications/Simulator.app