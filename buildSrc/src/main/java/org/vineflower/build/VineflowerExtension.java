package org.vineflower.build;

import javax.inject.Inject;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;

public class VineflowerExtension {
  private final Provider<Boolean> arm64;

  @Inject
  public VineflowerExtension(final ProviderFactory providers) {
    this.arm64 = providers.systemProperty("os.arch").map(it -> it.equals("aarch64"));
  }

  public Provider<Boolean> getIsArm64() {
    return this.arm64;
  }
}
