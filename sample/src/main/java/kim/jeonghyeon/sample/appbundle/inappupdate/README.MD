Official Document
=============
- https://developer.android.com/guide/app-bundle/in-app-updates

Introduction
=============
- update app inside app

Dependency
=============
```groovy
implementation 'com.google.android.play:core:1.5.0'
```

Limitation
=============
- running Android 5.0 (API level 21) or higher
- working only if user install the app by the user account.(if installed manually, it is not possible to update)
- Google Play Cache is not renewed. then doesn't know latest version.

Mode
=============
- Flexible
- Immediate