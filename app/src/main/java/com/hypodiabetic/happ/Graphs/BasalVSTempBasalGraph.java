package com.hypodiabetic.happ.Graphs;

import android.content.Context;

import com.hypodiabetic.happ.Objects.Stats;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by Tim on 16/02/2016.
 * Basal Vs Basal Chart
 */
public class BasalVSTempBasalGraph extends CommonChartSupport{

    public BasalVSTempBasalGraph(Context context){super(context); }

    private List<Stats> statsReadings = Stats.statsList(numValues, start_time * fuzz);
    private List<PointValue> tempBasalValues = new ArrayList<>();



    public LineChartData basalvsTempBasalData() {
        LineChartData lineData = new LineChartData(addBasalvsTempBasaLines());
        lineData.setAxisYLeft(basalVsTempBasalyAxis());
        //lineData.setAxisYRight(cobPastyAxis());
        lineData.setAxisXBottom(xAxis());
        return lineData;
    }
    public Axis basalVsTempBasalyAxis() {
        Axis yAxis = new Axis();
        yAxis.setAutoGenerated(false);
        List<AxisValue> axisValues = new ArrayList<>();

        for(double j = -maxBasal.intValue(); j <= maxBasal.intValue(); j += 1) {
            //axisValues.add(new AxisValue((float)fitIOB2COBRange(j)));
            AxisValue value = new AxisValue((float)j);
            if (j==0){
                value.setLabel("Basal");
            } else if (j>0){
                value.setLabel("+" + String.valueOf(j) + "u");
            } else {
                value.setLabel(String.valueOf(j) + "u");
            }
            axisValues.add(value);
        }
        yAxis.setValues(axisValues);
        yAxis.setHasLines(true);
        yAxis.setMaxLabelChars(5);
        yAxis.setInside(true);
        return yAxis;
    }
    public List<Line> addBasalvsTempBasaLines() {
        addBasalvsTempBasalValues();
        List<Line> lines = new ArrayList<>();
        lines.add(basalvsTempBasalLine());
        lines.add(minShowLine()); //used to set an invisible line from start to end time of chart
        return lines;
    }
    public Line basalvsTempBasalLine(){
        Line cobValuesLine = new Line(tempBasalValues);
        cobValuesLine.setColor(ChartUtils.COLOR_BLUE);
        cobValuesLine.setHasLines(true);
        cobValuesLine.setHasPoints(false);
        cobValuesLine.setFilled(true);
        cobValuesLine.setCubic(false);
        return cobValuesLine;
    }
    public void addBasalvsTempBasalValues(){
        tempBasalValues.clear();                                                                    //clears past data
        Double basalDelta;
        for (Stats tempBasalReading : statsReadings) {
            if (tempBasalReading != null) {
                if (tempBasalReading.temp_basal_type.equals("High") || tempBasalReading.temp_basal_type.equals("Low")) {  //Has a Temp Basal been set?
                    basalDelta = tempBasalReading.temp_basal - tempBasalReading.basal;                  //Delta between normal Basal and Temp Basal set
                } else {
                    basalDelta = 0D;                                                                    //No Temp Basal set
                }
                tempBasalValues.add(new PointValue((float) (tempBasalReading.datetime / fuzz), basalDelta.floatValue()));
            }
        }
    }
}
