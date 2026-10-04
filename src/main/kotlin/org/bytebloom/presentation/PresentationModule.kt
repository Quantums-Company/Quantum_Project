package org.bytebloom.presentation

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val presentationModule = module {
    factoryOf(::CrudUseCaseRunner)
}