package com.betafy.feature.profile.di;

import com.betafy.feature.profile.domain.ProfileRepository;
import com.betafy.feature.profile.domain.SaveWriterProfileUseCase;
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
public final class ProfileModule_ProvideSaveWriterProfileUseCaseFactory implements Factory<SaveWriterProfileUseCase> {
  private final Provider<ProfileRepository> repositoryProvider;

  private ProfileModule_ProvideSaveWriterProfileUseCaseFactory(
      Provider<ProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SaveWriterProfileUseCase get() {
    return provideSaveWriterProfileUseCase(repositoryProvider.get());
  }

  public static ProfileModule_ProvideSaveWriterProfileUseCaseFactory create(
      Provider<ProfileRepository> repositoryProvider) {
    return new ProfileModule_ProvideSaveWriterProfileUseCaseFactory(repositoryProvider);
  }

  public static SaveWriterProfileUseCase provideSaveWriterProfileUseCase(
      ProfileRepository repository) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideSaveWriterProfileUseCase(repository));
  }
}
