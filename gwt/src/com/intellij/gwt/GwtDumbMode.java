package com.intellij.gwt;

import com.intellij.openapi.project.DumbModeBlockedFunctionality;

public final class GwtDumbMode {
  /**
   * {@code DumbModeBlockedFunctionality.Gwt} was removed from the platform in 263.
   * Replace this value if the platform provides a successor.
   */
  public static final DumbModeBlockedFunctionality BLOCKED_FUNCTIONALITY = DumbModeBlockedFunctionality.Other;

  private GwtDumbMode() {
  }
}
