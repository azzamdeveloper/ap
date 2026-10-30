package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.adblocker.AdBlocker
import com.example.vpn.VpnManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Z Browser", appName)
  }

  @Test
  fun `test adblocker domain interception`() {
    AdBlocker.setEnabled(true)
    assertTrue(AdBlocker.isAd("https://googleads.g.doubleclick.net/pagead/ads?id=123"))
    assertTrue(AdBlocker.isAd("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"))
    assertFalse(AdBlocker.isAd("https://wikipedia.org/wiki/Kotlin"))
    assertFalse(AdBlocker.isAd("https://github.com/torvalds/linux"))
  }

  @Test
  fun `test vpn server list and duration format`() {
    assertTrue(VpnManager.serverList.isNotEmpty())
    assertEquals("01:05", VpnManager.formatDuration(65))
    assertEquals("01:00:00", VpnManager.formatDuration(3600))
  }
}
