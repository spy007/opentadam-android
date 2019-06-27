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

public class DefLocalePhone {
    private String defaultPhoneLocal;
    public String defaultSystemLocal;
    public String defaultCountryLocal;

    public DefLocalePhone setDefaultSystemLocal(String string) {
        defaultSystemLocal = string;
        return this;
    }

    public DefLocalePhone setDefaultPhoneLocal(String string) {
        defaultPhoneLocal = string;
        return this;
    }

    public DefLocalePhone setDefaultCountryLocal(String string) {
        defaultCountryLocal = string;
        return this;
    }
}
