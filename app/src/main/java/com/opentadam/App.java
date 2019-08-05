/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opentadam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.contactskotlin.data.di.AppComponent;
import com.contactskotlin.data.di.DaggerAppComponent;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.opentadam.bus.HiveBus;
import com.opentadam.data.Countries;
import com.opentadam.network.rest.Service;
import com.opentadam.start.ForegroundService;
import com.opentadam.start.HashBC;
import com.opentadam.utils.LocaleUtils;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/*upd commit*/
public class App extends android.app.Application {

    public static HiveBus bus;
    public static App app;
    public static boolean uiActivated = false;
    public final BaseGoogleLocation mMyGoogleLocation;
    private final com.opentadam.network.rest.Settings workSettings;
    private final Service hashService;
    public boolean isEnabledPoush = true;
    public boolean initYandexMetrica = false;
    private boolean activated = false;
    private Tracker mTracker;
    private DefLocalePhone defLocalePhone;
    public HashBC hashBC;
    public static AppComponent appComponent;

    public App() {
        hashBC = new HashBC();
        bus = new HiveBus();
        app = this;
        workSettings = new com.opentadam.network.rest.Settings();
        hashService = new Service();
        mMyGoogleLocation = new BaseGoogleLocation(this);
    }

    public com.opentadam.network.rest.Settings getWorkSettings() {
        return workSettings;
    }

    public Service getHashService() {
        return hashService;
    }

    public void mustDie(Object object) {
/*        if (refWatcher != null) {
            refWatcher.watch(object);
        }*/
    }

    public void initLocation() {

        String language = getStringLanguage();
        LocaleUtils.setLocale(new Locale(language));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());

        Locale newLocale = new Locale(language);
        Locale.setDefault(newLocale);
        Configuration config = getResources().getConfiguration();
        config.locale = newLocale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateConfig(this, newConfig);
    }

    public DefLocalePhone getDefLocalePhone() {
        return defLocalePhone;
    }

    @Override
    public void onCreate() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        org.osmdroid.config.Configuration.getInstance().load(this, defaultSharedPreferences);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Injector.setAppContext(getApplicationContext());
        Locale aDefault = Locale.getDefault();
        defLocalePhone = new DefLocalePhone()
                .setDefaultSystemLocal(getResources().getString(R.string.dc_system_local))
                .setDefaultPhoneLocal(aDefault.getDisplayLanguage())
                .setDefaultCountryLocal(aDefault.getLanguage());

        initializeDagger();

        Timber.plant(new Timber.DebugTree());

        super.onCreate();

        if (App.app.hashBC.keyAppMetrica != null) initYandexMetrica(App.app.hashBC.keyAppMetrica);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
            logUser();
        }


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto/Roboto-Regular.ttf").setFontAttrId(R.attr.fontPath).build());

        JodaTimeAndroid.init(this);


        if (!Countries.getEnabledLocale(Countries.getLocale(BuildConfig.availableLanguages)))
            finish();
    }

    private void logUser() {
        String regKeyClient = Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null);
        if (regKeyClient != null)
            Crashlytics.setUserIdentifier(regKeyClient);    // сюда записать ID установки приложения

        String regClientPhone = Injector.getSettingsStore().readString(Constants.REG_PHONE_CLIENT, null);
        if (regClientPhone != null && regClientPhone.length() > 0)
            Crashlytics.setUserName(regClientPhone);          // здесь номер телефона клиента, если номера нет, то ничего не записываем

        String regClientEmail = Injector.getSettingsStore().readString(Constants.REG_USER_MAIL, null);
        if (regClientEmail != null && regClientEmail.length() > 0)
            Crashlytics.setUserEmail(regClientEmail);         // здесь записать email клиента, если email не установлен, то ничего
    }


    public void initYandexMetrica(String apiKey) {
        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder(apiKey).build();
        // Инициализация AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);
        // Отслеживание активности пользователей.
        YandexMetrica.enableActivityAutoTracking(this);
        initYandexMetrica = true;
        YandexMetricaPush.init(getApplicationContext());
    }

    public void setBaseLocale() {

        String language = getStringLanguage();

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    //Add MultiDex install in attachBaseContext
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    private String getStringLanguage() {
        String readString = Injector.getSettingsStore()
                .readString("getLocale", null);

        if (readString == null) {

            readString = "sys".equals(App.app.hashBC.availableLanguages[0])
                    ? "sys" : Countries.getValidateLocaleName(App.app.hashBC.availableLanguages[0]);

            Injector.getSettingsStore().writeString("getLocale"
                    , readString);
        }


        String language = "sys".equals(readString) ?

                defLocalePhone.defaultCountryLocal :
                Countries.getLocale(App.app.hashBC.availableLanguages);

        if (!Countries.getEnabledLocale(language)) {
            language = "en";
        }
        return language;
    }

    public void activate() {

        if (activated)
            return;
        activated = true;
        mMyGoogleLocation.init();
        initLocation();
        Injector.getSoundPoolClient();
        startService(new Intent(this, ForegroundService.class));
    }


    public void finish() {
        if (!activated)
            return;
        mMyGoogleLocation.deactivate();
        uiActivated = false;
        activated = false;
        stopService(new Intent(this, ForegroundService.class));
    }


    synchronized public Tracker getGATracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker("UA-52672512-10");
            mTracker.enableExceptionReporting(true);
        }
        return mTracker;
    }

    private static void initializeDagger() {
        appComponent = DaggerAppComponent.builder().build();
    }
}