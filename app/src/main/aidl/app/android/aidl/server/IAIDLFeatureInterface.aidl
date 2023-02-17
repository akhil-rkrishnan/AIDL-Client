// IAIDLFeatureInterface.aidl
package app.android.aidl.server;

// Declare any non-default types here with import statements

interface IAIDLFeatureInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     String getCurrentValue();

     List<String> getDataList(int limit);
}