package com.betafy.feature.profile.presentation;

import com.betafy.feature.profile.domain.GetFeedbackTypesUseCase;
import com.betafy.feature.profile.domain.GetGenresUseCase;
import com.betafy.feature.profile.domain.GetReaderProfileUseCase;
import com.betafy.feature.profile.domain.GetWriterProfileUseCase;
import com.betafy.feature.profile.domain.ProfileTrace;
import com.betafy.feature.profile.domain.SaveReaderProfileUseCase;
import com.betafy.feature.profile.domain.SaveWriterProfileUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class ProfileEditViewModel_Factory implements Factory<ProfileEditViewModel> {
  private final Provider<GetReaderProfileUseCase> getReaderProfileProvider;

  private final Provider<SaveReaderProfileUseCase> saveReaderProfileProvider;

  private final Provider<GetWriterProfileUseCase> getWriterProfileProvider;

  private final Provider<SaveWriterProfileUseCase> saveWriterProfileProvider;

  private final Provider<GetGenresUseCase> getGenresProvider;

  private final Provider<GetFeedbackTypesUseCase> getFeedbackTypesProvider;

  private final Provider<ProfileTrace> traceProvider;

  private ProfileEditViewModel_Factory(Provider<GetReaderProfileUseCase> getReaderProfileProvider,
      Provider<SaveReaderProfileUseCase> saveReaderProfileProvider,
      Provider<GetWriterProfileUseCase> getWriterProfileProvider,
      Provider<SaveWriterProfileUseCase> saveWriterProfileProvider,
      Provider<GetGenresUseCase> getGenresProvider,
      Provider<GetFeedbackTypesUseCase> getFeedbackTypesProvider,
      Provider<ProfileTrace> traceProvider) {
    this.getReaderProfileProvider = getReaderProfileProvider;
    this.saveReaderProfileProvider = saveReaderProfileProvider;
    this.getWriterProfileProvider = getWriterProfileProvider;
    this.saveWriterProfileProvider = saveWriterProfileProvider;
    this.getGenresProvider = getGenresProvider;
    this.getFeedbackTypesProvider = getFeedbackTypesProvider;
    this.traceProvider = traceProvider;
  }

  @Override
  public ProfileEditViewModel get() {
    return newInstance(getReaderProfileProvider.get(), saveReaderProfileProvider.get(), getWriterProfileProvider.get(), saveWriterProfileProvider.get(), getGenresProvider.get(), getFeedbackTypesProvider.get(), traceProvider.get());
  }

  public static ProfileEditViewModel_Factory create(
      Provider<GetReaderProfileUseCase> getReaderProfileProvider,
      Provider<SaveReaderProfileUseCase> saveReaderProfileProvider,
      Provider<GetWriterProfileUseCase> getWriterProfileProvider,
      Provider<SaveWriterProfileUseCase> saveWriterProfileProvider,
      Provider<GetGenresUseCase> getGenresProvider,
      Provider<GetFeedbackTypesUseCase> getFeedbackTypesProvider,
      Provider<ProfileTrace> traceProvider) {
    return new ProfileEditViewModel_Factory(getReaderProfileProvider, saveReaderProfileProvider, getWriterProfileProvider, saveWriterProfileProvider, getGenresProvider, getFeedbackTypesProvider, traceProvider);
  }

  public static ProfileEditViewModel newInstance(GetReaderProfileUseCase getReaderProfile,
      SaveReaderProfileUseCase saveReaderProfile, GetWriterProfileUseCase getWriterProfile,
      SaveWriterProfileUseCase saveWriterProfile, GetGenresUseCase getGenres,
      GetFeedbackTypesUseCase getFeedbackTypes, ProfileTrace trace) {
    return new ProfileEditViewModel(getReaderProfile, saveReaderProfile, getWriterProfile, saveWriterProfile, getGenres, getFeedbackTypes, trace);
  }
}
