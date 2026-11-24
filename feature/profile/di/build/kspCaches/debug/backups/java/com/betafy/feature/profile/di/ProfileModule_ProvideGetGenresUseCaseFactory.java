package com.betafy.feature.profile.di;

import com.betafy.feature.profile.domain.GetGenresUseCase;
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
public final class ProfileModule_ProvideGetGenresUseCaseFactory implements Factory<GetGenresUseCase> {
  private final Provider<ProfileRepository> repositoryProvider;

  private ProfileModule_ProvideGetGenresUseCaseFactory(
      Provider<ProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetGenresUseCase get() {
    return provideGetGenresUseCase(repositoryProvider.get());
  }

  public static ProfileModule_ProvideGetGenresUseCaseFactory create(
      Provider<ProfileRepository> repositoryProvider) {
    return new ProfileModule_ProvideGetGenresUseCaseFactory(repositoryProvider);
  }

  public static GetGenresUseCase provideGetGenresUseCase(ProfileRepository repository) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideGetGenresUseCase(repository));
  }
}
