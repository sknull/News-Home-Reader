package de.visualdigits.newshomereader

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import de.visualdigits.newshomereader.presentation.style.DisplayThemeEnum
import javax.swing.LookAndFeel

val DisplayThemeEnum.laf: LookAndFeel
    get() = when (this) {
        DisplayThemeEnum.ANTHRACITE -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_ORANGE -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_BLUE -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_GREEN -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_PURPLE -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_YELLOW -> FlatDarculaLaf()
        DisplayThemeEnum.DARK -> FlatDarculaLaf()
        DisplayThemeEnum.LIGHT -> FlatLightLaf()
    }
