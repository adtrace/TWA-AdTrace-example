# TWA sample applications
---

### **Important**: This repository contains updated branches:
#### [Custom Launcher - more flexible method](https://github.com/adtrace/TWA-AdTrace-example/tree/twa-custom-launcher)
#### [more examples from official GoogleChrome Github](https://github.com/GoogleChrome/android-browser-helper/tree/main/demos)

**NOTE**: this is the recommended method for implementing you TWA application and the main purpose is to somehow attach Adtrace's life cycle into the app's life cycle.

ground rolls:
- add `permissions` and `proguard` rolls and `dependencies`! [more info](https://github.com/adtrace/adtrace_sdk_android#qs-add-sdk)
- create an `Application` class, create an `AdTraceConfig` object (obviously with your own customizations) and feed it to `AdTrace.onCreate(config)`. [more info](https://github.com/adtrace/adtrace_sdk_android#qs-basic-setup)
- have `AdTrace.onResume()` and `AdTrace.onPause()` attached to `Application`. make sure `onPuase` only triggered when app is off not sooner! [more info](https://github.com/adtrace/adtrace_sdk_android#qs-session-tracking)


