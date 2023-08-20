package dev.haqim.pitjarusapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.haqim.pitjarusapp.data.repository.AuthRepository
import dev.haqim.pitjarusapp.data.repository.StoreRepository
import dev.haqim.pitjarusapp.domain.repository.IAuthRepository
import dev.haqim.pitjarusapp.domain.repository.IStoreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(repository: AuthRepository): IAuthRepository
    
    @Binds
    @Singleton
    abstract fun provideStoreRepository(repository: StoreRepository): IStoreRepository
}
