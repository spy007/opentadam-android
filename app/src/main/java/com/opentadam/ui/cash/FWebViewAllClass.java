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

package com.opentadam.ui.cash;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.opentadam.Constants;
import com.opentadam.R;
import com.opentadam.bus.BusScript;
import com.opentadam.ui.BaseFr;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;


public class FWebViewAllClass extends BaseFr {
    @InjectView(R.id.web_view)
    WebView mWebView;
    private String url;
    private String vendorPayment;
    private int vendor;

    public static Fragment newInstance(String url, int vendor, String vendorPayment) {
        return new FWebViewAllClass().withViewId(R.layout.v2f_web_view)
                .withArgument("url", url)
                .withArgument("vendorPayment", vendorPayment)
                .withArgument("vendor", vendor);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getAWork().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments == null)
            return;

        url = arguments.getString("url");
        vendorPayment = arguments.getString("vendorPayment", "rbs");
        vendor = arguments.getInt("vendor", -1);
        setWebView();
    }

    @SuppressLint({"AddJavascriptInterface", "SetJavaScriptEnabled"})
    private void setWebView() {

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

        mWebView.loadUrl(
                url
                //      "https://www.yandex.ru"
        );

    }

    @OnClick(R.id.fwv__bask)
    public void fwvBask() {
        switch (vendor) {
            case Constants.VENDOR_FSCO:
                getAWork().showV2FSetCashOrder();
                break;

            case Constants.VENDOR_FCS:
                getAWork().showFCardSettings(getAWork().accountState, -1);
                break;
        }
    }

    @Override
    public boolean onBackPressed() {

        fwvBask();


        return true;
    }

    @Override
    public void onDestroyView() {

        getAWork().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onDestroyView();

    }

    class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getAWork()
                    , R.style.OrderDialog);
            if (!isVisible())
                return;
            builder.setTitle(R.string.title_notification_error_ssl_cert_invalid);
            builder.setMessage(R.string.notification_error_ssl_cert_invalid);
            builder.setPositiveButton(R.string.continue_button_dialog_repl, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton(R.string.cancel_button_dialog_repl, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                    getAWork().showV2FSetCashOrder();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (!isVisible())
                return;

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

    @Subscribe
    public void onBusScript(BusScript e) {
        if (!isVisible())
            return;
        if ("rbs".equals(vendorPayment)) {
            String body = e.body;
            processBody(body);
        }
    }


    private void processBody(String by) {


        if (by == null)
            return;
        String body = by.trim();
        if (body.contains("3DSecure"))

            return;

        if (body.contains("authForm"))
            return;


        boolean contains = body.contains("{\"status\"");


        if (contains) {
            String pattern = "{\"status\":\"added\"}";

            if (body.contains(pattern)) {
                onBackPressed();

            } else {
                alert(getString(R.string.error_add_card));
                getAWork().showV2FSetCashOrder();
            }
        }


    }

}
