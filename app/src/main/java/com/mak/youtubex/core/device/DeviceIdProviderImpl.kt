package com.mak.youtubex.core.device

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import androidx.core.content.edit

class DeviceIdProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DeviceIdProvider {

    private val prefs = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)

    override fun getDeviceId(): String {
        var id = prefs.getString("device_id", null)

        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit { putString("device_id", id) }
        }

        return id
    }
}