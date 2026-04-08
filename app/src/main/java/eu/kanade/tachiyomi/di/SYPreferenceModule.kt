package eu.kanade.tachiyomi.di

import android.app.Application
import org.koin.dsl.module
import exh.pref.DelegateSourcePreferences
import exh.source.ExhPreferences
import uy.kohesive.injekt.api.InjektRegistrar

val syPreferenceModule = module {
    single {
        DelegateSourcePreferences(
            preferenceStore = get(),
        )
    }

    single {
        ExhPreferences(get())
    }
}

class SYPreferenceModule(val application: Application) : InjektModule {

    override fun uy.kohesive.injekt.api.InjektRegistrar.registerInjectables() {
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<DelegateSourcePreferences>(DelegateSourcePreferences::class.java) }
        addSingletonFactory { org.koin.java.KoinJavaComponent.get<ExhPreferences>(ExhPreferences::class.java) }
    }
}
