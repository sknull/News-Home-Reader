package de.visualdigits.newshomereader

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatLightLaf
import de.visualdigits.common.domain.model.configuration.keyfactory.DisplayThemeEnum
import javax.swing.LookAndFeel

val DisplayThemeEnum.laf: LookAndFeel
    get() = when (this) {
        DisplayThemeEnum.DARK -> FlatDarculaLaf()
        DisplayThemeEnum.LIGHT -> FlatLightLaf()
    }
