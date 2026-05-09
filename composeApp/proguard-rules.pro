# Erhalte alle Compose-spezifischen Dinge
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Erhalte deine eigenen Klassen, damit Reflection (z.B. für Version oder Datenbanken) funktioniert
-keep class de.visualdigits.newshomereader.** { *; }
-keep class de.visualdigits.generated.** { *; }

# Falls du Kotlin Serialization oder Ktor nutzt
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod
-dontwarn kotlinx.serialization.**
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
}
