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

package com.opentadam.ui.frends.referal;

public class FrefObjectInitPopup {
    private int dpX;
    private float korrectX;
    private int dpY;
    private int idBgPopup;
    private Transaction transaction;

    public int getDpX() {
        return dpX;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public float getKorrectX() {
        return korrectX;
    }

    public int getDpY() {
        return dpY;
    }

    public int getIdBgPopup() {
        return idBgPopup;
    }

    public static FrefObjectInitPopup invoke() {
        return new FrefObjectInitPopup();
    }

    public FrefObjectInitPopup setDpX(int dpX) {
        this.dpX = dpX;
        return this;
    }

    public FrefObjectInitPopup setKorrectX(float korrectX) {
        this.korrectX = korrectX;
        return this;
    }

    public FrefObjectInitPopup setDpY(int dpY) {
        this.dpY = dpY;
        return this;
    }

    public FrefObjectInitPopup setIdBgPopup(int idBgPopup) {
        this.idBgPopup = idBgPopup;
        return this;
    }

    public FrefObjectInitPopup setTransaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }
}
