package com.betafy.feature.profile.di;

import com.betafy.feature.profile.domain.GetReaderProfileUseCase;
import com.betafy.feature.profile.domain.ProfileRepository;
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
public final class ProfileModule_ProvideGetReaderProfileUseCaseFactory implements Factory<GetReaderProfileUseCase> {
  private final Provider<ProfileRepository> repositoryProvider;

  private ProfileModule_ProvideGetReaderProfileUseCaseFactory(
      Provider<ProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetReaderProfileUseCase get() {
    return provideGetReaderProfileUseCase(repositoryProvider.get());
  }

  public static ProfileModule_ProvideGetReaderProfileUseCaseFactory create(
      Provider<ProfileRepository> repositoryProvider) {
    return new ProfileModule_ProvideGetReaderProfileUseCaseFactory(repositoryProvider);
  }

  public static GetReaderProfileUseCase provideGetReaderProfileUseCase(
      ProfileRepository repository) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideGetReaderProfileUseCase(repository));
  }
}
