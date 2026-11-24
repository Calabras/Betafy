package com.betafy.feature.profile.di;

import com.betafy.feature.profile.data.ProfileRemoteDataSource;
import com.betafy.feature.profile.domain.ProfileRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ProfileModule_ProvideProfileRepositoryFactory implements Factory<ProfileRepository> {
  private final Provider<ProfileRemoteDataSource> dataSourceProvider;

  private ProfileModule_ProvideProfileRepositoryFactory(
      Provider<ProfileRemoteDataSource> dataSourceProvider) {
    this.dataSourceProvider = dataSourceProvider;
  }

  @Override
  public ProfileRepository get() {
    return provideProfileRepository(dataSourceProvider.get());
  }

  public static ProfileModule_ProvideProfileRepositoryFactory create(
      Provider<ProfileRemoteDataSource> dataSourceProvider) {
    return new ProfileModule_ProvideProfileRepositoryFactory(dataSourceProvider);
  }

  public static ProfileRepository provideProfileRepository(ProfileRemoteDataSource dataSource) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideProfileRepository(dataSource));
  }
}
