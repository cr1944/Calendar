package com.myandroid.calendar.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ResourceCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.myandroid.calendar.AsyncQueryService;
import com.myandroid.calendar.CalendarController;
import com.myandroid.calendar.CalendarEventModel;
import com.myandroid.calendar.GeneralPreferences;
import com.myandroid.calendar.R;
import com.myandroid.calendar.Utils;

/**
 * Created by chengrui1 on 13-7-31.
 */
public class CreateEventDialogFragment extends DialogFragment implements TextWatcher,
        AdapterView.OnItemSelectedListener {
    private static final String TAG = "CreateEventDialogFragment";
    private static final String KEY_DATE_STRING = "date_string";
    private static final String KEY_DATE_MILLIS = "date_in_millis";
    private static final String DAY_FORMAT = "%a, %b %d, %Y";
    private static final int TOKEN_CALENDARS = 1 << 3;
    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    private EditText mEventTitle;
    private TextView mDate;
    private Spinner mCalendarsSpinner;
    private View mCalendarSelectorGroup;
    private AlertDialog mAlertDialog;
    private CalendarController mController;
    private EditEventHelper mEditEventHelper;
    private CalendarEventModel mModel;
    private CalendarQueryService mService;
    private long mDateInMillis;
    private String mDateString;
    private long mCalendarId = -1L;
    private String mCalendarOwner;
    private Button mButtonAddEvent;

    public CreateEventDialogFragment() {

    }

    public CreateEventDialogFragment(Time day) {
        setDay(day);
    }

    private void createAllDayEvent() {
        mModel.mStart = mDateInMillis;
        mModel.mEnd = (DAY_IN_MILLIS + mDateInMillis);
        mModel.mTitle = mEventTitle.getText().toString();
        mModel.mAllDay = true;
        mModel.mCalendarId = mCalendarId;
        mModel.mOwnerAccount = mCalendarOwner;
        if (mEditEventHelper.saveEvent(mModel, null, Utils.MODIFY_UNINITIALIZED))
            Toast.makeText(getActivity(), R.string.creating_event, Toast.LENGTH_SHORT).show();
    }

    public void setDay(Time day) {
        mDateString = day.format(DAY_FORMAT);
        mDateInMillis = day.toMillis(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        mController = CalendarController.getInstance(activity);
        mEditEventHelper = new EditEventHelper(activity);
        mModel = new CalendarEventModel(activity);
        mService = new CalendarQueryService(activity);
        mService.startQuery(TOKEN_CALENDARS, null, CalendarContract.Calendars.CONTENT_URI,
                EditEventHelper.CALENDARS_PROJECTION, EditEventHelper.CALENDARS_WHERE_WRITEABLE_VISIBLE, null, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDateString = savedInstanceState.getString(KEY_DATE_STRING);
            mDateInMillis = savedInstanceState.getLong(KEY_DATE_MILLIS);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DATE_STRING, mDateString);
        outState.putLong(KEY_DATE_MILLIS, mDateInMillis);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Activity activity = getActivity();
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.edit_event_simple, null);
        mDate = (TextView) v.findViewById(R.id.event_day);
        mEventTitle = (EditText) v.findViewById(R.id.event_title);
        mEventTitle.addTextChangedListener(this);
        mCalendarSelectorGroup = v.findViewById(R.id.calendar_selector_group);
        mCalendarsSpinner = (Spinner) v.findViewById(R.id.calendars_spinner);
        mCalendarsSpinner.setOnItemSelectedListener(this);
        if (mDateString != null) {
            mDate.setText(mDateString);
        }
        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.new_event_dialog_option)
                .setView(v)
                .setPositiveButton(R.string.save_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createAllDayEvent();
                        dismiss();
                    }
                })
                .setNeutralButton(R.string.edit_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mController.sendEventRelatedEventWithExtra(this, CalendarController.EventType.CREATE_EVENT, -1L,
                                mDateInMillis, DAY_IN_MILLIS + mDateInMillis, 0, 0, CalendarController.EXTRA_CREATE_ALL_DAY, -1L);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.discard_label, null)
                .create();
        return mAlertDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mButtonAddEvent == null) {
            mButtonAddEvent = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        }
        mButtonAddEvent.setEnabled(mEventTitle.getText().toString().length() > 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) parent.getItemAtPosition(position);
        if (c == null) {
            // TODO: can this happen? should we drop this check?
            return;
        }
        int colorColumn = c.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR);
        int color = c.getInt(colorColumn);
        int displayColor = Utils.getDisplayColorFromColor(color);
        mCalendarSelectorGroup.setBackgroundColor(displayColor);
        int idColumn = c.getColumnIndexOrThrow(CalendarContract.Calendars._ID);
        mCalendarId = c.getLong(idColumn);
        mCalendarOwner = c.getString(c.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setDefaultCalendarView(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.no_syncable_calendars).setIconAttribute(
                    android.R.attr.alertDialogIcon).setMessage(R.string.no_calendars_found)
                    .setPositiveButton(R.string.add_account, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent nextIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                            final String[] array = {"com.android.calendar"};
                            nextIntent.putExtra(Settings.EXTRA_AUTHORITIES, array);
                            nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(nextIntent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, null);
            builder.show();
            return;
        }
        int defaultCalendarPosition = findDefaultCalendarPosition(cursor);

        CalendarsAdapter adapter = new CalendarsAdapter(getActivity(), cursor);
        mCalendarsSpinner.setAdapter(adapter);
        mCalendarsSpinner.setSelection(defaultCalendarPosition);
    }

    private int findDefaultCalendarPosition(Cursor calendarsCursor) {
        if (calendarsCursor.getCount() <= 0) {
            return -1;
        }

        String defaultCalendar = Utils.getSharedPreference(
                getActivity(), GeneralPreferences.KEY_DEFAULT_CALENDAR, (String) null);

        if (defaultCalendar == null) {
            return 0;
        }
        int calendarsOwnerColumn = calendarsCursor.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT);
        int position = 0;
        calendarsCursor.moveToPosition(-1);
        while (calendarsCursor.moveToNext()) {
            if (defaultCalendar.equals(calendarsCursor.getString(calendarsOwnerColumn))) {
                return position;
            }
            position++;
        }
        return 0;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mButtonAddEvent != null) {
            mButtonAddEvent.setEnabled(s.length() > 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class CalendarQueryService extends AsyncQueryService {

        public CalendarQueryService(Context context) {
            super(context);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            setDefaultCalendarView(cursor);
        }
    }

    static private class CalendarsAdapter extends ResourceCursorAdapter {
        public CalendarsAdapter(Context context, Cursor c) {
            super(context, R.layout.calendars_item, c);
            setDropDownViewResource(R.layout.calendars_dropdown_item);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            View colorBar = view.findViewById(R.id.color);
            int colorColumn = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR);
            int nameColumn = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
            int ownerColumn = cursor.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT);
            if (colorBar != null) {
                colorBar.setBackgroundColor(Utils.getDisplayColorFromColor(cursor
                        .getInt(colorColumn)));
            }

            TextView name = (TextView) view.findViewById(R.id.calendar_name);
            if (name != null) {
                String displayName = cursor.getString(nameColumn);
                name.setText(displayName);

                TextView accountName = (TextView) view.findViewById(R.id.account_name);
                String owner = cursor.getString(ownerColumn);
                if (accountName != null && !displayName.equals(owner)) {
                    accountName.setText(owner);
                    accountName.setVisibility(TextView.VISIBLE);
                } else {
                    accountName.setVisibility(TextView.GONE);
                }
            }
        }

    }
}
