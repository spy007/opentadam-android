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

package com.opentadam.utils;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;


public interface LatLngInterpolator {

    LatLng interpolate(float fraction, LatLng a, LatLng b);

/*

    class LinearFixed implements LatLngInterpolator {
        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lngDelta = b.longitude - a.longitude;
            // Take the shortest path across the 180th meridian.
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360;
            }
            double lng = lngDelta * fraction + a.longitude;
            return new LatLng(lat, lng);
        }
    }
*/

    class Spherical implements LatLngInterpolator {
        @Override
        public LatLng interpolate(float fraction, LatLng from, LatLng to) {
            // http://en.wikipedia.org/wiki/Slerp
            double fromLat = toRadians(from.latitude);
            double fromLng = toRadians(from.longitude);
            double toLat = toRadians(to.latitude);
            double toLng = toRadians(to.longitude);
            double cosFromLat = cos(fromLat);
            double cosToLat = cos(toLat);

            // Computes Spherical interpolation coefficients.
            double angle = computeAngleBetween(fromLat, fromLng, toLat, toLng);
            double sinAngle = sin(angle);
            if (sinAngle < 1E-6) {
                return from;
            }
            double a = sin((1 - fraction) * angle) / sinAngle;
            double b = sin(fraction * angle) / sinAngle;

            // Converts from polar to vector and interpolate.
            double x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng);
            double y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng);
            double z = a * sin(fromLat) + b * sin(toLat);

            // Converts interpolated vector back to polar.
            double lat = atan2(z, sqrt(x * x + y * y));
            double lng = atan2(y, x);
            return new LatLng(toDegrees(lat), toDegrees(lng));
        }

        private double computeAngleBetween(double fromLat, double fromLng, double toLat, double toLng) {
            // Haversine's formula
            double dLat = fromLat - toLat;
            double dLng = fromLng - toLng;
            return 2 * asin(sqrt(pow(sin(dLat / 2), 2) +
                    cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2)));
        }
    }
}
