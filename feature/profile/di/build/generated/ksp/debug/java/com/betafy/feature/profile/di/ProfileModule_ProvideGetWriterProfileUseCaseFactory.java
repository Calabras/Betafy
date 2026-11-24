package com.betafy.feature.profile.di;

import com.betafy.feature.profile.domain.GetWriterProfileUseCase;
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
public final class ProfileModule_ProvideGetWriterProfileUseCaseFactory implements Factory<GetWriterProfileUseCase> {
  private final Provider<ProfileRepository> repositoryProvider;

  private ProfileModule_ProvideGetWriterProfileUseCaseFactory(
      Provider<ProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetWriterProfileUseCase get() {
    return provideGetWriterProfileUseCase(repositoryProvider.get());
  }

  public static ProfileModule_ProvideGetWriterProfileUseCaseFactory create(
      Provider<ProfileRepository> repositoryProvider) {
    return new ProfileModule_ProvideGetWriterProfileUseCaseFactory(repositoryProvider);
  }

  public static GetWriterProfileUseCase provideGetWriterProfileUseCase(
      ProfileRepository repository) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideGetWriterProfileUseCase(repository));
  }
}
