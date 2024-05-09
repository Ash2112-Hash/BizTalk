package com.example.messagingapp.models;

import java.io.Serializable;

public class ProfilePreferences implements Serializable {
    private String fontSize;
    private String systemTheme;

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getSystemTheme() {
        return systemTheme;
    }

    public void setSystemTheme(String systemTheme) {
        this.systemTheme = systemTheme;
    }
}
