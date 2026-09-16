📱 Daily Habit Tracker

A modern Android application for building better daily habits, maintaining streaks, setting custom reminders, tracking exercise and nutrition, and viewing personal progress.

🎯 Project Overview

Daily Habit Tracker brings daily routine management into one simple Android application. Users can create habits, complete them every day, track streaks and missed days, set custom alarms, manage exercise and nutrition, and view statistics.

The project focuses on practical functionality, a clean user interface, and easy daily use.

✨ Features

📝 Habit Management

Add new habits

View all habits

Complete habits daily

Automatic streak counting

Missed-day tracking

Streak reset after extended inactivity

Edit habits

Delete habits

Habit-specific icons

🔥 Streak Tracking

First completion starts a streak at 1

Consecutive daily completion increases the streak

Completing the same habit twice on the same day does not increase the count

Missed-day handling and automatic reset

Highest/current streak display

⏰ Custom Alarms & Reminders

Create multiple daily alarms

Select custom alarm times

Add custom reminder messages

Turn alarms ON/OFF

Edit alarm time and message

Delete individual alarms

Exact alarm scheduling

Full-screen alarm display

Custom alarm ringtone

Snooze and stop controls

Alarm restoration after device restart

🏃 Exercise Tracking

Add exercises

Mark exercises as completed

Track exercise completion percentage

Exercise reminder support

Exercise calorie tracking

Exercise calories connected with nutrition goals

🍎 Nutrition Tracking

Add food items

Track calories and nutrition

Enter food quantity

Nutrition preview

English, Hindi and Nepali food aliases

Daily calorie goal support

📊 Statistics

Total habits

Completed habits

Highest streak

Daily progress

Progress visualization

👤 Profile

Personal profile information

Edit and save profile details

Simple profile management

🎨 UI/UX

The application uses a consistent modern design with:

Purple and lavender visual theme

Rounded Material cards

Clear typography

Simple navigation

Card-based dashboard

Habit icons and status indicators

Modern ON/OFF controls

Empty states

Consistent spacing

Full-screen alarm interface

🚀 Onboarding / Splash Screen

The app starts with a three-screen onboarding experience:

Daily Habit Tracker

Build Better Habits

Stay on Track

Navigation:

Get Started → Next → Let's Go → Main Dashboard

The splash screen supports swipe navigation and uses a fullscreen presentation.

🛠️ Technologies Used

Kotlin

Android Studio

XML

Material Components

RecyclerView

ViewPager2

Room Database

Kotlin Coroutines

AlarmManager

Android Notifications

Java Time / LocalDate APIs

📂 Project Structure

DailyHabitTracker/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/dailyhabittracker/
│           │   ├── MainActivity.kt
│           │   ├── SplashActivity.kt
│           │   ├── AddHabitActivity.kt
│           │   ├── AllHabitsActivity.kt
│           │   ├── StatisticsActivity.kt
│           │   ├── ProfileActivity.kt
│           │   ├── NutritionActivity.kt
│           │   ├── AddFoodActivity.kt
│           │   ├── ExerciseActivity.kt
│           │   ├── AddExerciseActivity.kt
│           │   ├── ReminderActivity.kt
│           │   ├── AlarmActivity.kt
│           │   ├── Habit.kt
│           │   ├── HabitDao.kt
│           │   ├── HabitDatabase.kt
│           │   ├── Reminder.kt
│           │   ├── ReminderDao.kt
│           │   ├── ReminderScheduler.kt
│           │   ├── CustomReminderReceiver.kt
│           │   └── BootReceiver.kt
│           └── res/
└── README.md

▶️ How to Run

Open the project in Android Studio.

Allow Gradle sync to complete.

Connect an Android device or start an emulator.

Grant notification/alarm permissions when requested.

Click Run ▶.

Complete the onboarding screens to reach the dashboard.

🔔 Required Permissions

Depending on the Android version and device settings, the app may use:

Notification permission

Exact alarm permission

Full-screen intent permission

Wake lock

Vibration

Boot completed receiver

These are used for reliable reminders and alarm behavior.

🧪 Testing

The main functionality tested includes:

Habit creation

Habit completion

Consecutive streak counting

Missed-day handling

Habit editing

Habit deletion

Multiple alarm creation

Alarm ON/OFF

Alarm editing

Alarm deletion

Full-screen alarm

Exercise tracking

Nutrition tracking

Profile management

Statistics

Splash/onboarding navigation

📈 Future Enhancements

Cloud backup and synchronization

Firebase/Google authentication

Weekly and monthly habit reports

Advanced charts and analytics

Achievement badges

More customization options

Cloud notification synchronization

Improved theme customization

👨‍💻 Developer

Name: Dipendra Pandit
Enrollment No.: 24012011206

Project: Daily Habit Tracker
Platform: Android
Language: Kotlin

📌 Conclusion

Daily Habit Tracker is a practical Android productivity application that combines habit tracking, streak management, custom alarms, exercise, nutrition and statistics in one place.

The project emphasizes functionality, UI/UX, originality, regular development, documentation and presentation readiness.
