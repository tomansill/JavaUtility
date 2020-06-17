package com.ansill.utility;

import com.ansill.validation.Validation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/** Utility class for detecting and managing versions within JAR */
@Immutable
public final class Version{

  /** Raw version string */
  @Nonnull
  private final String version;

  /** Major version */
  @Nonnull
  private final String majorVersion;

  /** Minor version */
  @Nonnull
  private final String minorVersion;

  /** Patch version */
  @Nullable
  private final String patchVersion;

  /** Snapshot version */
  @Nullable
  private final String snapshotVersion;

  /** Creates com.ansill.utility.Version object */
  public Version(){
    this(Version.class);
  }

  /**
   * Creates version based on given class
   *
   * @param clazz class
   */
  public Version(@Nonnull Class<?> clazz){
    this(getVersion(clazz).orElse("X.Y.Z"));
  }

  /**
   * Creates version based on input version string
   *
   * @param version version string
   */
  public Version(@Nonnull String version){

    // Assert nonnull
    Validation.assertNonnull(version, "version");

    // Get version
    this.version = version;

    // Split up the string based on dots
    String[] split = version.split("\\.");

    // Copy in values
    majorVersion = split[0];
    minorVersion = split[1];

    // Proceed only if there's more
    if(split.length > 2){

      // Last one may have snapshot version affixed to it with a dash '-', split that again
      split = split[2].split("-");

      // Copy in patch version
      patchVersion = split[0];

      // Check if snapshot is present in the version string
      snapshotVersion = split.length >= 2 ? split[1] : null;

    }else{
      patchVersion = null;
      snapshotVersion = null;
    }
  }

  /**
   * Retrieve the raw version string from class
   *
   * @param clazz input class
   * @return optional object that may contain raw version string - if version string cannot be found, then empty object will be returned
   */
  @Nonnull
  private static Optional<String> getVersion(@Nonnull Class<?> clazz){

    // Assert nonnull
    Validation.assertNonnull(clazz, "clazz");

    // Get version
    String version = clazz.getPackage().getImplementationVersion();
    if(version != null) return Optional.of(version);

    // Try specification version
    version = clazz.getPackage().getSpecificationVersion();
    if(version != null) return Optional.of(version);

    // Try finding com.ansill.utility.Version in version.properties
    try(InputStream is = Version.class.getClassLoader().getResourceAsStream("version.properties")){

      // If stream exists, attempt to open it up
      if(is != null){

        // Open up properties
        Properties properties = new Properties();
        properties.load(is);

        // Get version
        version = properties.getProperty("version");
        if(version != null) return Optional.of(version);
      }

      // No more options left, return empty;
      return Optional.empty();
    }catch(IOException e){
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the raw version string
   *
   * @return version string
   */
  @Nonnull
  public String getVersion(){
    return version;
  }

  /**
   * Gets the major version string
   *
   * @return major version
   */
  @Nonnull
  public String getMajorVersion(){
    return majorVersion;
  }

  /**
   * Gets the minor version string
   *
   * @return minor version
   */
  @Nonnull
  public String getMinorVersion(){
    return minorVersion;
  }

  /**
   * Gets the patch version string
   *
   * @return patch version
   */
  @Nonnull
  public Optional<String> getPatchVersion(){
    return Optional.ofNullable(patchVersion);
  }

  /**
   * Gets the snapshot version string as Optional object. Object will be empty if snapshot version is absent.
   *
   * @return optional object containing snapshot version
   */
  @Nonnull
  public Optional<String> getSnapshotVersion(){
    return Optional.ofNullable(snapshotVersion);
  }
}
