package com.codefusiongroup.gradshub.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.codefusiongroup.gradshub.common.models.User;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserPreferencesTest {

    UserPreferences userPreferences;
    @Test
    public void getUserID() throws Exception{
        final String PREF_NAME = "com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY";
        userPreferences = UserPreferences.getInstance();
        final Context ctx = mock(Context.class);
        final SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        assertEquals("foobar", userPreferences.getUserID(ctx));
    }

    /*
    @Test
    public void saveTokenState() {
        final String PREF_NAME = "com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY";
        final String FCM_TOKEN_CHANGED = "fcm_token_changed";
        final String FCM_TOKEN = "fcm_token";
        final String token = "token";

        userPreferences = UserPreferences.getInstance();
        final Context ctx = mock(Context.class);
        final SharedPreferences.Editor edit = mock(SharedPreferences.Editor.class);
        final SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        //when(edit.putBoolean(FCM_TOKEN_CHANGED, true)).thenReturn(edit);
        when(ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()).thenReturn(edit);
        when(ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(FCM_TOKEN_CHANGED, true)
                .putString(FCM_TOKEN, token));
        //assertEquals("foobar", userPreferences.getUserID(ctx));
        userPreferences.saveTokenState(FCM_TOKEN, ctx);
    }
*/

    @Test
    public void getToken() {
        final String PREF_NAME = "com.codefusiongroup.gradshub.PREFERENCE_FILE_KEY";
        userPreferences = UserPreferences.getInstance();
        final Context ctx = mock(Context.class);
        final SharedPreferences sharedPreferences = mock(SharedPreferences.class);
        when(ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)).thenReturn(sharedPreferences);
        when(sharedPreferences.getString(anyString(), anyString())).thenReturn("foobar");
        assertEquals("foobar", userPreferences.getToken(ctx));
    }
}