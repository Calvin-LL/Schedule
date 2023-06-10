package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.core.utils.NotificationHelper
import com.advice.core.utils.Storage
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import com.advice.data.UserSession
import com.advice.data.datasource.ArticleDataSource
import com.advice.data.datasource.BookmarkedElementDataSource
import com.advice.data.datasource.ConferencesDataSource
import com.advice.data.datasource.DocumentsDataSource
import com.advice.data.datasource.EventsDataSource
import com.advice.data.datasource.FAQDataSource
import com.advice.data.datasource.LocationsDataSource
import com.advice.data.datasource.MapsDataSource
import com.advice.data.datasource.OrganizationsDataSource
import com.advice.data.datasource.ProductsDataSource
import com.advice.data.datasource.SpeakersDataSource
import com.advice.data.datasource.TagsDataSource
import com.advice.data.datasource.VendorsDataSource
import com.advice.data.datasource.VillagesDataSource
import com.advice.documents.DocumentsRepository
import com.advice.firebase.FirebaseUserSession
import com.advice.firebase.datasource.FirebaseArticleDataSource
import com.advice.firebase.datasource.FirebaseConferencesDataSource
import com.advice.firebase.datasource.FirebaseDocumentsDataSource
import com.advice.firebase.datasource.FirebaseEventsDataSource
import com.advice.firebase.datasource.FirebaseFAQDataSource
import com.advice.firebase.datasource.FirebaseLocationsDataSource
import com.advice.firebase.datasource.FirebaseMapsDataSource
import com.advice.firebase.datasource.FirebaseOrganizationDataSource
import com.advice.firebase.datasource.FirebaseProductsDataSource
import com.advice.firebase.datasource.FirebaseSpeakersDataSource
import com.advice.firebase.datasource.FirebaseTagsDataSource
import com.advice.firebase.datasource.FirebaseVendorsDataSource
import com.advice.firebase.datasource.FirebaseVillagesDataSource
import com.advice.locations.data.LocationRepository
import com.advice.products.ProductsViewModel
import com.advice.products.data.ProductsRepository
import com.advice.reminder.ReminderManager
import com.advice.schedule.repository.FAQRepository
import com.advice.schedule.repository.FiltersRepository
import com.advice.schedule.repository.HomeRepository
import com.advice.schedule.repository.InformationRepository
import com.advice.schedule.repository.MapRepository
import com.advice.schedule.repository.OrganizationsRepository
import com.advice.schedule.repository.ScheduleRepository
import com.advice.schedule.repository.SettingsRepository
import com.advice.schedule.repository.SpeakerRepository
import com.advice.schedule.repository.SpeakersRepository
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.information.InformationViewModel
import com.advice.schedule.ui.information.faq.FAQViewModel
import com.advice.schedule.ui.information.info.ConferenceViewModel
import com.advice.schedule.ui.information.speakers.SpeakerViewModel
import com.advice.schedule.ui.information.speakers.SpeakersViewModel
import com.advice.schedule.ui.information.vendors.OrganizationsViewModel
import com.advice.schedule.ui.maps.MapViewModel
import com.advice.schedule.ui.schedule.FiltersViewModel
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.ui.settings.SettingsViewModel
import com.advice.schedule.utilities.Analytics
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { Storage(get(), get()) }

    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }
    single { FirebaseJobDispatcher(GooglePlayDriver(get())) }

    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }

    // work manager
    single { WorkManager.getInstance(androidContext()) }

    single { Analytics(get()) }

    // reminder
    single { NotificationHelper(get()) }
    single { ReminderManager(get()) }

    // repo
    single { ScheduleRepository(get(), get(), get()) }
    single { HomeRepository(get(), get(), get()) }
    single { SpeakersRepository(get()) }
    single { SpeakerRepository(get()) }
    single { FiltersRepository(get(), get()) }
    single { FAQRepository(get()) }
    single { SettingsRepository(get(), get(), "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") }
    single { MapRepository(get()) }
    single { LocationRepository(get()) }
    single { OrganizationsRepository(get(), get()) }
    single { InformationRepository(get(), get(), get(), get()) }
    single { ProductsRepository(get()) }
    single { DocumentsRepository(get()) }


//    single<BookmarkedElementDataSource> { BookmarksDataSourceImpl(get(), get()) }
    single<BookmarkedElementDataSource> { InMemoryBookmarkedDataSourceImpl() }

    single<UserSession> { FirebaseUserSession(get(), get(), get()) }
    single<ArticleDataSource> { FirebaseArticleDataSource(get(), get()) }
    single<ConferencesDataSource> { FirebaseConferencesDataSource(get()) }
    single<EventsDataSource> { FirebaseEventsDataSource(get(), get(), get(), get()) }
    single<TagsDataSource> { FirebaseTagsDataSource(get(), get(), get()) }
    single<FAQDataSource> { FirebaseFAQDataSource(get(), get()) }
    single<LocationsDataSource> { FirebaseLocationsDataSource(get(), get()) }
    single<MapsDataSource> { FirebaseMapsDataSource(get(), androidContext().applicationContext.getExternalFilesDir(null), get(), ) }

    single<SpeakersDataSource> { FirebaseSpeakersDataSource(get(), get()) }
    single<ProductsDataSource> { FirebaseProductsDataSource(get(), get()) }

    // Organizations
    single<OrganizationsDataSource> { FirebaseOrganizationDataSource(get(), get())}
    single<VendorsDataSource> { FirebaseVendorsDataSource(get(), get()) }
    single<VillagesDataSource>{ FirebaseVillagesDataSource(get(), get()) }

    // Documents
    single<DocumentsDataSource> { FirebaseDocumentsDataSource(get(), get()) }

    viewModel { HomeViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { SpeakerViewModel() }
    viewModel { SpeakersViewModel() }
    viewModel { MapViewModel() }
    viewModel { InformationViewModel() }
    viewModel { com.advice.locations.LocationsViewModel() }
    viewModel { OrganizationsViewModel() }
    viewModel { FAQViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { FiltersViewModel() }
    viewModel { ConferenceViewModel() }

    viewModel { ProductsViewModel() }

}