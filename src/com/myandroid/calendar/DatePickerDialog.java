package com.myandroid.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;

import com.myandroid.calendar.lunar.LunarUtil;

import java.util.Calendar;

/**
 * Created by chengrui1 on 13-8-8.
 */
public class DatePickerDialog extends AlertDialog implements DialogInterface.OnClickListener,
        DatePicker.OnDateChangedListener {
    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String TAB_LUNAR = "lunar";
    private static final String TAB_SPECIAL = "special";

    private DatePicker mDatePicker;
    private TextView mTitleDate;
    private TextView mTitleLunar;
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;
    private TabHost mTabHost;
    private ViewGroup mTabsContainer;
    private TabWidget mTabWidget;
    private View mPage1;
    private View mPage2;

    private TabContentFactory mEmptyTabContent = new TabContentFactory() {
        @Override
        public View createTabContent(String tag) {
            return new View(mTabHost.getContext());
        }
    };

    private TabHost.OnTabChangeListener mTabListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            if (tabId.equals(TAB_LUNAR)) {
                mPage1.setVisibility(View.VISIBLE);
                mPage2.setVisibility(View.GONE);
            } else {
                mPage1.setVisibility(View.GONE);
                mPage2.setVisibility(View.VISIBLE);
            }
        }

    };

    private boolean mTitleNeedsUpdate = true;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *  with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
                            OnDateSetListener callBack,
                            int year,
                            int monthOfYear,
                            int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialog(Context context,
                            int theme,
                            OnDateSetListener callBack,
                            int year,
                            int monthOfYear,
                            int dayOfMonth) {
        super(context, theme);

        mCallBack = callBack;

        mCalendar = Calendar.getInstance();

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, themeContext.getText(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getText(android.R.string.cancel), this);
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_picker_dialog, null);
        setView(view);
        mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
        mTabsContainer = (ViewGroup) view.findViewById(R.id.tabs_container);
        mTabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
        mPage1 = view.findViewById(R.id.page1);
        mPage2 = view.findViewById(R.id.page2);
        mTitleDate = (TextView) mPage1.findViewById(R.id.datePicker_title);
        mTitleLunar = (TextView) mPage1.findViewById(R.id.datePicker_title2);
        mDatePicker = (DatePicker) mPage1.findViewById(R.id.datePicker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        mTabHost.setup();
        mTabHost.setOnTabChangedListener(mTabListener);
        mTabHost.addTab(mTabHost.newTabSpec(TAB_LUNAR)
                .setIndicator(themeContext.getString(R.string.goto_day))
                .setContent(mEmptyTabContent));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_SPECIAL)
                .setIndicator(themeContext.getString(R.string.goto_special_day))
                .setContent(mEmptyTabContent));
        mTabHost.setCurrentTabByTag(TAB_LUNAR);

        updateTitle(year, monthOfYear, dayOfMonth);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which == BUTTON_POSITIVE)
            tryNotifyDateSet();
    }

    public void onDateChanged(DatePicker view, int year,
                              int month, int day) {
        mDatePicker.init(year, month, day, this);
        updateTitle(year, month, day);
    }

    /**
     * Gets the {@link DatePicker} contained in this dialog.
     *
     * @return The calendar view.
     */
    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    /**
     * Sets the current date.
     *
     * @param year The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth The date day of month.
     */
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    private void tryNotifyDateSet() {
        if (mCallBack != null) {
            mDatePicker.clearFocus();
            mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
                    mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
        }
    }

    @Override
    protected void onStop() {
        //tryNotifyDateSet();
        super.onStop();
    }

    private void updateTitle(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        String title = DateUtils.formatDateTime(getContext(),
                mCalendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_ABBREV_MONTH
                        | DateUtils.FORMAT_ABBREV_WEEKDAY);
        mTitleDate.setText(title);
        LunarUtil lunarUtil = LunarUtil.getInstance(getContext());
        if (lunarUtil.canShowLunar()) {
            String lunar = lunarUtil.getLunarDateString(mCalendar);
            mTitleLunar.setVisibility(View.VISIBLE);
            mTitleLunar.setText(lunar);
        } else {
            mTitleLunar.setVisibility(View.GONE);
        }
        mTitleNeedsUpdate = true;
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDatePicker.getYear());
        state.putInt(MONTH, mDatePicker.getMonth());
        state.putInt(DAY, mDatePicker.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mDatePicker.init(year, month, day, this);
    }
}
