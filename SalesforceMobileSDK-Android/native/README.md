# Introduction

This folder contains the native sample projects that can be used to exercise the SDK, and test projects associated with those sample projects.

# Setting up your development environment

Please follow the instructions for setting up your development environment from the README file at the root of this repo, prior to working with the Salesforce SDK.

For the rest of this document, we assume that you have setup three shell variables:

1. ANDROID_SDK_DIR pointing to the Android SDK directory
2. SALESFORCE_SDK_DIR pointing to your clone of the Salesforce Mobile SDK repository e.g. `/home/jon/SalesforceMobileSDK-Android`.
3. NATIVE_DIR pointing to $SALESFORCE_SDK_DIR/native
4. LIBS_DIR pointing to $SALESFORCE_SDK_DIR/libs

**If you haven't, just make sure to replace $ANDROID_SDK_DIR, $SALESFORCE_SDK_DIR, $NATIVE_DIR and $LIBS_DIR in the snippets below with the actual paths.**

Inside the $NATIVE_DIR, you will find several projects:

1. **TemplateApp**: Template used when creating new native application using SalesforceSDK
2. **test/TemplateAppTest**: Test project for the TemplateApp project
3. **SampleApps/RestExplorer**: An app using SalesforceSDK to explore the REST API calls
4. **SampleApps/test/RestExplorerTest**: Test project for the RestExplorer project
5. **SampleApps/NativeSqlAggregator**: An app using SalesforceSDK and SmartStore to demonstrate complex aggregate smart SQL queries

# Running sample apps from Eclipse

1. Launch Eclipse and select $SALESFORCE_SDK_DIR as your workspace 
2. Go to Window -> Preferences, choose the Android section, and enter the the Android SDK location.
3. Go to File -> Import and select General -> Existing Projects into Workspace.
4. Select $SALESFORCE_SDK_DIR as your root directory and import the projects described above.
5. Right click on any of the sample apps and choose Run As -> Android Application to run it.
6. Right click on any of the test project and choose Run As -> Android JUnit Test to run the tests.

# Creating a new native application using SalesforceSDK

There is a build.xml in $SALESFORCE_SDK_DIR.
If you type:
<pre>
ant
</pre>

It will print out information about available targets.

To create a new native application simply do:
<pre>
ant create_native -Dapp.name={appName} -Dtarget.dir={targetDir} -Dpackage.name={packageName} [-Duse.smartstore=true]
</pre>

Where:
* appName: the name for the new application 
* targetDir: the directory where the code should reside 
* packageName: the java package for the new application e.g. com.acme.mobileapp
* only pass -Duse.smartstore=true if you want SmartStore support

If it's your first time build an application with the Salesforce SDK, do the following:
cd $LIBS_DIR/SalesforceSDK
$ANDROID_SDK_DIR/android update project -p .

To build the new application, do the following:
<pre>
CD $TARGET_DIR
$ANDROID_SDK_DIR/android update project -p .
ant clean debug
</pre>

To deploy the application, start an emulator or plugin your device and run:
<pre>
ant installd
</pre>

Before you ship, make sure to plug in your oauth client id and callback url in:
<pre>
$TARGET_DIR/res/values/rest.xml
</pre>

# Running your new native application from Eclipse

1. Launch Eclipse
2. Go to File -> Import and select General -> Existing Projects into Workspace.
3. Import the $SALESFORCE_SDK_DIR/libs/SalesforceSDK library project and your newly created project $TARGET_DIR into the workspace
4. Right click on the your project and choose Run As -> Android Application

