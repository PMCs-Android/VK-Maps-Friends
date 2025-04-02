package com.example.mapsfriends

<<<<<<< HEAD
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

=======
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
<<<<<<< HEAD
        assertEquals("com.example.mapsfriends", appContext.packageName)
    }
}
=======
        assertEquals("com.example.vkid", appContext.packageName)
    }
}
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
