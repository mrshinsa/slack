package com.slack.exercise.dagger

import android.app.Application
import android.content.Context
import com.slack.exercise.api.SlackApi
import com.slack.exercise.api.SlackApiImpl
import com.slack.exercise.dataprovider.UserSearchResultDataProvider
import com.slack.exercise.dataprovider.UserSearchResultDataProviderImpl
import com.slack.exercise.ui.usersearch.UserSearchPresenter
import com.slack.exercise.ui.usersearch.UserSearchPresenterImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module to setup Application scoped instances that require providers.
 */
@Module
abstract class AppModule {
  @Binds
  abstract fun provideUserSearchResultDataProvider(
      dataProvider: UserSearchResultDataProviderImpl): UserSearchResultDataProvider

  @Binds
  abstract fun provideUserSearchPresenter(
    dataProvider: UserSearchPresenterImpl
  ): UserSearchPresenter

  @Binds
  abstract fun provideSlackApi(apiImpl: SlackApiImpl): SlackApi

  companion object {
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
      return application.applicationContext
    }
  }
}