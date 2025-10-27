package com.bitchat.android

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.bitchat.android.nostr.RelayDirectory
import com.bitchat.android.ui.theme.ThemePreferenceManager
import com.bitchat.android.net.TorManager
import java.util.Locale

/**
 * Main application class for bitchat Android
 */
class BitchatApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Set default locale to Hebrew
        setDefaultLocale(Locale("he"))
        
        // Initialize Tor first so any early network goes over Tor
        try { TorManager.init(this) } catch (_: Exception) { }

        // Initialize relay directory (loads assets/nostr_relays.csv)
        RelayDirectory.initialize(this)

        // Initialize LocationNotesManager dependencies early so sheet subscriptions can start immediately
        try { com.bitchat.android.nostr.LocationNotesInitializer.initialize(this) } catch (_: Exception) { }

        // Initialize favorites persistence early so MessageRouter/NostrTransport can use it on startup
        try {
            com.bitchat.android.favorites.FavoritesPersistenceService.initialize(this)
        } catch (_: Exception) { }

        // Warm up Nostr identity to ensure npub is available for favorite notifications
        try {
            com.bitchat.android.nostr.NostrIdentityBridge.getCurrentNostrIdentity(this)
        } catch (_: Exception) { }

        // Initialize theme preference
        ThemePreferenceManager.init(this)

        // Initialize debug preference manager (persists debug toggles)
        try { com.bitchat.android.ui.debug.DebugPreferenceManager.init(this) } catch (_: Exception) { }

        // TorManager already initialized above
    }
    
    /**
     * Set the default locale for the application
     */
    private fun setDefaultLocale(locale: Locale) {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        
        // Update resources with the locale configuration
        val resources = applicationContext.resources
        val metrics = resources.displayMetrics
        resources.updateConfiguration(config, metrics)
    }
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }
    
    private fun updateBaseContextLocale(context: Context): Context {
        val locale = Locale("he")
        Locale.setDefault(locale)
        
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        // Don't set layout direction to keep LTR layout
        // configuration.setLayoutDirection(locale)
        
        return context.createConfigurationContext(configuration)
    }
}
