# TransLoc Widget 

Simple Android widget application for viewing bus arrival times on Android homescreen. 
Uses [TransLoc Public API](http://api.transloc.com)

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.shyamu.translocwidget)


I decided to make it open-source because the Android documentation and online tutorials are very lacking in terms of developing Android widgets.
The source may be useful as reference for the following...
* Material design with appcompat libraries
* Service calls using RxAndroid
* Making a widget with a widget configuration activity
* Responsive layouts for widgets
* Using an appwidgetmanager
* Updating a widget when its tapped
* Fragment communication
* PendingIntent, RemoteViews
* Usage of the libraies below


# Building
1) Clone project

2) Get an API key from [Mashape](https://www.mashape.com) for the TransLoc API

3) Go to TransLocWidget/gradle.properties_UPDATEME and add API keys

4) Rename gradle.properties_UPDATEME to gradle.properties

5) Import project into Android Studio and build

# Acknowledgements
All application bus data is from [TransLoc](http://api.transloc.com) and using their data is subject to their [Terms of Service](http://api.transloc.com/doc/tos/)

It also uses other libraries and APIs such as:

* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Retrofit](http://square.github.io/retrofit/)
* [OkHttp](http://square.github.io/okhttp/)
* [TourGuide](https://github.com/worker8/TourGuide)
* [Holo ColorPicker](https://github.com/LarsWerkman/HoloColorPicker) 
* [JodaTime](http://joda-time.sourceforge.net/)
