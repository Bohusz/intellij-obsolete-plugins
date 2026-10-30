package com.intellij.gwt;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.gwt.facet.GwtFacet;
import com.intellij.gwt.module.GwtModulesManager;
import com.intellij.gwt.module.model.GwtModule;
import com.intellij.gwt.web.GwtWebUtil;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;

public class GwtFreeEditionTest extends LightJavaCodeInsightFixtureTestCase {
  public void testCoreLoadsWithoutUltimate() {
    assertTrue(PluginManagerCore.getLoadedPlugins().stream()
                 .anyMatch(plugin -> plugin.getPluginId().equals(PluginId.getId("com.intellij.gwt"))));
    assertFalse(PluginManagerCore.getLoadedPlugins().stream()
                  .anyMatch(plugin -> plugin.getPluginId().equals(PluginId.getId("com.intellij.modules.ultimate"))));
    assertFalse(GwtWebUtil.isWebSupportAvailable());
  }

  public void testFacetAndModuleModelWithoutWebServices() {
    GwtFacet facet = GwtTestCase.addGwtFacet(getModule(), null);
    assertNull(facet.getWebFacet());
    XmlFile file = (XmlFile)myFixture.addFileToProject("sample/App.gwt.xml", "<module rename-to=\"sample\"><source path=\"client\"/></module>");
    GwtModule module = GwtModulesManager.getInstance(getProject()).getGwtModule(file);
    assertNotNull(module);
    assertEquals("sample.App", module.getQualifiedName());
    assertEquals("sample", module.getOutputName());
    assertNull(GwtWebUtil.getRelativeToWebRootPath(file.getVirtualFile(), getProject()));
  }
}
