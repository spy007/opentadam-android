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

package com.opentadam.ui.frends;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;

public class ObjList {
    private float tempoRange;
    private List<Line> lines;

    public float getTempoRange() {
        return tempoRange;
    }

    public List<Line> getLines() {
        return lines;
    }

    public ObjList invoke(int color) {
        float maxHeight = 200f;
        tempoRange = 10f;

        float scale = tempoRange / maxHeight;


        int numValues = 6;


        List<PointValue> values;
        lines = new ArrayList<>();

        values = new ArrayList<>();
        for (int i = 0; i < numValues; ++i) {

            float rawHeight = (float) (Math.random() * 200);
            float normalizedHeight = rawHeight * scale;
            values.add(new PointValue(i, normalizedHeight));
        }

        Line line = new Line(values);

        line.setColor(color);
        line.setHasPoints(false);
        line.setFilled(true);
        line.setStrokeWidth(3);
        lines.add(line);
        return this;
    }
}