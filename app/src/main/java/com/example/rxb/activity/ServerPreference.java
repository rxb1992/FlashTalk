package com.example.rxb.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ServerPreference extends PreferenceActivity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(R.xml.loginconfig);
  }
}
