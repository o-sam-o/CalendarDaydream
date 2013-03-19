package calendar.daydream;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Instances;
import android.service.dreams.DreamService;
import android.widget.ListAdapter;
import android.widget.ListView;
import calendar.daydream.data.CalendarCursorAdapter;

public class CalendarDreamService extends DreamService {

	private static final String[] INSTANCE_PROJECTION = new String[] {
			Instances._ID,
			Instances.EVENT_ID, // 0
			Instances.BEGIN, // 1
			Instances.TITLE // 2
	};

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Allow user touch
		setInteractive(true);

		// Hide system UI
		setFullscreen(false);

		setContentView(R.layout.calendar_dream);
		
		ListAdapter adapter = new CalendarCursorAdapter(this, getCalendarCursor(), true);
		
		ListView listView = (ListView) findViewById(R.id.cal_list_view);
		listView.setAdapter(adapter);
		
	}

	private Cursor getCalendarCursor() {
		// Specify the date range you want to search for recurring
		// event instances
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		Calendar endTime = Calendar.getInstance();
		endTime.add(Calendar.DATE, 5);
		long endMillis = endTime.getTimeInMillis();

		Cursor cur = null;
		ContentResolver cr = getContentResolver();

		// Construct the query with the desired date range.
		Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(builder, startMillis);
		ContentUris.appendId(builder, endMillis);

		// Submit the query
		cur = cr.query(builder.build(), INSTANCE_PROJECTION, null, null, null);
		return cur;
	}

}
