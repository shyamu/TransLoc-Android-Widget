# TransLoc Android Widget 

Standalone Android widget application for TransLoc bus tracking.
Uses [TransLoc Public API](http://api.transloc.com)

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.shyamu.translocwidget)


I decided to make it open-source because the Android documentation and online tutorials are very lacking in terms of developing Android widgets.
The source may be useful as reference for the following..
* Making a widget with a widget configuration activity
* Using an appwidgetmanager
* Parsing JSON using Jackson
* Updating a widget when its tapped
* Filtering onReceive actions
* PendingIntent, AsyncTask, Spinner, RemoteViews, SharedPreferences
* Using Android Holo ColorPicker
* Making responsive layouts for widgets


# Building
1) Clone project

2) Get an API key from [Mashape](https://www.mashape.com) for the TransLoc API

3) Go to root of repo and add your API key to line 17 of strings.xml

4) Move the strings.xml file from the repo root and put into TransLocWidget/src/main/res/values

5) Import project into Android Studio and build

# Acknowledgements
All application bus data is from [TransLoc](http://api.transloc.com) and using their data is subject to their [Terms of Service](http://api.transloc.com/doc/tos/)

It also uses other libraries and APIs such as:

* [JodaTime](http://joda-time.sourceforge.net/)
* [Jackson JSON processor](http://jackson.codehaus.org/)
* [Holo ColorPicker](https://github.com/LarsWerkman/HoloColorPicker) 

Some TransLoc JSON parsing courtesy of [Michael Marley](https://github.com/mamarley)
