package com.hypodiabetic.happ.Objects;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Tim on 03/09/2015.
 */
@Table(name = "openaps_temp_basals", id = BaseColumns._ID)
public class TempBasal extends Model {

    @Expose
    @Column(name = "rate")
    public Double   rate=0D;                //Temp Basal Rate for (U/hr) mode
    //@Expose
    //@Column(name = "ratePercent")
    //public Integer  ratePercent=0;          //Temp Basal Rate for "percent" of normal basal
    @Expose
    @Column(name = "duration")
    public Integer  duration=0;             //Duration of Temp
    //@Expose
    //@Column(name = "basal_type")
    //public String   basal_type;             //Absolute or Percent
    @Expose
    @Column(name = "start_time")
    public Date     start_time;             //When the Temp Basal started
    @Expose
    @Column(name = "basal_adjustemnt")
    public String   basal_adjustemnt="";    //High or Low temp
    //@Expose
    //@Column(name = "current_pump_basal")
    //public Double   current_pump_basal;     //Pumps current basal
    @Expose
    @Column(name = "integration")           //JSON String holding details of integration made with this record, NS upload, etc
    public String integration;
    @Expose
    @Column(name = "aps_mode")
    public String   aps_mode;

    public Date     created_time = new Date();

    public static TempBasal getTempBasalByID(Long dbid) {
        TempBasal tempBasal = new Select()
                .from(TempBasal.class)
                .where("_id = " + dbid)
                .executeSingle();
        return tempBasal;
    }

    public static TempBasal last() {
        TempBasal last = new Select()
                .from(TempBasal.class)
                .orderBy("start_time desc")
                .executeSingle();

        if (last != null){
            return last;
        } else {
            return new TempBasal();     //returns an empty TempBasal, other than null
        }
    }

    public static TempBasal getCurrentActive(Date atThisDate) {
        TempBasal last = new Select()
                .from(TempBasal.class)
                .orderBy("start_time desc")
                .executeSingle();

        if (last != null && last.isactive(atThisDate)){
            return last;
        } else {
            return new TempBasal();     //returns an empty TempBasal, other than null or inactive basal
        }
    }

    public boolean isactive(Date atThisDate){
        if (atThisDate == null) atThisDate = new Date();

        if (start_time == null){ return false;}

        Date fur = new Date(start_time.getTime() + duration * 60000);
        if (fur.after(atThisDate)){
            return true;
        } else {
            return false;
        }
    }

    public String ageFormattted(){
        Integer minsOld = age();
        if (minsOld > 1){
            return minsOld + " mins ago";
        } else {
            return minsOld + " min ago";
        }
    }

    public int age(){
        Date timeNow = new Date();
        return (int)(timeNow.getTime() - created_time.getTime()) /1000/60;                          //Age in Mins the Temp Basal was suggested
    }

    public Date endDate(){
        Date endedAt = new Date(start_time.getTime() + (duration * 1000 * 60));                   //The date this Temp Basal ended
        return endedAt;
    }

    public Long durationLeft(){
        if (start_time != null) {
            Date timeNow = new Date();
            Long min_left = ((start_time.getTime() + duration * 60000) - timeNow.getTime()) / 60000;   //Time left to run in Mins
            return min_left;
        } else {
            return duration.longValue();
        }
    }

    public static List<TempBasal> latestTempBasals(int limit) {

        return new Select()
                .from(TempBasal.class)
                .orderBy("start_time desc")
                .limit(limit)
                .execute();
    }

    public static List<TempBasal> getTempBasalsDated(Long dateFrom, Long dateTo) {

        return new Select()
                .from(TempBasal.class)
                .where("start_time >= ? and start_time <= ?", dateFrom, dateTo)
                .orderBy("start_time desc")
                .execute();
    }

    public boolean checkIsCancelRequest() {
        if (rate.equals(0D) && duration.equals(0)){
            return true;
        } else {
            return false;
        }
    }

}
