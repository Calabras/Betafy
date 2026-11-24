package com.betafy.feature.profile.di;

import com.betafy.feature.profile.domain.GetFeedbackTypesUseCase;
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
public final class ProfileModule_ProvideGetFeedbackTypesUseCaseFactory implements Factory<GetFeedbackTypesUseCase> {
  private final Provider<ProfileRepository> repositoryProvider;

  private ProfileModule_ProvideGetFeedbackTypesUseCaseFactory(
      Provider<ProfileRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetFeedbackTypesUseCase get() {
    return provideGetFeedbackTypesUseCase(repositoryProvider.get());
  }

  public static ProfileModule_ProvideGetFeedbackTypesUseCaseFactory create(
      Provider<ProfileRepository> repositoryProvider) {
    return new ProfileModule_ProvideGetFeedbackTypesUseCaseFactory(repositoryProvider);
  }

  public static GetFeedbackTypesUseCase provideGetFeedbackTypesUseCase(
      ProfileRepository repository) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideGetFeedbackTypesUseCase(repository));
  }
}
