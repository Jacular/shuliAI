package com.shuli.cc.app

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.shuli.cc.app.data.remote.KeyType
import com.shuli.cc.app.domain.model.ModelProvider

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
     companion object {
        private lateinit var context: Context

        fun init(androidContext: Context) {
            context = androidContext
        }

        private val masterKey by lazy {
            MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
        }

        private val encryptedPrefs by lazy {
            EncryptedSharedPreferences.create(
                context,
                "secure_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

         fun getApiKey(provider: ModelProvider, type: KeyType): String {
            val key = when(type) {
                KeyType.API_KEY -> "${provider.name}_API_KEY"
                KeyType.CLIENT_ID -> "${provider.name}_CLIENT_ID"
                KeyType.SECRET -> "${provider.name}_SECRET"
            }
            return encryptedPrefs.getString(key, "") ?: ""
        }

         fun setApiKey(provider: ModelProvider, type: KeyType, value: String) {
            val key = when(type) {
                KeyType.API_KEY -> "${provider.name}_API_KEY"
                KeyType.CLIENT_ID -> "${provider.name}_CLIENT_ID"
                KeyType.SECRET -> "${provider.name}_SECRET"
            }
            encryptedPrefs.edit().putString(key, value).apply()
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()

