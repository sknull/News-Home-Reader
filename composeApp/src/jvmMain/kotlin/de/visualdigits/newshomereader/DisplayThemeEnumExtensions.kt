package de.visualdigits.newshomereader

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import javax.swing.LookAndFeel

val DisplayThemeEnum.laf: LookAndFeel
    get() = when (this) {
        DisplayThemeEnum.ANTHRACITE -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_ORANGE -> FlatDarculaLaf()
        DisplayThemeEnum.ANTHRACITE_BLUE -> FlatDarculaLaf()
        DisplayThemeEnum.DARK -> FlatDarculaLaf()
        DisplayThemeEnum.LIGHT -> FlatLightLaf()
    }
