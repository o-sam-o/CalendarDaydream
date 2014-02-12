package calendar.daydream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Instances;
import android.service.dreams.DreamService;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import calendar.daydream.data.CalendarCursorAdapter;
import calendar.daydream.data.CalendarCursorPresenter;
import calendar.daydream.ui.AttendeeAdapter;
import calendar.daydream.util.CalendarDreamContants;

public class CalendarDreamService extends DreamService implements OnItemClickListener {

	private Timer refreshTimer;
	private Cursor cursor;
	private boolean screenSaving = true;
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Allow user touch
		setInteractive(true);

		displayCalendarListView();
	}

	private void displayCalendarListView() {
		setContentView(R.layout.calendar_dream);
		ListView listView = (ListView) findViewById(R.id.cal_list_view);
		listView.setOnItemClickListener(this);
		
		screenSaving = true;
		refreshView();
	}

	private Cursor getCalendarCursor() {
		// Specify the date range you want to search for recurring
		// event instances
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.DATE, getNumberOfDaysToDisplay());
		long endMillis = endTime.getTimeInMillis();

		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		
		// Construct the query with the desired date range.
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);
		
		String query = Instances.ALL_DAY + " = ? and " + Instances.END + " < ?";
		List<String> params = new ArrayList<String>();
		params.add("0");
		params.add(String.valueOf(endMillis + 50000));
		
		//Only limit by cal id if user has explicitly set a limit via the pref screen
		Set<String> calIds = getCalendarIds();
		if(calIds != null && !calIds.isEmpty()) {
			query += " and " + Instances.CALENDAR_ID + " in (" + generateQuestionMarks(calIds.size()) + ")";
			params.addAll(calIds);
		}
		
		// Note: All day events are not displayed
		cur = cr.query(builder.build(),
				CalendarCursorPresenter.CURSOR_PROJECTION, 
				query, 
				params.toArray(new String[params.size()]), 
				Instances.BEGIN);
		return cur;
	}

	private String generateQuestionMarks(int count) {
		List<String> marks = new ArrayList<String>();
		for(int i = 0; i < count; i++) {
			marks.add("?");
		}
		return TextUtils.join(",", marks);
	}
	
	private int getNumberOfDaysToDisplay() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		return Integer.parseInt(sharedPref.getString("days_to_show_preference", String.valueOf(CalendarDreamContants.DAYS_TO_DISPLAY)));
	}

	private boolean isScreenSaveEnabled() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		return sharedPref.getBoolean("screensaver_preference", false);
	}
	
	private Set<String> getCalendarIds() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		return sharedPref.getStringSet("calendars_preference", null);
	}
	
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		setupPeriodicRefresh();
	}

	private void setupPeriodicRefresh() {
		refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Handler handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					@Override
					public void run() {
						Log.i(CalendarDreamContants.DEBUG_TAG, "Refreshing dream view");
						refreshView();
					}
				});
			}
		}, 10000, 60000); // updates each min
	}

    public void onListViewClick(View view) {
    	if(isScreenSaveEnabled() && screenSaving) {
    		refreshView();
    	}
    }
	
	private void refreshView() {
		if(cursor != null) {
			cursor.close();
		}
		cursor = getCalendarCursor();
		ListAdapter adapter = new CalendarCursorAdapter(this, cursor, true);
		
		ListView listView = (ListView) findViewById(R.id.cal_list_view);
		if(listView != null) {
			if(!isScreenSaveEnabled() || screenSaving) {
				listView.setVisibility(View.VISIBLE);
				listView.setAdapter(adapter);
				setFullscreen(false);
				screenSaving = false;
			} else {
				listView.setVisibility(View.GONE);
				setFullscreen(true);
				screenSaving = true;
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CalendarCursorPresenter presenter = new CalendarCursorPresenter((Cursor)((ListView) parent).getItemAtPosition(position), this);
		Log.i(CalendarDreamContants.DEBUG_TAG, "Item clicked " + presenter.getTitle());

		this.setContentView(R.layout.calendar_item_detailed);
		TextView titleView = (TextView) findViewById(R.id.cal_detailed_title);
		titleView.setText(presenter.getTitle());
		titleView.setTextColor(presenter.getColor());
		((TextView) findViewById(R.id.cal_detailed_subtitle)).setText(presenter.getDate());
		((TextView) findViewById(R.id.cal_detailed_time)).setText(presenter.getTime());
		((TextView) findViewById(R.id.cal_detailed_summary)).setText(presenter.getLocation());
		((TextView) findViewById(R.id.cal_detailed_duration)).setText(presenter.getDuration());
		
		AttendeeAdapter attendeesAdapter = new AttendeeAdapter(presenter.getAttendees(), this);
		ListView attendeesView = (ListView) findViewById(R.id.cal_detailed_attendees);
		attendeesView.setAdapter(attendeesAdapter);
		
		//Need to do this or clicking on the attendees list does nothing ...
		attendeesView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				detailedViewClicked(null);
			}
		});
	}

	public void detailedViewClicked(View view) {
		displayCalendarListView();
	}
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		if(refreshTimer != null) {
			refreshTimer.cancel();
		}
	}

}
