# Rembertime location

## Status
[![Codecov](https://codecov.io/gh/rembertime/location-android/branch/develop/graph/badge.svg?token=7KHDY9ATMG)](https://codecov.io/gh/rembertime/location-android) ![Build status](https://github.com/rembertime/location-android/workflows/Build%20status/badge.svg) [![API](https://img.shields.io/badge/API-%2B16-brightgreen)](https://android-arsenal.com/api?level=16#l16) [![](https://jitpack.io/v/rembertime/location-android.svg)](https://jitpack.io/#rembertime/location-android)
  
## Description
A simple library based on Google Api that through coroutines wraps google services to obtain the user's location by just calling the suspend operator of a use case.

## JitPack
1. Add the JitPack repository to your build file
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
2. Add the dependency
```
dependencies {
    implementation 'com.github.rembertime:location-android:0.1.0'
}
```

## Usage
The only thing you have to do to create a GetLocationUseCase is the following
```
LocationUseCaseProvider.Builder(aplicationContext).build()
```

You can customize your GetLocationUseCase by setting the following properties
```
LocationUseCaseProvider.Builder(aplicationContext)
            .withTimeOutPerAttemptInMillis(200)
            .withRetryDelayInMillis(150)
            .withAttempts(5)
            .withRetryStrategy(EXPONENTIAL_BACK_OFF)
            .withNumberOfLocationUpdates(1)
            .withFastestIntervalReceivingInMillis(5000)
            .withIntervalReceivingInMillis(10000)
            .withRequestPriority(PRIORITY_BALANCED_POWER_ACCURACY)
            .build()
```

## Contribute
New features, bug fixes and improvements in the translation are welcome! For questions and suggestions use the [issues](https://github.com/JuanchiFraga/rembertime-location-android/issues).

Before submit your PR, run the gradle checks.
```
./gradlew check
```

## Licence
```
MIT License

Copyright (c) 2021 Juanchi Fraga

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```


