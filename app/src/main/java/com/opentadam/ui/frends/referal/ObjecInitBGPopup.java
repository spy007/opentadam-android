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

class ObjecInitBGPopup {
    private int dpX;
    private float korrectX;
    private int dpY;
    private int idBgPopup;

    public static ObjecInitBGPopup init() {
        return new ObjecInitBGPopup();
    }

    public int getDpX() {
        return dpX;
    }

    public ObjecInitBGPopup setDpX(int val) {
        dpX = val;
        return this;
    }

    public float getKorrectX() {
        return korrectX;
    }

    public ObjecInitBGPopup setKorrectX(float val) {
        korrectX = val;
        return this;
    }

    public int getDpY() {
        return dpY;
    }

    public ObjecInitBGPopup setDpY(int val) {
        dpY = val;
        return this;
    }

    public int getIdBgPopup() {
        return idBgPopup;
    }

    public ObjecInitBGPopup setIdBgPopup(int val) {
        idBgPopup = val;
        return this;
    }
}
