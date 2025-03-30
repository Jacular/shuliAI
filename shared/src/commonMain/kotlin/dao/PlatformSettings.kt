package dao

import models.ModelProvider
import utils.KeyType

expect class PlatformSettings {
    fun getApiKey(provider: ModelProvider, type: KeyType): String
    fun setApiKey(provider: ModelProvider, type: KeyType, value: String)
}