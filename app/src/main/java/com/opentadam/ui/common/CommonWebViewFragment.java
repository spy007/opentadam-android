/*
 * Copyright (C) 2019 TadamGroup, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.opentadam.ui.common;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.opentadam.R;
import com.opentadam.bus.BusScript;
import com.opentadam.ui.BaseFr;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CommonWebViewFragment extends BaseFr {
    @InjectView(R.id.common_web_view)
    WebView mWebView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater
                .inflate(R.layout.common_web_view_fragment, container, false);

        ButterKnife.inject(this, v);

        return v;
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void setWebView(String url) {
        getAWork().showProgressDevault();

        WebSettings settings = mWebView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultTextEncodingName("utf-8");

        settings.setBuiltInZoomControls(true);
        settings.setUserAgentString(
                "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:28.0)" +
                        " Gecko/20100101 Firefox/28.0");

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);

        mWebView.setWebViewClient(new GeoWebViewClient());
        mWebView.setWebChromeClient(new GeoWebChromeClient());
        mWebView.addJavascriptInterface(new WebViewInjector(), "WebInjector");
        mWebView.setInitialScale(1);
        mWebView.clearCache(true);


        mWebView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        mWebView.loadUrl(url);

    }

    @Subscribe
    public void onBusScript(BusScript e) {
        if (!isVisible())
            return;

    }


    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (getAWork() == null || !isVisible())
                return;
            getAWork().hideProgressDevault();
            view.loadUrl("javascript:window.WebInjector.processBody(document.getElementsByTagName('body')[0].innerHTML);");
        }
    }

    /**
     * WebChromeClient subclass handles UI-related calls Note: think chrome as
     * in decoration, not the Chrome browser
     */
    class GeoWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {

        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
            if (!isVisible())
                return;
            callback.invoke(origin, true, false);
        }

    }


}
