import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object DatastoreRepository {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val loginTokenKey = stringPreferencesKey("loginToken")
    private val SMSCode = intPreferencesKey("SMSCode")
    private val logged = booleanPreferencesKey("logged")
    private val phone = stringPreferencesKey("phone")

    suspend fun getLoginToken(context: Context): String {
        return context.dataStore.data
            .map { preferences -> preferences[loginTokenKey] }
            .first()?:"-"
    }

    suspend fun saveLoginToken(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[loginTokenKey] = token
        }
    }

    suspend fun getSMSCode(context: Context): Int {
        return context.dataStore.data
            .map { preferences -> preferences[SMSCode] }
            .first()?:0
    }

    suspend fun saveSMSCode(context: Context, token: Int) {
        context.dataStore.edit { preferences ->
            preferences[SMSCode] = token
        }
    }

    suspend fun saveLoggedState(context: Context, token: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[logged] = token
        }
    }

    suspend fun getLoggedState(context: Context): Boolean {
        return context.dataStore.data
            .map { preferences -> preferences[logged] }
            .first()?:false
    }

    suspend fun savePhone(context: Context, token: String) {
        context.dataStore.edit { preferences ->
            preferences[phone] = token
        }
    }

    suspend fun getPhone(context: Context): String {
        return context.dataStore.data
            .map { preferences -> preferences[phone] }
            .first()?:""
    }
}
