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

package com.opentadam.network;

import android.util.Base64;

import com.opentadam.Constants;
import com.opentadam.Injector;

import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class HiveHmacSigner {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String HMAC_SHA256_KEYSPEC = "HmacSHA256";
    private final Mac mac;
    private final String identity;
    private final SecureRandom rnd = new SecureRandom();


    private HiveHmacSigner(String identity, Mac mac) {
        this.mac = mac;
        this.identity = identity;
    }

    private static HiveHmacSigner newSigner(String identity, byte[] secret)
            throws NoSuchAlgorithmException, InvalidKeyException {

        final Mac mac = Mac.getInstance(HMAC_SHA256_KEYSPEC);
        final SecretKeySpec spec = new SecretKeySpec(secret, HMAC_SHA256_KEYSPEC);
        mac.init(spec);
        return new HiveHmacSigner(identity, mac);
    }

    public static Map<String, String> addRegAutor(String metod, String uri) {
// /api/client/mobile/1.0/service

        if (metod == null || uri == null || !Injector.getSettingsStore().isOnRegClient()) {
            return null;
        }

        long regIdClient = Injector.getSettingsStore().readLong(Constants.REG_ID_CLIENT, 0);
        String regKeyClient = Injector.getSettingsStore().readString(Constants.REG_KEY_CLIENT, null);
        if (regIdClient != 0 && regKeyClient != null) {
            try {
                HiveHmacSigner naiveHmacSigner = newSigner(String.valueOf(regIdClient),
                        android.util.Base64.decode(regKeyClient, android.util.Base64.DEFAULT));
                return naiveHmacSigner.newSignature(metod, uri);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return null;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;

    }

    private Map<String, String> newSignature(String method, String uri) {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss")
                .withLocale(Locale.ENGLISH)
                .withChronology(ISOChronology.getInstanceUTC());
        String dateHeaderValue = formatter.print(Injector.getCurrentTimeServers())
                + (Injector.deltaTimezone == null ? " +0000"
                : " " + Injector.deltaTimezone);


        final String nonce = "" + Math.abs(this.rnd.nextInt());
        final String signData = method + uri + dateHeaderValue + nonce;

        this.mac.reset();
        final byte[] signBytes = this.mac.doFinal(signData.getBytes(UTF8));
        final String signBase64 = android.util.Base64.encodeToString(signBytes,
                Base64.NO_WRAP);
        final String authHeaderValue =
                String.format("hmac %s:%s:%s", this.identity, nonce, signBase64);

        final Map<String, String> result = new HashMap<>();
        result.put("Date", dateHeaderValue);
        result.put("Authentication", authHeaderValue);
        return result;
    }
}
