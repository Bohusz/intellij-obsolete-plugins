package com.intellij.gwt;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.gwt.run.GwtRunConfigurationType;
import com.intellij.gwt.run.GwtRunConfigurationEditor;
import com.intellij.gwt.web.GwtWebUtil;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class GwtOptionalIntegrationTest extends LightJavaCodeInsightFixtureTestCase {
  public void testOptionalClassesAreVisibleToPluginClassLoader() throws Exception {
    var plugin = PluginManagerCore.getPlugin(PluginId.getId("com.intellij.gwt"));
    assertNotNull(plugin);
    ClassLoader loader = plugin.getPluginClassLoader();
    assertNotNull(Class.forName("com.intellij.javaee.ui.packaging.ExplodedWarArtifactType", true, loader));
    assertNotNull(Class.forName("org.jetbrains.debugger.sourcemap.SourceFileResolver", true, loader));
  }

  public void testRunConfigurationRemainsAvailableWithWebSupport() {
    assertTrue(GwtWebUtil.isWebSupportAvailable());
    var configuration = GwtRunConfigurationType.getFactory().createTemplateConfiguration(getProject());
    assertNotNull(configuration);
    var editor = new GwtRunConfigurationEditor(getProject());
    try {
      assertNotNull(editor.getComponent());
    }
    finally {
      Disposer.dispose(editor);
    }
  }
}
