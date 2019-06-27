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

package com.opentadam.network.rest;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.opentadam.Injector;

public class ClientAddress {
    @SerializedName("address")
    public Address address;
    @SerializedName("entrance")
    public String entrance;
    @SerializedName("flat")
    public String flat;
    @SerializedName("comment")
    public String comment;
    @SerializedName("namePrivate")
    public String namePrivate;
    @SerializedName("id")
    public Long id;

    public ClientAddress(Address address) {
        this.address = address;
    }

    public String getName() {
        return namePrivate;
    }

    @NonNull
    public String getStringNameAdress(String nameEmpty) {

        if (address == null)
            return nameEmpty;

        String alias = address.getNameAliasSearsh();
        if (alias != null)
            return alias;

        String nameAdressSearsh = address.getNameAdressSearsh();
        if (nameAdressSearsh != null)

            return nameAdressSearsh;

        return nameEmpty;
    }

    public String getTextStartAdressDopInfo(String porchMin, String flatMin, String pointToMaps) {


        String text = "";
        String text1 = getAliasAdress(pointToMaps);
        if (text1 != null)
            text += text1 + ", ";

        if (entrance != null && !"".equals(entrance))
            text += porchMin + entrance + ", ";

        if (flat != null && !"".equals(flat))
            text += flatMin + flat + ", ";

        if (comment != null && !"".equals(comment))
            text += " " + comment + ", ";

        return "".equals(text) ? null : String.copyValueOf(text.toCharArray(), 0, text.length() - 2);
    }

    private String getAliasAdress(String pointToMaps) {

        String alias = address.getNameAliasSearsh();
        if (alias != null) {
            String nameSity = address.getNameSitySearsh();
            if (nameSity != null) {
                String nameAdress = address.getNameAdressSearsh();
                if (nameAdress == null)
                    nameAdress = pointToMaps;
                String text = nameSity + (nameAdress == null ? "" : ", " + nameAdress);

                if (nameSity.equals(nameAdress))
                    text = nameSity;

                return text;
            }

        }
        return null;
    }

    public String getNotNullNamePrivate(int id) {
        if (namePrivate != null) {
            return namePrivate;

        } else {
            return getStringNameAdress(Injector.getAppContext()
                    .getString(id));
        }
    }

    public static class Builder {
        private Address address;
        private String entrance;
        private String flat;
        private String comment;
        private Long id;

        public ClientAddress buidl() {
            return new ClientAddress(this);
        }

        public Builder optAddress(Address val) {
            address = val;
            return this;
        }

        public Builder optEntrance(String val) {
            entrance = val;
            return this;
        }

        public Builder optFlat(String val) {
            flat = val;
            return this;
        }

        public Builder optComment(String val) {
            comment = val;
            return this;
        }

        public Builder optId(Long val) {
            id = val;
            return this;
        }
    }

    private ClientAddress(Builder builder) {
        address = builder.address;
        entrance = builder.entrance;
        flat = builder.flat;
        comment = builder.comment;
        id = builder.id;
    }
}
