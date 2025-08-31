# FiatX Consumer ProGuard Rules
# Rules for library consumers

# Keep public API classes
-keep public class com.mohsin.fiatx.** {
    public *;
}

# Keep data classes used in public APIs
-keep class com.mohsin.fiatx.data.local.FiatCurrencyEntity { *; }

# Keep exception classes
-keep public class * extends java.lang.Exception
