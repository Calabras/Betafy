package com.betafy.feature.profile.di;

import com.betafy.feature.profile.domain.ProfileRepository;
import com.betafy.feature.profile.domain.SaveReaderProfileUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ProfileModule_ProvideSaveReaderProfileUseCaseFactory implements Factory<SaveReaderProfileUseCase> {
  private final Provider<ProfileRepository> repositoryProvider;

  private ProfileModule_ProvideSaveReaderProfileUseCaseFactory(
      Provider<ProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveReaderProfileUseCase get() {
    return provideSaveReaderProfileUseCase(repositoryProvider.get());
  }

  public static ProfileModule_ProvideSaveReaderProfileUseCaseFactory create(
      Provider<ProfileRepository> repositoryProvider) {
    return new ProfileModule_ProvideSaveReaderProfileUseCaseFactory(repositoryProvider);
  }

  public static SaveReaderProfileUseCase provideSaveReaderProfileUseCase(
      ProfileRepository repository) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideSaveReaderProfileUseCase(repository));
  }
}
