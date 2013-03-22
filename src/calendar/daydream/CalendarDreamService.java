package calendar.daydream;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract.Instances;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import calendar.daydream.data.CalendarCursorAdapter;
import calendar.daydream.data.CalendarCursorPresenter;
import calendar.daydream.util.CalendarDreamContants;

public class CalendarDreamService extends DreamService {

	private Timer refreshTimer;
	private Cursor cursor;
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Allow user touch
		setInteractive(true);

		// Hide system UI
		setFullscreen(false);

		setContentView(R.layout.calendar_dream);
		
		refreshView();
	}

	private Cursor getCalendarCursor() {
		// Specify the date range you want to search for recurring
		// event instances
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.DATE, CalendarDreamContants.DAYS_TO_DISPLAY);
		long endMillis = endTime.getTimeInMillis();

		Cursor cur = null;
		ContentResolver cr = getContentResolver();

		//TODO exclude all day events
		
		// Construct the query with the desired date range.
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);

		// Submit the query
		cur = cr.query(builder.build(), CalendarCursorPresenter.CURSOR_PROJECTION, null, null, Instances.BEGIN);
		return cur;
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

	private void refreshView() {
		if(cursor != null) {
			cursor.close();
		}
		cursor = getCalendarCursor();
		ListAdapter adapter = new CalendarCursorAdapter(this, getCalendarCursor(), true);
		
		ListView listView = (ListView) findViewById(R.id.cal_list_view);
		listView.setAdapter(adapter);	
	}
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		if(refreshTimer != null) {
			refreshTimer.cancel();
		}
	}

}
