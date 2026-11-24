package com.betafy.feature.profile.presentation;

import com.betafy.feature.profile.domain.GetReaderProfileUseCase;
import com.betafy.feature.profile.domain.GetWriterProfileUseCase;
import com.betafy.feature.profile.domain.ProfileTrace;
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
public final class ProfileCabinetViewModel_Factory implements Factory<ProfileCabinetViewModel> {
  private final Provider<GetReaderProfileUseCase> getReaderProfileProvider;

  private final Provider<GetWriterProfileUseCase> getWriterProfileProvider;

  private final Provider<ProfileTrace> traceProvider;

  private ProfileCabinetViewModel_Factory(
      Provider<GetReaderProfileUseCase> getReaderProfileProvider,
      Provider<GetWriterProfileUseCase> getWriterProfileProvider,
      Provider<ProfileTrace> traceProvider) {
    this.getReaderProfileProvider = getReaderProfileProvider;
    this.getWriterProfileProvider = getWriterProfileProvider;
    this.traceProvider = traceProvider;
  }

  @Override
  public ProfileCabinetViewModel get() {
    return newInstance(getReaderProfileProvider.get(), getWriterProfileProvider.get(), traceProvider.get());
  }

  public static ProfileCabinetViewModel_Factory create(
      Provider<GetReaderProfileUseCase> getReaderProfileProvider,
      Provider<GetWriterProfileUseCase> getWriterProfileProvider,
      Provider<ProfileTrace> traceProvider) {
    return new ProfileCabinetViewModel_Factory(getReaderProfileProvider, getWriterProfileProvider, traceProvider);
  }

  public static ProfileCabinetViewModel newInstance(GetReaderProfileUseCase getReaderProfile,
      GetWriterProfileUseCase getWriterProfile, ProfileTrace trace) {
    return new ProfileCabinetViewModel(getReaderProfile, getWriterProfile, trace);
  }
}
