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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.util.Property;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.SphericalUtil;
import com.opentadam.network.rest.Driver;
import com.opentadam.network.rest.GpsPosition;


public class CarIconAnimate {
    private int timeDuration;
    public void initCarMarker(Marker carMarker, int timeDuration) {
        animateMarker(carMarker);
        this.timeDuration = timeDuration;
    }

    private float getAzimut(LatLng car, LatLng client) {

        return (float) SphericalUtil.computeHeading(car, client);
    }

    private void animateMarker(Marker marcerCar) {

        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();

        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        GpsPosition location;
        Object tagMarker = marcerCar.getTag();
        if (tagMarker instanceof Driver) {
            Driver tag = (Driver) tagMarker;
            location = tag.location;
        } else if (tagMarker instanceof GpsPosition) {
            location = (GpsPosition) tagMarker;
        } else return;

        LatLng latLng = location.getLatLng();

        LatLng position = marcerCar.getPosition();

        double metr = SphericalUtil.computeDistanceBetween(latLng, position);
        if (metr < 100)
            return;

        float az = getAzimut(position, latLng);


        marcerCar.setRotation(az);
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
        ObjectAnimator animator = ObjectAnimator.ofObject(marcerCar, property, typeEvaluator, latLng);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });

        animator.setDuration(timeDuration);
        animator.start();
    }


}
