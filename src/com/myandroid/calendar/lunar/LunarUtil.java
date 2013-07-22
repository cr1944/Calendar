package com.myandroid.calendar.lunar;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.myandroid.calendar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *#Lunar#
 * A Util class  for Lunar
 *
 */
public class LunarUtil {
    private static final String TAG = "LunarUtil";
    
    public static final int LEAP_MONTH = 0;
    public static final int NORMAL_MONTH = 1;
    public static final int DECREATE_A_LUANR_YEAR = -1;
    public static final int INCREASE_A_LUANR_YEAR = 1;
    public static final String DELIM = ";";
    public static final float DAY_IN_MILLIS = 86400000.0f;

    ///M: these strings are inited in constructor @{
    private final String[] mMonthNumberArray;
    private final String[] mTensPrefixArray;
    private final String mLunarDateFormatterString;
    private final String mLunarTextLeap;
    private final String mLunarTextTensDay;
    private final String mLunarTextTwentithDay;
    private final String mLunarTextThirtiethDay;
    private final String mLunarTextYear;
    private final String mLunarTextMonth;
    private final String mLunarTextDay;

    private final String[] mSolarTermNamesArray;
    private final String mLunarFestCHUNJIE;
    private final String mLunarFestDUANWU;
    private final String mLunarFestZHONGQIU;
    private final String mLunarFestYUANDAN;
    private final String mLunarFestLAODONG;
    private final String mLunarFestGUOQING;
    private final String mLunarFestYUANXIAO;
    private final String mLunarFestQIXI;
    private final String mLunarFestCHONGYANG;
    private final String mLunarFestQINGNIAN;
    private final String mLunarFestQINGREN;
    private final String mLunarFestFUNV;
    private final String mLunarFestZHISHU;
    private final String mLunarFestYUREN;
    private final String mLunarFestERTONG;
    private final String mLunarFestJIANDANG;
    private final String mLunarFestJIANJUN;
    private final String mLunarFestJIAOSHI;
    private final String mLunarFestSHENGDAN;
    private final String[] mTianGanArray;
    private final String[] mDiZhiArray;
    private final String[] mShengXiaoArray;
    ///@}

    /*Table for SolarTerm from 1970-2050*/
    private static final int SolarTermTable[][] = {
        {6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22},       /* 1970 */        
        {6, 21, 4, 19, 6, 21, 5, 21, 6, 22, 6, 22, 8, 23, 8, 24, 8, 24, 9, 24, 8, 23, 8, 22},
        {6, 21, 5, 19, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 22, 7, 22},
        {6, 20, 4, 19, 6, 21, 5, 21, 6, 22, 6, 22, 8, 23, 8, 24, 8, 24, 9, 24, 8, 23, 8, 22},       /* 1975 */
        {6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 23, 7, 22},
        {6, 20, 4, 19, 6, 21, 5, 21, 6, 22, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 8, 22},
        {6, 21, 5, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        /* 1980 */
        {5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22},
        {6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 7, 22},
        {6, 21, 5, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 19, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},        /* 1985 */
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22},
        {6, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 7, 22},
        {6, 21, 5, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 7, 22, 7, 22},        /* 1990 */
        {6, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 8, 23, 8, 24, 8, 23, 9, 24, 8, 23, 7, 22},
        {6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22},        /* 1995 */
        {6, 21, 5, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22},
        {6, 21, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        /* 2000 */
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 23, 7, 22},
        {6, 21, 5, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},        /* 2005 */
        {5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 9, 24, 8, 22, 7, 22},
        {6, 20, 5, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 6, 21, 5, 20, 5, 21, 6, 20, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},        /* 2010 */
        {6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22},
        {6, 20, 4, 19, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21},
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22},        /* 2015 */
        {6, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21},
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 24, 8, 22, 7, 22},
        {6, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21},        /* 2020 */
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 22, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21},
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        /* 2025 */
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},
        {6, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 8, 23, 7, 22, 6, 21},
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},        /* 2030 */
        {5, 20, 5, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},        
        {6, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 8, 23, 7, 22, 6, 21},        
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},        
        {5, 20, 4, 19, 6, 21, 5, 20, 6, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},        /* 2035 */
        {6, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 8, 23, 7, 22, 6, 21},        
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        
        {5, 20, 4, 18, 5, 20, 5, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},        
        {5, 20, 4, 19, 6, 21, 5, 20, 5, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},        
        {5, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 7, 23, 7, 21, 6, 21},        /* 2040 */
        {5, 19, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 7, 21},        
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 22},        
        {5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 8, 23, 8, 23, 8, 23, 7, 22, 7, 22},        
        {5, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 21, 6, 22, 7, 22, 7, 22, 7, 23, 7, 21, 6, 21},        
        {5, 19, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21},        /* 2045 */
        {5, 20, 4, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        
        {5, 20, 4, 19, 5, 21, 5, 20, 5, 21, 6, 21, 7, 23, 7, 23, 8, 23, 8, 23, 7, 22, 7, 22},        
        {5, 20, 5, 19, 5, 20, 4, 19, 5, 20, 5, 20, 6, 22, 7, 22, 7, 22, 7, 23, 7, 21, 6, 21},        
        {5, 19, 4, 18, 5, 20, 4, 19, 5, 20, 5, 21, 7, 22, 7, 23, 7, 22, 8, 23, 7, 22, 6, 21},        
        {5, 20, 3, 18, 5, 20, 4, 20, 5, 21, 5, 21, 7, 23, 7, 23, 7, 23, 8, 23, 7, 22, 7, 21},        /* 2050 */
    };

    /**
     * Lunar info consts, for calculating leap month.
     */
    private final int[] mLunarInfoArray;

    static SimpleDateFormat sChineseDateFormat;
    private Date mBaseDate;

    /**
     * the lnuar calculate based on the year 1900
     */
    private static final int LUNAR_YEAR_BASE = 1900;
    private static final int LUNAR_YEAR_END = 2049;

    private static final int LUNAR2GRE_START_CHECK_DAY = 400;

    /**
     * get the total number days of a lunar year.
     * 
     * @param lunarYear which lunar year days number to return.
     * @return A lunar year days total number.
     */
    public int daysOfLunarYear(int lunarYear) {
        int i;
        int sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((mLunarInfoArray[lunarYear - 1900] & i) != 0) {
                sum += 1;
            }
        }
        return (sum + daysOfLeapMonthInLunarYear(lunarYear));
    }

    /**
     * get a lunar year's leap month  total days number.
     * 
     * @param lunarYear which lunar year
     * @return the total days number of this lunar year's leap month. if this
     *         luanr year hasn't leap,will return 0.
     */
    public int daysOfLeapMonthInLunarYear(int lunarYear) {
        if (leapMonth(lunarYear) != 0) {
            if ((mLunarInfoArray[lunarYear - 1900] & 0x10000) != 0) {
                return 30;
            } else {
                return 29;
            }
        }
        return 0;
    }

    /**
     * get the leap month of lunar year.
     * @param lunarYear which lunar year to return.
     * @return the number of the leapMonth.if hasn't leap
     *         month will return 0.
     */
    public int leapMonth(int lunarYear) {
        if (lunarYear < 1900 || lunarYear > 2100) {
            Log.e(TAG, "get leapMonth:" + lunarYear + "is out of range.return 0.");
            return 0;
        }
        return (int) (mLunarInfoArray[lunarYear - 1900] & 0xf);
    }


    /**
     * get the total days number of a month
     * @param luanrYear which lunar year.
     * @param lunarMonth which lunar month
     * @return the total days of this month
     */
    public int daysOfALunarMonth(int luanrYear, int lunarMonth) {
        if ((mLunarInfoArray[luanrYear - 1900] & (0x10000 >> lunarMonth)) == 0) {
            return 29;
        }
        return 30;
    }

    /**
     * get a lunar day's chnese String.
     * @param lunarDay the number of which day
     * @return the chnese string that the luanr day corresponded. like:初二,初二三.
     */
    public String getLunarDayString(int lunarDay) {
        int n = lunarDay % 10 == 0 ? 9 : lunarDay % 10 - 1;
        if (lunarDay < 0 || lunarDay > 30) {
            return "";
        }

        String ret;
        switch (lunarDay) {
        case 10:
            ret = mLunarTextTensDay;
            break;
        case 20:
            ret = mLunarTextTwentithDay;
            break;
        case 30:
            ret = mLunarTextThirtiethDay;
            break;
        default:
            ret = mTensPrefixArray[lunarDay / 10] + mMonthNumberArray[n];
            break;
        }

        return ret;
    }

    private Date getBaseDate() {
        if (mBaseDate != null) {
            return mBaseDate;
        }
        //parse baseDate
        try {
            // The Gregorian date of 1900.1.31
            mBaseDate = sChineseDateFormat.parse("1900" + mLunarTextYear + "1" + mLunarTextMonth + "31" + mLunarTextDay);
        } catch (ParseException e) {
            Log.e(TAG, "parse baseDate error.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return  the LunarDate Date corresponding  with the Gregorian Date
     * 
     * @param gregorianYear
     * @param gregorianMonth
     * @param gregorianDay
     * @return int[4],int[0] is luanrYear,int[1] is luanrMonth (index base 1),int[2] is luanrDay
     * int[3] represent is  current month leap month,if is leap month,will return LEARP_MONTH,
     * else return NORMAL_MONTH
     */
    public synchronized int[] calculateLunarByGregorian(int gregorianYear, int gregorianMonth, int gregorianDay) {
        // default lunar date is : 2000.1.1
        int lunar[] = { 2000, 1, 1, NORMAL_MONTH };
        int lunarYear;
        int lunarMonth;
        int lunarDay;

        Date baseDate = getBaseDate();

        if (baseDate == null) {
            Log.e(TAG, "baseDate is null,return lunar date:2000.1.1");
            return lunar;
        }

        //parse currentDate
        // The Gregorian date of current Time
        Date currentDate = null;
        String currentDateString;
        currentDateString = gregorianYear + mLunarTextYear + gregorianMonth + mLunarTextMonth + gregorianDay + mLunarTextDay;
        try {
            currentDate = sChineseDateFormat.parse(currentDateString);
        } catch (ParseException e) {
            Log.e(TAG, "calculateLunarByGregorian(),parse currentDate error.");
            e.printStackTrace();
        }
        if (currentDate == null) {
            Log.e(TAG, "currentDate is null,return lunar date:2000.1.1");
            return lunar;
        }

        //Calculate the number of days offset from current date to 1990.1.31
        // M: make it work correctly
        int offsetDaysFromBaseDate = Math.round(((currentDate.getTime() - baseDate.getTime())
                / DAY_IN_MILLIS));
      
        int tempLunaryear;
        int daysOfTempLunaryear = 0;
        //start calculator the lunar year.
        //loop use (offsetDaysFromBaseDate - daysOfTempLunaryear) until (offsetDaysFromBaseDate <= 0)
        //daysOfTempLunaryear is the days of 1900,1901,1902,1903.......
        //when loop end,daysOfTempLunaryear will <= 0
        //if offsetDaysFromBaseDate = 0,tempLunaryear is the right lunar year
        //if offsetDaysFromBaseDate < 0,tempLunaryear + 1 is the right lunar year.
        for (tempLunaryear = LUNAR_YEAR_BASE;
                tempLunaryear <= LUNAR_YEAR_END && offsetDaysFromBaseDate > 0;
                tempLunaryear++) {
            daysOfTempLunaryear = daysOfLunarYear(tempLunaryear);
            offsetDaysFromBaseDate -= daysOfTempLunaryear;
        }
        //if offsetDaysFromBaseDate < 0,culcalate the previous year
        if (offsetDaysFromBaseDate < 0) {
            offsetDaysFromBaseDate += daysOfTempLunaryear;
            tempLunaryear--;
        }
        lunarYear = tempLunaryear;

        // get which month is leap month,if none 0.
        int leapMonth = leapMonth(tempLunaryear);
        //represent if minus the leap month days
        boolean isMinusLeapMonthDays = false;

        int tempLunarMonth;
        int daysOfTempLunarMonth = 0;
        //start calculate the lunar month
        //now the value of offsetDaysFromBaseDate equals the day  of the lunar year,like:111/365
        //when offsetDaysFromBaseDate <= 0,then tempLunarMonth <= the right lunar month
        //so if offsetDaysFromBaseDate < 0,the previous lunar month is the right lunar month
        //if offsetDaysFromBaseDate = 0,the tempLunarMonth si the right lunar month
        for (tempLunarMonth = 1; tempLunarMonth < 13 && offsetDaysFromBaseDate > 0; tempLunarMonth++) {
            // leap month
            if (leapMonth > 0 && tempLunarMonth == (leapMonth + 1) && !isMinusLeapMonthDays) {
                --tempLunarMonth;
                isMinusLeapMonthDays = true;
                daysOfTempLunarMonth = daysOfLeapMonthInLunarYear(lunarYear);
            } else {
                daysOfTempLunarMonth = daysOfALunarMonth(lunarYear, tempLunarMonth);
            }
            //Minus a the days of a month
            offsetDaysFromBaseDate -= daysOfTempLunarMonth;
            
            //reset isMinusLeapMonthDays status
            if (isMinusLeapMonthDays && tempLunarMonth == (leapMonth + 1)) {
                isMinusLeapMonthDays = false;
            }
        }
        //if offsetDaysFromBaseDate == 0,it says  the tempLunarMonth is the leap month
        //But now the value of tempLunarMonth = leapMonth + 1,so we should minus 1.
        if (offsetDaysFromBaseDate == 0 && leapMonth > 0 && tempLunarMonth == leapMonth + 1) {
            if (isMinusLeapMonthDays) {
                isMinusLeapMonthDays = false;
            } else {
                isMinusLeapMonthDays = true;
                --tempLunarMonth;
            }
        }
        //if offsetDaysFromBaseDate < 0,calculate the previous lunar month
        if (offsetDaysFromBaseDate < 0) {
            offsetDaysFromBaseDate += daysOfTempLunarMonth;
            --tempLunarMonth;
        }
        lunarMonth = tempLunarMonth;
        
        //start calculate the lunar day.
        //now the value of the offsetDaysFromBaseDate equals the lunar day + 1,like:11/31
        //only plus 1.
        lunarDay = offsetDaysFromBaseDate + 1;

        lunar[0] = lunarYear;
        lunar[1] = lunarMonth;
        lunar[2] = lunarDay;
        lunar[3] = isMinusLeapMonthDays ? LEAP_MONTH : NORMAL_MONTH;
        return lunar;
    }
   
    /**
     *get the lunar date string by calendar
     * @param cal   Gregorian calendar objectw
     * @return   the lunar date string like:xx年[闰]xx月初xx
     */ 
    public String getLunarDateString(Calendar cal) {
        int gregorianYear = cal.get(Calendar.YEAR);
        int gregorianMonth = cal.get(Calendar.MONTH) + 1;
        int gregorianDay = cal.get(Calendar.DAY_OF_MONTH);
        
        int lunarDate[] = calculateLunarByGregorian(gregorianYear, gregorianMonth, gregorianDay);
        
        return getLunarDateString(lunarDate[0],lunarDate[1],lunarDate[2],lunarDate[3]);
    }
    
    /**
     * get the lunar date string,like xx年[闰]xx月初xx
     * 
     * @param gregorianYear
     * @param gregorianMonth
     * @param gregorianDay
     * @return the lunar date string like:xx年[闰]xx月初xx
     */
    public String getLunarDateString(int gregorianYear, int gregorianMonth,int gregorianDay) {
        int lunarDate[] = calculateLunarByGregorian(gregorianYear, gregorianMonth, gregorianDay);
        return getLunarDateString(lunarDate[0],lunarDate[1],lunarDate[2],lunarDate[3]);
    }
    
    /**
     * The really function produce lunar date string.
     * @param lunarYear
     * @param lunarMonth
     * @param lunarDay 
     * @param leapMonthCode  LEAP_MONTH or NORMAL_MONTH
     * @return the lunar date string like:xx年[闰]xx月初xx
     */
    private String getLunarDateString(int lunarYear, int lunarMonth, int lunarDay, int leapMonthCode) {
        /// M: If the leapMonthCode is LEAP_MONTH show special word by getSpecialWord function, because
        //  should show "閏" in TC, "闰" in SC.
        String luanrDateString = lunarYear + mLunarTextYear
                + (leapMonthCode == LEAP_MONTH ? mLunarTextLeap : "")
                + mMonthNumberArray[lunarMonth - 1] + mLunarTextMonth + getLunarDayString(lunarDay);
        return luanrDateString;
    }

    public String getLunarDateNoYearString(int gregorianYear, int gregorianMonth,int gregorianDay) {
        int lunarDate[] = calculateLunarByGregorian(gregorianYear, gregorianMonth, gregorianDay);
        String luanrDateString =
                (lunarDate[3] == LEAP_MONTH ? mLunarTextLeap : "")
                + mMonthNumberArray[lunarDate[1] - 1] + mLunarTextMonth + getLunarDayString(lunarDate[2]);
        return luanrDateString;
    }

    public String getFullLunarDateString(Context c, int gregorianYear, int gregorianMonth,int gregorianDay) {
        int lunarDate[] = calculateLunarByGregorian(gregorianYear, gregorianMonth, gregorianDay);
        String ganzhi = getYearGanzhi(c, lunarDate[0]);
        String animal = AnimalsYear(c, lunarDate[0]);
        String luanrDateString = ganzhi + animal + mLunarTextYear
                + (lunarDate[3] == LEAP_MONTH ? mLunarTextLeap : "")
                + mMonthNumberArray[lunarDate[1] - 1] + mLunarTextMonth + getLunarDayString(lunarDate[2]);
        return luanrDateString;
    }

    public String getYearGanzhi(Context context, int y) {
        int num = y - 1900 + 36;
        return (cyclicalm(context, num));
    }

    /*
     * return the GanZhi text from number , 0 return JiaZi
     */
    private String cyclicalm(Context context, int num) {

        return (mTianGanArray[num % 10] + mDiZhiArray[num % 12]);
    }

    /**
     * @param y year
     * @return the ShengXiao Text
     */
    public String AnimalsYear(Context context, int y) {

        return mShengXiaoArray[(y - 4) % 12];
    }

    /**
     * Decrease or Increase a lunar year's time on the Gregorian time.
     * @param calendar The Gregorian date to be decrease or increase.
     * @param lunarMonth decrease or increase  happed in which lunar month.(ignore leap month)
     * @param lunarDay decrease or increase happed in which lunar day.
     * @param operatorType 
     * @return The Gregorian date that has been decreaseed or increased a lunar year's time
     */
    public Calendar decreaseOrIncreaseALunarYear(Calendar calendar, int lunarMonth, int lunarDay,
            int operatorType) {
        if ((operatorType != INCREASE_A_LUANR_YEAR) && (operatorType != DECREATE_A_LUANR_YEAR)) {
            Log.w(TAG, "operatorType:" + operatorType + 
                    " error! Cann't increase or decrease a lunar year on this time.");
            return calendar;
        }
        
        int offset = operatorType * LUNAR2GRE_START_CHECK_DAY;
        
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTimeInMillis(calendar.getTimeInMillis());
        newCalendar.add(Calendar.DAY_OF_MONTH, offset);
        int year;
        int month;
        int day;

        int lunarDates[];
        while (true) {
            year = newCalendar.get(Calendar.YEAR);
            month = newCalendar.get(Calendar.MONTH) + 1;
            day = newCalendar.get(Calendar.DAY_OF_MONTH);
            lunarDates = calculateLunarByGregorian(year, month, day);
            if ((lunarDates[1] == lunarMonth) && (lunarDates[2] == lunarDay)) {
                break;
            }
            newCalendar.add(Calendar.DAY_OF_MONTH, -operatorType);
        }

        return newCalendar;
    }

    /** 
     * get Solar term.
     * @param gregorianYear the Gregorian year
     * @param gregorianMonth the Gregorian month
     * @param gregorianDay the Gregorian day
     * @return The two days which have solar term in xx year  xx month
     * @return null if the day is not the solar term, otherwise return the solar term name.
     */
    public String getSolarTerm(int gregorianYear, int gregorianMonth, int gregorianDay) {
        
        int days[] = getAMonthSolarTermDays(gregorianYear,gregorianMonth);
        if ((gregorianDay != days[0]) && (gregorianDay != days[1])) {
            return null;
        }
        
        String names[] = getAMonthSolarTermNames(gregorianMonth);
        if (gregorianDay == days[0]) {
            return names[0];
        } else if (gregorianDay == days[1]) {
            return names[1];
        }
        return null;
    }
    ///@}
    
    /*
     * @param year,the Gregorian year
     * @param month,the Gregorian month
     * @return The two days which have solar term in xx year  xx month
     */
    private int[] getAMonthSolarTermDays(int gregorianYear, int gregorianMonth) {
        int firstSolarTermIndex = (gregorianMonth - 1) * 2;
        int days[] = { 0, 0 };

        if (gregorianYear > 1969 && gregorianYear < 2051) {
            int firstSolarTermDay = SolarTermTable[gregorianYear - 1970][firstSolarTermIndex];
            int secondSolarTermDay = SolarTermTable[gregorianYear - 1970][firstSolarTermIndex + 1];
            days[0] = firstSolarTermDay;
            days[1] = secondSolarTermDay;
        }
        return days;
    }
    
    /*
     * @param month,the Gregorian month base 1.
     * @return The two solar term names in xx month,failed will return {"",""}
     */
    private String[] getAMonthSolarTermNames(int gregorianMonth) {
        if (gregorianMonth < 1 || gregorianMonth > 12) {
            Log.e(TAG, "getAMonthSolarTermNames(),param gregorianMonth:" + gregorianMonth + " is error");
            String solarTerms[] = {"",""};
            return solarTerms;
        }
        int firstSolarTermIndex = gregorianMonth * 2 - 1;
        return new String[] {
                getSolarTermNameByIndex(firstSolarTermIndex),
                getSolarTermNameByIndex(firstSolarTermIndex + 1) };
    }
   
    /**
     * Change given year.month.day to Chinese string.
     * in this method, the Lunar state is force updated to the 
     * transfered lunar date.
     * @param gregorianYear
     * @param gregorianMonth
     * @param gregorianDay
     * @return lunar string
     */
    public String getLunarChineseString(int gregorianYear, int gregorianMonth, int gregorianDay) {
        int lunarDate[] = calculateLunarByGregorian(gregorianYear, gregorianMonth, gregorianDay);
        boolean isLeapMonth = lunarDate[3] == LEAP_MONTH ? true : false;
        return getLunarNumber(lunarDate[1],lunarDate[2],isLeapMonth);
    }

    /**
     * Change given year.month.day to Chinese string. Festival, SolarTerm.
     * in this method, the Lunar state is force updated to the 
     * transfered lunar date.
     * @param gregorianYear
     * @param gregorianMonth
     * @param gregorianDay
     * @return lunar festival string split by DELIM
     */
    public String getLunarFestivalChineseString(int gregorianYear, int gregorianMonth, int gregorianDay) {
        StringBuilder chineseStringBuilder = new StringBuilder();
        String chineseString = null;
        
        chineseString = getGregFestival(gregorianMonth, gregorianDay);
        if (!TextUtils.isEmpty(chineseString)) {
            chineseStringBuilder.append(chineseString).append(DELIM);
        }
        int lunarDate[] = calculateLunarByGregorian(gregorianYear, gregorianMonth, gregorianDay);
        //Log.e(TAG, gregorianYear+"-"+gregorianMonth+"-"+gregorianDay+" -> "+lunarDate[0]+"-"+lunarDate[1]+"-"+lunarDate[2]+" "+lunarDate[3]);
        chineseString = getLunarFestival(lunarDate[1], lunarDate[2], lunarDate[3]);
        if (!TextUtils.isEmpty(chineseString)) {
            chineseStringBuilder.append(chineseString).append(DELIM);
        }
        chineseString = getSolarTerm(gregorianYear, gregorianMonth, gregorianDay);
        if (!TextUtils.isEmpty(chineseString)) {
            chineseStringBuilder.append(chineseString).append(DELIM);
        }
        return chineseStringBuilder.toString();
    }

    /**
     * get the current Lunar day number
     * @param lunarDay
     * @return the string as the lunar number day.
     */
    private String getLunarNumber(int lunarMonth, int lunarDay, boolean isLeapMonth) {
        // The first day of each month will display like X月 or 闰X
        if (lunarDay == 1) {
            if (isLeapMonth) {
                return mLunarTextLeap + mMonthNumberArray[lunarMonth - 1];
            }
            return mMonthNumberArray[lunarMonth - 1] + mLunarTextMonth;
        }
        return getLunarDayString(lunarDay);
    }

    /**
     * Constructor
     * @param context the context is for looking up via PluginManager
     */
    private LunarUtil(Context context) {
        Resources res = context.getResources();
        mTianGanArray = res.getStringArray(R.array.tiangan_text);
        mDiZhiArray = res.getStringArray(R.array.dizhi_text);
        mShengXiaoArray = res.getStringArray(R.array.shengxiao_text);
        mMonthNumberArray = (String[])res.getStringArray(R.array.month_number_array);
        mTensPrefixArray = (String[])res.getStringArray(R.array.tens_prefix_array);
        mLunarTextLeap = (String)res.getString(R.string.lunar_leap);
        mLunarTextTensDay = (String)res.getString(R.string.lunar_tenth_day);
        mLunarTextTwentithDay = (String)res.getString(R.string.lunar_twentieth_day);
        mLunarTextThirtiethDay = (String)res.getString(R.string.lunar_thirtieth_day);
        mLunarTextYear = (String)res.getString(R.string.lunar_year);
        mLunarTextMonth = (String)res.getString(R.string.lunar_month);
        mLunarTextDay = (String)res.getString(R.string.lunar_day);
        mLunarDateFormatterString = (String)res.getString(R.string.lunar_date_formatter);
        sChineseDateFormat = new SimpleDateFormat(mLunarDateFormatterString);

        mSolarTermNamesArray = res.getStringArray(R.array.sc_solar_terms);
        mLunarFestCHUNJIE = res.getString(R.string.lunar_fest_chunjie);
        mLunarFestDUANWU = res.getString(R.string.lunar_fest_duanwu);
        mLunarFestZHONGQIU = res.getString(R.string.lunar_fest_zhongqiu);
        mLunarFestYUANDAN = res.getString(R.string.lunar_fest_yuandan);
        mLunarFestLAODONG = res.getString(R.string.lunar_fest_laodong);
        mLunarFestGUOQING = res.getString(R.string.lunar_fest_guoqing);
        mLunarFestYUANXIAO = res.getString(R.string.lunar_fest_yuanxiao);
        mLunarFestQIXI = res.getString(R.string.lunar_fest_qixi);
        mLunarFestCHONGYANG = res.getString(R.string.lunar_fest_chongyang);
        mLunarFestQINGNIAN = res.getString(R.string.lunar_fest_qingnian);
        mLunarFestQINGREN = res.getString(R.string.lunar_fest_qingren);
        mLunarFestFUNV = res.getString(R.string.lunar_fest_funv);
        mLunarFestZHISHU = res.getString(R.string.lunar_fest_zhishu);
        mLunarFestYUREN = res.getString(R.string.lunar_fest_yuren);
        mLunarFestERTONG = res.getString(R.string.lunar_fest_ertong);
        mLunarFestJIANDANG = res.getString(R.string.lunar_fest_jiandang);
        mLunarFestJIANJUN = res.getString(R.string.lunar_fest_jianjun);
        mLunarFestJIAOSHI = res.getString(R.string.lunar_fest_jiaoshi);
        mLunarFestSHENGDAN = res.getString(R.string.lunar_fest_shengdan);

        mLunarInfoArray = res.getIntArray(R.array.lunar_info);
    }

    /**
     * M: judge whether a day is a lunar festival
     * @param lunarMonth
     * @param lunarDay
     * @param lunarMonthType lunar month type, is leap?
     * @return festival text
     */
    public String getLunarFestival(int lunarMonth, int lunarDay, int lunarMonthType) {

        if (LEAP_MONTH == lunarMonthType) {
            return null;
        }
        if ((lunarMonth == 1) && (lunarDay == 1)) {
            return mLunarFestCHUNJIE;
        } else if ((lunarMonth == 5) && (lunarDay == 5)) {
            return mLunarFestDUANWU;
        } else if ((lunarMonth == 8) && (lunarDay == 15)) {
            return mLunarFestZHONGQIU;
        } else if ((lunarMonth == 1) && (lunarDay == 15)) {
            return mLunarFestYUANXIAO;
        } else if ((lunarMonth == 7) && (lunarDay == 7)) {
            return mLunarFestQIXI;
        } else if ((lunarMonth == 9) && (lunarDay == 9)) {
            return mLunarFestCHONGYANG;
        }

        return null;
    }

    /**
     * M: get the solar term text
     * @param index
     * @return null if not solar term
     */
    private String getSolarTermNameByIndex(int index) {
        if (index < 1 || index > mSolarTermNamesArray.length) {
            Log.e(TAG, "SolarTerm should between [1, 24]");
            return null;
        }
        return mSolarTermNamesArray[index - 1];
    }

    /**
     * M: if the date is a greg festival, return the text, or null if not
     * @param gregorianMonth
     * @param gregorianDay
     * @return text or null
     */
    public String getGregFestival(int gregorianMonth, int gregorianDay) {

        if ((gregorianMonth == 1) && (gregorianDay == 1)) {
            return mLunarFestYUANDAN;
        }
        if (gregorianMonth == 5) {
            if (gregorianDay == 1) {
                return mLunarFestLAODONG;
            } else if (gregorianDay == 4) {
                return mLunarFestQINGNIAN;
            }
        }
        if ((gregorianMonth == 10) && (gregorianDay == 1)) {
            return mLunarFestGUOQING;
        }
        if ((gregorianMonth == 2) && (gregorianDay == 14)) {
            return mLunarFestQINGREN;
        }
        if (gregorianMonth == 3) {
            if (gregorianDay == 8) {
                return mLunarFestFUNV;
            } else if (gregorianDay == 12) {
                return mLunarFestZHISHU;
            }
        }
        if ((gregorianMonth == 4) && (gregorianDay == 1)) {
            return mLunarFestYUREN;
        }
        if ((gregorianMonth == 6) && (gregorianDay == 1)) {
            return mLunarFestERTONG;
        }
        if ((gregorianMonth == 7) && (gregorianDay == 1)) {
            return mLunarFestJIANDANG;
        }
        if ((gregorianMonth == 8) && (gregorianDay == 1)) {
            return mLunarFestJIANJUN;
        }
        if ((gregorianMonth == 9) && (gregorianDay) == 10) {
            return mLunarFestJIAOSHI;
        }
        if ((gregorianMonth == 12) && (gregorianDay == 25)) {
            return mLunarFestSHENGDAN;
        }
        return null;

    }

    /**
     * M: if the Locale is SC chinese, lunar can show
     * @return true if can
     */
    public boolean canShowLunar() {
        return Locale.SIMPLIFIED_CHINESE.equals(Locale.getDefault())
                || Locale.TRADITIONAL_CHINESE.equals(Locale.getDefault());
    }

    private static LunarUtil sInstance;

    public static LunarUtil getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new LunarUtil(context.getApplicationContext());
        }
        return sInstance;
    }

}
