package com.jeremykenedy.pipeworksdream;

import org.junit.Test;

public final class CoverageTest {
  @Test
  public void verifiesRoundedTubeGeometry() {
    PipeMeshTest.main(new String[0]);
  }

  @Test
  public void validatesSettingResolutionAndProviderValues() {
    PipeworksOptionsTest.main(new String[0]);
  }
}
