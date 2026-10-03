package org.bytebloom.di

import org.bytebloom.data.remote.client.SupabaseClientProvider
import org.koin.dsl.module

val networkModule = module {
    single { SupabaseClientProvider.create() }
}