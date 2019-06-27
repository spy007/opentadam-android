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

import com.google.gson.annotations.SerializedName;
import com.opentadam.network.model.DateRoute;

import java.math.BigDecimal;
import java.util.List;

public class RouteResponse {
    @SerializedName("routes")
    public List<Route> routes;

    public DateRoute getDateRoute() {
        if (routes == null || routes.size() == 0 || routes.get(0) == null
                || routes.get(0).legs == null || routes.get(0).legs.size() == 0)
            return null;
        List<Legs> legs = routes.get(0).legs;
        long valueDistance = 0;
        long valueDuration = 0;
        for (Legs l : legs) {
            valueDistance += l.distance.value;
            valueDuration += l.duration.value;
        }

        float unscaledVal = valueDistance / 1000f;
        BigDecimal bigDecimalDistanceKm = BigDecimal.valueOf(unscaledVal)
                .setScale(1,
                        BigDecimal.ROUND_CEILING);
        int minutes = (int) (valueDuration / 60);
        int hur = minutes / 60;
        if (hur != 0) {
            minutes = minutes % 60;
        }

        return new DateRoute(bigDecimalDistanceKm.toString(), hur, minutes);


    }


}
