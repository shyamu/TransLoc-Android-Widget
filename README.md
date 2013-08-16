# TransLoc Android Widget 

Standalone Android widget application for TransLoc bus tracking.
Uses [TransLoc Public API](http://api.transloc.com)

[![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.shyamu.translocwidget)


I decided to make it open-source because the Android documentation and online tutorials are very lacking in terms of developing Android widgets.
The source may be useful as reference for the following..
* making a widget with a widget configuration activity
* using an appwidgetmanager
* parsing JSON using Jackson
* updating a widget when its tapped
* filtering onReceive actions
* PendingIntent, AsyncTask, Spinner, RemoteViews


# Building
Easiest way is to clone and import to Android Studio using 'Import Project', click next with all the way through import dialogs.

If you're using Eclipse with the Android SDK, import and include .jar files from TransLocWidget/libs.

# Acknowledgements
All application bus data is from [TransLoc](http://api.transloc.com) and using their data is subject to their [Terms of Service](http://api.transloc.com/doc/tos/)

It also uses other libraries and APIs such as:

* [JodaTime](http://joda-time.sourceforge.net/)
* [Jackson JSON processor](http://jackson.codehaus.org/)

Some TransLoc JSON parsing courtesy of [Michael Marley](https://github.com/mamarley)
