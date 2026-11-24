package com.betafy.feature.profile.di;

import com.betafy.feature.profile.data.ProfileRemoteDataSource;
import com.betafy.feature.profile.domain.ProfileTrace;
import com.google.firebase.database.FirebaseDatabase;
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
public final class ProfileModule_ProvideProfileRemoteDataSourceFactory implements Factory<ProfileRemoteDataSource> {
  private final Provider<FirebaseDatabase> databaseProvider;

  private final Provider<ProfileTrace> traceProvider;

  private ProfileModule_ProvideProfileRemoteDataSourceFactory(
      Provider<FirebaseDatabase> databaseProvider, Provider<ProfileTrace> traceProvider) {
    this.databaseProvider = databaseProvider;
    this.traceProvider = traceProvider;
  }

  @Override
  public ProfileRemoteDataSource get() {
    return provideProfileRemoteDataSource(databaseProvider.get(), traceProvider.get());
  }

  public static ProfileModule_ProvideProfileRemoteDataSourceFactory create(
      Provider<FirebaseDatabase> databaseProvider, Provider<ProfileTrace> traceProvider) {
    return new ProfileModule_ProvideProfileRemoteDataSourceFactory(databaseProvider, traceProvider);
  }

  public static ProfileRemoteDataSource provideProfileRemoteDataSource(FirebaseDatabase database,
      ProfileTrace trace) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideProfileRemoteDataSource(database, trace));
  }
}
