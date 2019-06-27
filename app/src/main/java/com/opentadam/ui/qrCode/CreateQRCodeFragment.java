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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.opentadam.R;
import com.opentadam.ui.BaseFr;

import butterknife.InjectView;
import butterknife.OnClick;

public class CreateQRCodeFragment extends BaseFr {
    @InjectView(R.id.msp_progress)
    com.opentadam.ui_payemnts_metods.ProgressView progressView;
    @InjectView(R.id.img_result_qr)
    ImageView imgResult;

    public static Fragment instance(String referralCode) {
        return new CreateQRCodeFragment()
                .withViewId(R.layout.create_qr_fragment)
                .withArgument("referralCode", referralCode);
    }

    @OnClick(R.id.qr_bask)
    public void hideQR() {
        getAWork().hideUpdateApp();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String referralCode = getArguments().getString("referralCode");
        QrTask qrTask = new QrTask();
        qrTask.execute(referralCode);
    }

    private Bitmap encodeAsBitmap(String contents) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(contents, BarcodeFormat.QR_CODE, getPX(256), getPX(256));
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bmp;

            //   imgResult.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

   private class QrTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            return encodeAsBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if(!isVisible())
                return;
            progressView.setVisibility(View.GONE);

            if(result !=null){
                imgResult.setImageBitmap(result);
            }else{
                alert(getString(R.string.error_create_qr));
                hideQR();
            }
        }
    }
}
