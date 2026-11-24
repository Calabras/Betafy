package com.betafy.feature.profile.di;

import android.content.Context;
import com.betafy.feature.profile.domain.ProfileTrace;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ProfileModule_ProvideProfileTraceFactory implements Factory<ProfileTrace> {
  private final Provider<Context> contextProvider;

  private ProfileModule_ProvideProfileTraceFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ProfileTrace get() {
    return provideProfileTrace(contextProvider.get());
  }

  public static ProfileModule_ProvideProfileTraceFactory create(Provider<Context> contextProvider) {
    return new ProfileModule_ProvideProfileTraceFactory(contextProvider);
  }

  public static ProfileTrace provideProfileTrace(Context context) {
    return Preconditions.checkNotNullFromProvides(ProfileModule.INSTANCE.provideProfileTrace(context));
  }
}
