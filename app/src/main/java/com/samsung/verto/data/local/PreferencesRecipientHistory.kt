package com.samsung.verto.data.local
import android.content.Context
import com.samsung.verto.domain.model.User
import org.json.JSONArray
import org.json.JSONObject

class PreferencesRecipientHistory(context: Context) : RecipientHistory {
    private val preferences = context.getSharedPreferences("recipient_history", Context.MODE_PRIVATE)
    override fun load(): List<User> = runCatching {
        val array = JSONArray(preferences.getString("items", "[]"))
        (0 until array.length()).map { i ->
            val item = array.getJSONObject(i)
            User(item.getLong("id"), item.getString("email"), item.optString("name").ifEmpty { null })
        }.take(10)
    }.getOrDefault(emptyList())
    override fun save(users: List<User>) {
        val array = JSONArray()
        users.take(10).forEach { user -> array.put(JSONObject().put("id", user.id).put("email", user.email).put("name", user.displayName ?: "")) }
        preferences.edit().putString("items", array.toString()).apply()
    }
}
