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

package com.opentadam.ui.qrCode;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.zxing.Result;
import com.opentadam.R;
import com.opentadam.ui.BaseFr;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerQRFragment extends BaseFr implements ZXingScannerView.ResultHandler {
    private static final int VOLUME = 55;
    private static final int TONE_DURATION = 50;
    @InjectView(R.id.qr_bask)
    View qrBask;
    private ZXingScannerView mScannerView;

    public static Fragment instance() {
        return new ScannerQRFragment();
    }

    private void playBeep() {

        // R23MGC
        setVibrate();

        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, VOLUME);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, TONE_DURATION);
    }

    private void setVibrate() {
        Vibrator vibrator = (Vibrator) getAWork().getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(100L);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflate = inflater.inflate(R.layout.scanner_qr_fragment, container, false);

        FrameLayout contQr = inflate.findViewById(R.id.cont_qr);
        mScannerView = new ZXingScannerView(getActivity());
        contQr.addView(mScannerView);

        View heder = LayoutInflater.from(getContext())
                .inflate(R.layout.gr_heder, contQr, false);
        contQr.addView(heder);

        ButterKnife.inject(this, inflate);
        return inflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setBorderCornerRadius(getPX(8));
        mScannerView.setBorderLineLength(getPX(32));
        mScannerView.setBorderStrokeWidth(getPX(2));
        mScannerView.setSquareViewFinder(true);
        mScannerView.setAutoFocus(true);
        mScannerView.setFlash(false);
        mScannerView.setLaserEnabled(false);
        mScannerView.setBorderColor(Color.parseColor("#FFFFFF"));
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

    }

    @OnClick(R.id.qr_bask)
    public void hideQR() {
        getAWork().hideUpdateApp();
    }

    @Override
    public void handleResult(Result rawResult) {
        playBeep();
        String text = rawResult.getText();
        Uri uri = Uri.parse(text);
        String uriPath = uri.getPath().replace("/","");
        getAWork().initQR(uriPath);
        Log.e("gfgf", "uriPath=" + uriPath);
        hideQR();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
