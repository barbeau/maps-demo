# maps-demo
Demos a bug with Android Maps API v2

* StackOverflow post - http://stackoverflow.com/questions/32276570/jumping-markers-on-android-maps-api-v2
* gmaps-api-issues - https://code.google.com/p/gmaps-api-issues/issues/detail?id=8455

Originally seen in the [OneBusAway Android project](https://github.com/OneBusAway/onebusaway-android/tree/develop) - this project aims to isolate the code to just the parts necessary to add markers to the map, and reproduce the problem.

### Summary

I'm seeing markers jump around on the map on Android Maps API v2 even when nothing is happening in the app.  

Here's a video of the behavior:

https://youtu.be/cOUGD0T5Ojs

*What I expect* 

Markers should remain stationary at their originally added lat/long.

*What steps will reproduce the problem?* 

* 1a. Download the APK from:
https://dl.dropboxusercontent.com/u/46443835/OneBusAway/UI_redesign/onebusaway-android-oba-google-debug.apk

or

* 1b. Build, install, and run the develop branch of OneBusAway:
 * a. `git clone https://github.com/OneBusAway/onebusaway-android.git`
 * b. `git checkout develop`
 * c. `gradlew installObaGoogleDebug`
 * d. `adb shell am start -n com.joulespersecond.seattlebusbot/org.onebusaway.android.ui.HomeActivity`

* 2. Browse to any supported city (e.g., Seattle or Tampa), and watch the green bus stop markers jump around on the map

I should add that I can't *always* reproduce this.  It seems like everything works fine for a time, but then when the markers start jumping around they don't stop.

*Marker Implementation Details*

The code that loads the icons used for the 9 marker types (8 directions + no direction) is here:
https://github.com/OneBusAway/onebusaway-android/blob/develop/onebusaway-android/src/google/java/org/onebusaway/android/map/googlemapsv2/StopOverlay.java#L169

I'm using this drawable:
https://github.com/OneBusAway/onebusaway-android/blob/develop/onebusaway-android/src/main/res/drawable/map_stop_icon.xml

...which is a number of shapes - this creates the main green circle with the white outline and the drop shadoes.  Then, I'm drawing the direction arrow on top of this drawable for each of the 8 directions - code for drawing directions is here:

https://github.com/OneBusAway/onebusaway-android/blob/develop/onebusaway-android/src/google/java/org/onebusaway/android/map/googlemapsv2/StopOverlay.java#L202

In the code to load the icons, I'm caching the `BitmapDescriptor` returned from `BitmapDescriptorFactory.fromBitmap()` for each of the 9 icon types on first load, so this isn't done each time a marker is put on the map.

I also saw the app crash to "Unfortunately, OneBusAway has stopped." and saw this exception in Logcat after letting the app sit on the map screen for a few minutes:

    08-10 16:40:02.422  15843-15929/com.joulespersecond.seattlebusbot E/AndroidRuntime﹕ FATAL EXCEPTION: GLThread 8614
        Process: com.joulespersecond.seattlebusbot, PID: 15843
        java.lang.IllegalArgumentException: Comparison method violates its general contract!
                at java.util.ComparableTimSort.mergeHi(ComparableTimSort.java:831)
                at java.util.ComparableTimSort.mergeAt(ComparableTimSort.java:449)
                at java.util.ComparableTimSort.mergeCollapse(ComparableTimSort.java:372)
                at java.util.ComparableTimSort.sort(ComparableTimSort.java:178)
                at java.util.ComparableTimSort.sort(ComparableTimSort.java:142)
                at java.util.Arrays.sort(Arrays.java:1957)
                at java.util.Collections.sort(Collections.java:1864)
                at com.google.maps.api.android.lib6.gmm6.n.bl.a(Unknown Source)
                at com.google.maps.api.android.lib6.gmm6.n.l.a(Unknown Source)
                at com.google.maps.api.android.lib6.gmm6.n.l.b(Unknown Source)
                at com.google.maps.api.android.lib6.gmm6.n.cv.f(Unknown Source)
                at com.google.maps.api.android.lib6.gmm6.n.cv.run(Unknown Source)

I've seen this on an LG G4 and Nexus 6.  More details on LG device is below.

* LG G4 LS991 with Android 5.1 (LS991ZV4)

* Google Play Services client library version = `compile 'com.google.android.gms:play-services-maps:7.5.0'` and `compile 'com.google.android.gms:play-services-maps:7.8.0'`

* Google Play Services version on the device - Google Play Services 7.8.99 (2134222-440)

* Android SDK Version: `compileSdkVersion 21 buildToolsVersion "21.1.2"`

This issue hasn't always existed, which makes me believe it was introduced during an update to Android Google Play Services/Maps at some point.

I've opened an issue for this on gmaps-api-issues as well, but no response as of this post:

https://code.google.com/p/gmaps-api-issues/issues/detail?id=8455
