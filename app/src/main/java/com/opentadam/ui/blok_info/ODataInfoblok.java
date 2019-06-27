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

package com.opentadam.ui.blok_info;

import android.view.View;

public class ODataInfoblok {
    private final View view;
    private final String textInfo;
    private final int widthInfoViewDP;
    private final int idView;
    public int topView;
    public int leftView;

    private ODataInfoblok(Bild bild) {
        this.view = bild.view;
        this.topView = bild.topView;
        this.textInfo = bild.textInfo;
        this.leftView = bild.leftView;
        this.widthInfoViewDP = bild.widthInfoViewDP;
        this.idView = bild.idView;
    }

    public View getView() {
        return view;
    }

    public int getIdView() {
        return idView;
    }

    public String getTextInfo() {
        return textInfo;
    }

    public int getTopView() {
        return topView;
    }

    public int getLeftView() {
        return leftView;
    }

    public int getWidthInfoViewDP() {
        return widthInfoViewDP;
    }

    public static class Bild {
        private View view;
        private String textInfo;
        private int topView;
        private int leftView;
        private int widthInfoViewDP;
        private int idView;

        public Bild setView(View view) {
            this.view = view;
            return this;
        }

        public Bild setTextInfo(String textInfo) {
            this.textInfo = textInfo;
            return this;
        }

        public Bild setTopView(int topView) {
            this.topView = topView;
            return this;
        }

        public Bild setLeftView(int leftView) {
            this.leftView = leftView;
            return this;
        }

        public Bild setWidthInfoViewDP(int widthInfoViewDP) {
            this.widthInfoViewDP = widthInfoViewDP;
            return this;
        }

        public ODataInfoblok buidl() {
            return new ODataInfoblok(this);
        }

        public Bild setIdView(int idView) {
            this.idView = idView;
            return this;
        }


    }
}
