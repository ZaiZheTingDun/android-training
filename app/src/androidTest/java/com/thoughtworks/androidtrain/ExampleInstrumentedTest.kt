package com.thoughtworks.androidtrain

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun should_click_remember_me_selector() {
        onView(withId(R.id.button2)).perform(click())
        val rememberCheckBox = onView(withId(R.id.remember_check_box))

        rememberCheckBox.check(matches(isChecked()))

        rememberCheckBox.perform(click())

        rememberCheckBox.check(matches(isNotChecked()))
    }
}