# GWT plugin

This fork supports the free mode of unified IntelliJ IDEA 2026.2 (including 2026.2.3).
It requires Java, JavaScript, CSS, Java internationalization, and the bundled Jakarta EE API modules,
which are available without an Ultimate subscription in this version.
It does not target the older standalone Community Edition distributions.

The free mode provides GWT module/facet support, Java/RPC inspections, UiBinder and ClientBundle
coding assistance, JSNI, navigation, and Maven/Gradle import support. Build and run your application
using its Maven goals or Gradle tasks.

Web/Servlets inspections, WAR artifact import/validation, and web-server integration are optional.
Dedicated GWT run configurations and sample web-application setup load only when both the
Web/Servlets and Application Servers plugins are enabled. JavaScript debugging additionally needs
the JavaScript Debugger plugin. These integrations retain their normal Ultimate requirements.

## Build on Windows

Use JDK 25. From this directory, run:

```powershell
.\gradlew.bat buildPlugin
```

Install `build\distributions\GWT-262.0.1.zip` with **Settings → Plugins → Install Plugin from Disk**.
Select the ZIP without extracting it, then restart IntelliJ if requested.

## Check free-mode compatibility

```powershell
.\gradlew.bat testFreeEdition --no-configuration-cache
```

This runs in a separate test sandbox with the Ultimate, Web/Servlets, Application Servers,
and JavaScript Debugger plugins disabled. It checks GWT loading, module support, Java inspections,
and annotation constants used by UiBinder and ClientBundle.
