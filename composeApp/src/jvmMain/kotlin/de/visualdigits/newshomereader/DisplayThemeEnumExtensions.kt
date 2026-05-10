package de.visualdigits.newshomereader

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import javax.swing.LookAndFeel

val DisplayThemeEnum.laf: LookAndFeel
    get() = when (this) {
        DisplayThemeEnum.ANTHRACITE -> FlatDarculaLaf()
        DisplayThemeEnum.LIGHT -> FlatLightLaf()
    }
