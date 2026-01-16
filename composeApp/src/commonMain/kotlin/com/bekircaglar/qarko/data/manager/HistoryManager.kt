package com.bekircaglar.qarko.data.manager

import androidx.compose.runtime.mutableStateListOf
import com.bekircaglar.qarko.data.model.Tenant
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Son görüntülenen menüleri yöneten manager
 * Verileri kalıcı olarak saklamak için Multiplatform Settings kullanır
 */
object HistoryManager : KoinComponent {
    // Koin üzerinden enjekte edilen platform-specific settings
    private val settings: Settings by inject()
    
    private val json = Json { ignoreUnknownKeys = true }
    private const val HISTORY_KEY = "visited_menus_history"

    private val _history = mutableStateListOf<VisitedMenu>()
    val history: List<VisitedMenu> get() = _history

    init {
        loadHistory()
    }

    @Serializable
    data class VisitedMenu(
        val tenant: Tenant,
        val tableId: String?,
        val visitedAt: Instant
    )

    private fun loadHistory() {
        try {
            val historyJson: String? = settings[HISTORY_KEY]
            if (historyJson != null) {
                val decodedHistory = json.decodeFromString<List<VisitedMenu>>(historyJson)
                _history.clear()
                _history.addAll(decodedHistory)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveHistory() {
        try {
            val historyJson = json.encodeToString(_history.toList())
            settings[HISTORY_KEY] = historyJson
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addVisitedMenu(tenant: Tenant, tableId: String?) {
        val now = Clock.System.now()
        
        // Aynı tenant varsa eskisini kaldır
        _history.removeAll { it.tenant.id == tenant.id }
        
        // Yeni ziyareti en başa ekle
        _history.add(0, VisitedMenu(tenant, tableId, now))
        
        // Sadece son 10 kaydı tut
        if (_history.size > 10) {
            _history.removeRange(10, _history.size)
        }
        
        saveHistory()
    }

    fun getRecentMenus(): List<VisitedMenu> {
        val now = Clock.System.now()
        // Son 15 dakika içindeki menüleri filtrele
        return _history.filter { 
            (now - it.visitedAt).inWholeMinutes <= 15 
        }
    }

    fun clearHistory() {
        _history.clear()
        settings.remove(HISTORY_KEY)
    }
}
