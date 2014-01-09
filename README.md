QT4andAndroid
=============

An Android application that sends orientation data to a QT4 program over USB.  

The Android app uses the Android Debug Bridge(ADB) to communicate with the computer over USB.
The Android app starts a TCP server and anttemps to open a socket with the QT4 program.
After the connection is established, the app will send orientation data over the socket every 50ms.
The orientation data is sent as a string in the form:x y z.
Whenever the QT4 prrogram notices there is valid data in the socket then it places a vector and sends a signal to reciever object in another thread to handle the data.

To setup the ADB, you need to run adb from android-sdk-linux/platform-tools as follows:
	adb forward tcp:38300 tcp:38301

The following lines need to be added to your androidmanifest.xml file:
	<uses-permission android:name="android.permission.GET_TASKS" ></uses-permission>
	<uses-permission android:name="android.permission.INTERNET"></uses-permission>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
	<uses-permission android:name="android.permission.WAKE_LOCK" /></uses-permission>

The android application probably should be using UDP instead of TCP, but I wanted to make sure that packets were in order and could be reliably recieved/ transmitted. Since sockets are being used, this could be modified to transmit data over a wifi/ internet connection, if IP's can be determined/set/known at run time. This way the ADB shouldn't have to be used to forward ports.
