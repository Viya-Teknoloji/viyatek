package com.viyatek.lockscreen

import android.content.Context
import android.util.Log
import java.util.*
import kotlin.math.floor

const val EARLY_TIME_PERCENTAGE = 0.8

class ScreenDisplayCoordinator(val activity: Context) {

    private val MORNING_BOUNDRIES = intArrayOf(7, 11)
    val AFTERNOON_BOUNDRIES = intArrayOf(12, 17)
    val EVENING_BOUNDRIES = intArrayOf(18, 23)
    val NIGHT_BOUNDRIES = intArrayOf(0, 6)

    var theReminderInterval : Int = 10
    val LOG_TAG = "Display Coordinator"
    private val lockScreenSharedPrefsHandler by lazy { LockScreenPreferencesHandler(activity)}

    private val preferredAmount by lazy { lockScreenSharedPrefsHandler.getMustShowFactCount()}
    private val lastDayOpened by lazy { lockScreenSharedPrefsHandler.getLastDayOpened() }
    private val knowledgeSoFar by lazy { lockScreenSharedPrefsHandler.getSeenFactCount()}
    val quoteLeft by lazy {  preferredAmount - knowledgeSoFar }

    val calendar: Calendar by lazy { Calendar.getInstance() }
    private val minuteOfHour by lazy { calendar[Calendar.MINUTE]  }
    private val hourOfDay by lazy {calendar[Calendar.HOUR_OF_DAY]  }
    private val today by lazy { calendar[Calendar.DAY_OF_MONTH]  }
    private val currentTime by lazy { calendar.timeInMillis }

    private val showTime by lazy { lockScreenSharedPrefsHandler.getShowTime()}

    val remainingMiliSeconds by lazy { ((showTime - currentTime) * 0.8).toLong() }
    val remainingSeconds by lazy { (remainingMiliSeconds / 1000 % 60).toInt() }
    val remainingMinutes by lazy { floor((remainingMiliSeconds / 60000).toDouble()).toInt() }

    //Get Period's full time minutes
    val fullTimeNight by lazy { (NIGHT_BOUNDRIES[1] - NIGHT_BOUNDRIES[0] + 1) * 60 }
    private val fullTimeMorning by lazy { (MORNING_BOUNDRIES[1] - MORNING_BOUNDRIES[0] + 1) * 60  }
    private val fullTimeAfternoon by lazy { (AFTERNOON_BOUNDRIES[1] - AFTERNOON_BOUNDRIES[0] + 1) * 60 }
    private val fullTimeEvening by lazy { (EVENING_BOUNDRIES[1] - EVENING_BOUNDRIES[0] + 1) * 60 }

    private val minutesLeft by lazy {  calculateMinutesleft() }
    private val minutesLeftPercentage by lazy { minutesLeft.toDouble() * EARLY_TIME_PERCENTAGE }

    val nextShowInMinutes : Number by lazy {  (minutesLeftPercentage / quoteLeft.toDouble()).let { if(it<10) theReminderInterval else it }}
    val futureShowTime by lazy { currentTime + nextShowInMinutes.toLong() * 60 * 1000 }

    fun checkIfDisplay(): Boolean {
        var eligibleToShow = false
        if (checkUserPref() && checkIfFactAmount() && checkIfTimeHasCome()) { eligibleToShow = true }
        return eligibleToShow
    }

    //Sub Functions
    private fun checkUserPref(): Boolean //it is mandatory to select a time period so it will return true everytime
    {
        Log.d(LOG_TAG, "Looking for User Prefs")

        if (!lockScreenSharedPrefsHandler.isLockScreenOk() && !lockScreenSharedPrefsHandler.isLockScreenNotificationOk())
        {
            Log.d(LOG_TAG, "User don't want to take reminders. So it is not elligible to show")
            return false
        }

        when {
            hourOfDay >= NIGHT_BOUNDRIES[0] && hourOfDay <= NIGHT_BOUNDRIES[1] -> {
                when (lockScreenSharedPrefsHandler.isNightOk()) {
                    false -> {
                        Log.d(LOG_TAG, "We are in night period night is not preferred returning false")
                        return false
                    }
                    else -> {
                        Log.d(LOG_TAG, "We are in night period night is preferred, period OK")
                    }
                }
            }
            hourOfDay >= MORNING_BOUNDRIES[0] && hourOfDay <= MORNING_BOUNDRIES[1] -> {
                if (!lockScreenSharedPrefsHandler.isMorningOk()) {
                    Log.d(LOG_TAG, "We are in morning period morning is not preferred returning false")
                    return false
                }
                else { Log.d(LOG_TAG, "We are in morning period morning is preferred returning true") }
            }
            hourOfDay >= AFTERNOON_BOUNDRIES[0] && hourOfDay <= AFTERNOON_BOUNDRIES[1] -> {
                if (!lockScreenSharedPrefsHandler.isAfternoonOk()) {
                    Log.d(LOG_TAG, "We are in afternoon period afternoon is not preferred returning false")
                    return false
                } else {
                    Log.d(LOG_TAG, "We are in afternoon period afternoon is preferred, period OK")
                }
            }
            hourOfDay >= EVENING_BOUNDRIES[0] && hourOfDay <= EVENING_BOUNDRIES[1] -> {
                if (!lockScreenSharedPrefsHandler.isEveningOk()) {
                    Log.d(LOG_TAG, "We are in evening period evening is not preferred returning false")
                    return false
                } else {
                    Log.d(LOG_TAG, "We are in evening period, evening is preferred, period OK")
                }
            }
        }
        Log.d(LOG_TAG, "Prefs OK. Returning for looking other factors")
        return true
    }

    private fun checkIfFactAmount(): Boolean {
        Log.d(LOG_TAG, "Looking for Fact Amounts")

        return if (knowledgeSoFar < preferredAmount) {
            Log.d(LOG_TAG, "Current seen amount is less than preferred amount")
            true
        } else {
            Log.d(LOG_TAG, "current amount is more than preferred not eligible")
            false
        }
    }

    private fun checkIfTimeHasCome(): Boolean {
        Log.d(LOG_TAG, "Looking for Time")

        //Decide
        return if (currentTime >= showTime) {
            Log.d(LOG_TAG, "Time has come. Elligible to show")
            true
        } else {
            Log.d(LOG_TAG, "Time has not come yet. Not eligible : $remainingMinutes minutes $remainingSeconds seconds")
            false
        }
    }

    fun updateFutureTime() {
        Log.d(LOG_TAG, "Checking day, Last Day Opened: $lastDayOpened, Today: $today")

        //Check if new day
        if (today != lastDayOpened) {
            Log.d(LOG_TAG, "Making knowledge_education amount zero because it is different day")
           lockScreenSharedPrefsHandler.setSeenFactCount(0)
        }

        lockScreenSharedPrefsHandler.setLastDayOpened(today)
        Log.d(LOG_TAG, "Last day 's today now")

        /*
        1. Check last day opened
        2: How many knowledges left?
        3: How much time left?
        */

        //Get How Many Knowledges Left

        Log.d(LOG_TAG, "$knowledgeSoFar facts learned, $quoteLeft facts left  today ${if(quoteLeft<1){"Not calculating future time"}else{""}}")

        if (quoteLeft < 1) {return}

        Log.d(LOG_TAG, "Knowledge Left: $quoteLeft Minutes Left: $minutesLeft Minutes Left Percentage: " +
                "$minutesLeftPercentage NextShowInMinutes: $nextShowInMinutes Time Now: $currentTime Future Show Time: $futureShowTime")

        //Apply Time Future
        lockScreenSharedPrefsHandler.setShowTime(futureShowTime)

        Log.d(LOG_TAG, "Future Show Time applied to prefs")
    }

    private fun calculateMinutesleft(): Int {
        var minutesLeft = 0

        when {
            hourOfDay >= NIGHT_BOUNDRIES[0] && hourOfDay <= NIGHT_BOUNDRIES[1] -> {
                Log.d(LOG_TAG, "Gece Saatindeyiz")
                //Ad Full Times (If preferred)
                if (lockScreenSharedPrefsHandler.isMorningOk()) {
                    minutesLeft += fullTimeMorning
                    Log.d(LOG_TAG, "Sabah periyodu seçili eklendi")
                }
                if (lockScreenSharedPrefsHandler.isAfternoonOk()) {
                    minutesLeft += fullTimeAfternoon
                    Log.d(LOG_TAG, "Öğle periyodu seçili eklendi")
                }
                if (lockScreenSharedPrefsHandler.isEveningOk()) {
                    minutesLeft += fullTimeEvening
                    Log.d(LOG_TAG, "Akşam periyodu eklendi")
                }

                //Determine minutes left at this period
                if (lockScreenSharedPrefsHandler.isNightOk()) {
                    val minutesLeftAtThisPeriod = (NIGHT_BOUNDRIES[1] - hourOfDay) * 60 + (60 - minuteOfHour)
                    minutesLeft += minutesLeftAtThisPeriod
                    Log.d(LOG_TAG, "Gece periyodu seçili, periyottan kalan dakikalar eklendi")
                }
            }
            hourOfDay >= MORNING_BOUNDRIES[0] && hourOfDay <= MORNING_BOUNDRIES[1] -> {
                Log.d(LOG_TAG, "We are in Morning Time ")
                //Ad Full Times (If preferred)
                if (lockScreenSharedPrefsHandler.isAfternoonOk()) {
                    minutesLeft += fullTimeAfternoon
                    Log.d(LOG_TAG, "Afternoon is selected period added")
                }
                if (lockScreenSharedPrefsHandler.isEveningOk()) {
                    minutesLeft += fullTimeEvening
                    Log.d(LOG_TAG, "Evening is selected period added")
                }

                //Determine minutes left at this period
                if (lockScreenSharedPrefsHandler.isMorningOk()) {
                    val minutesLeftAtThisPeriod =
                        (MORNING_BOUNDRIES[1] - hourOfDay) * 60 + (60 - minuteOfHour)
                    minutesLeft += minutesLeftAtThisPeriod
                    Log.d(LOG_TAG, "Morning is selected remaining minutes added")
                }
            }
            hourOfDay >= AFTERNOON_BOUNDRIES[0] && hourOfDay <= AFTERNOON_BOUNDRIES[1] -> {
                Log.d(LOG_TAG, "We are in afternoon Time ")
                //Ad Full Times (If preferred)
                if (lockScreenSharedPrefsHandler.isEveningOk()) {
                    minutesLeft += fullTimeEvening
                    Log.d(LOG_TAG, "Evening is selected period added")
                }

                //Determine minutes left at this period
                if (lockScreenSharedPrefsHandler.isAfternoonOk()) {
                    val minutesLeftAtThisPeriod =
                        (AFTERNOON_BOUNDRIES[1] - hourOfDay) * 60 + (60 - minuteOfHour)
                    minutesLeft += minutesLeftAtThisPeriod
                    Log.d(LOG_TAG, "Afternoon is selected remaining minutes added")
                }
            }
            hourOfDay >= EVENING_BOUNDRIES[0] && hourOfDay <= EVENING_BOUNDRIES[1] -> {
                Log.d(LOG_TAG, "We are in eveninng Time ")
                //Determine minutes left at this period
                if (lockScreenSharedPrefsHandler.isEveningOk()) {
                    val minutesLeftAtThisPeriod =
                        (EVENING_BOUNDRIES[1] - hourOfDay) * 60 + (60 - minuteOfHour)
                    minutesLeft += minutesLeftAtThisPeriod
                    Log.d(LOG_TAG, "Evening is selected remaining minutes added")
                }
            }
        }

        return minutesLeft
    }
}