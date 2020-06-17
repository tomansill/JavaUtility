package com.ansill.test;

import com.ansill.utility.Version;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.JUnitException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionTest{

  @Test
  void getVersion(){

    // Default
    Version defaultVersion = new Version();
    assertEquals("0.1.0", defaultVersion.getVersion());
    assertEquals("0", defaultVersion.getMajorVersion());
    assertEquals("1", defaultVersion.getMinorVersion());
    assertEquals(Optional.of("0"), defaultVersion.getPatchVersion());
    assertEquals(Optional.empty(), defaultVersion.getSnapshotVersion());
  }

  @Test
  void getVersionOfOtherPackage(){

    // Default
    Version defaultVersion = new Version(JUnitException.class);
    assertEquals("1.6.0", defaultVersion.getVersion());
    assertEquals("1", defaultVersion.getMajorVersion());
    assertEquals("6", defaultVersion.getMinorVersion());
    assertEquals(Optional.of("0"), defaultVersion.getPatchVersion());
    assertEquals(Optional.empty(), defaultVersion.getSnapshotVersion());
  }
}