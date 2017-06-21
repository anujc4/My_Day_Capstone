My Day - Android Application Readme
----------------------------------

1. Introduction

My Day is an Digital Diary Application that is built on Android Framework. It helps users to 

-Make daily entries and delete any past entry

-Update a past Entry

-Adding rich media content like Images and Videos

-Support for adding metadata like GeoTag and Weather Information

-Search for any Entry by date

-Set Favourites Entry

-Adding Screen Widget

-Enable Google Drive AutoBackup for Database and Preferences (requires Sign In using Google+ Account)

Developer Features
-Firebase Authentication

-Crash Reporting

-Test Labs

-Analytics

-Push Notifications


2. OS Requirements

My Day for Android is supported on all devices running Android Marshmallow (API Level 23) and above.
It can be installed, but is untested on Android Lollipop and Kitkat.
All Android Versions below Kitkat are unsupported.


3. Installation

My Day has two build types: Deug and Release.
For testing and demonstration uses, please use the Debug Verison which is currently on build version 1. Beta Testing
For production runs, please use the Release Version which is currently on build version 1. on Stable Channels

	3.1 To install Debug version, follow the given steps
		a. Sign up on openWeatherMap.org and obtain an API Key using the website the website https://home.openweathermap.org/users/sign_up
		b. Goto and install Android Studio along with SDK tools for API Level 25. At the time of this writing, Android Studio is Version 2.3.3
			Use the following link to download Android Studio	https://developer.android.com/studio/index.html
		c. In Android Studio, goto File >New >Import from Version Control >Github and checkout the following repository
			https://github.com/anujc4/My_Day_Capstone.git
		d. Download the project and complete the Gradle Synchronization.
		e. Change the View from Android to Project from the left side Pane.
		f. In the Project View, navigate to app/src/main/java/com/simplicity/anuj/myday/Utility class and put the API Key in the variable API_KEY.
		g. Enable Debugging options in your Mobile from Developer Tools in Settings. If you choose to run the project on an emulator, skip this step.
		h. Run the project by using Command Shift+F10 or the Play button in top bar.
		i. After the Gradle Build completes, the App will start running on your device.

	3.2 To install the Release verison, simply goto the given URL from your Android Device in which you want to install My Day
			https://goo.gl/w6zSXp


4. Advanced Configuration

MyDay has advanced cloud integration with features for Crash Reporting, Analytics, Cloud Authentication and Test Labs. These features are intended solely for the Developer and are not available for public use.
