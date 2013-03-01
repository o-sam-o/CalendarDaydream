package calendar.daydream;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Instances;
import android.service.dreams.DreamService;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CalendarDreamService extends DreamService {

	private static final String DEBUG_TAG = "CDr";
	public static final String[] INSTANCE_PROJECTION = new String[] {
			Instances._ID,
			Instances.EVENT_ID, // 0
			Instances.BEGIN, // 1
			Instances.TITLE // 2
	};

	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_BEGIN_INDEX = 1;
	private static final int PROJECTION_TITLE_INDEX = 2;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Allow user touch
		setInteractive(true);

		// Hide system UI
		setFullscreen(false);

		setContentView(R.layout.calendar_dream);
		
		int[] toViews = {android.R.id.text1};
		ListAdapter adapter = new SimpleCursorAdapter(this, 
                android.R.layout.simple_list_item_1, getCalendarCursor(),
                new String[] {Instances.TITLE}, toViews, 0);
		
		ListView listView = (ListView) findViewById(R.id.cal_list_view);
		listView.setAdapter(adapter);
		
//		// Set the dream layout
//		TextView txtView = new TextView(this);
//		setContentView(txtView);
//		txtView.setText(getLastThreeEvents());
//		txtView.setTextColor(Color.rgb(184, 245, 0));
//		txtView.setTextSize(30);

	}

//	private String getLastThreeEvents() {
//		Cursor cur = getCalendarCursor();
//
//		String result = "";
//		while (cur.moveToNext()) {
//			String title = null;
//			long eventID = 0;
//			long beginVal = 0;
//
//			// Get the field values
//			eventID = cur.getLong(PROJECTION_ID_INDEX);
//			beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
//			title = cur.getString(PROJECTION_TITLE_INDEX);
//
//			// Do something with the values.
//			Log.i(DEBUG_TAG, "Event:  " + title);
//			Calendar calendar = Calendar.getInstance();
//			calendar.setTimeInMillis(beginVal);
//			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
//			Log.i(DEBUG_TAG, "Date: " + formatter.format(calendar.getTime()));
//            
//			result += title + " " + formatter.format(calendar.getTime()) + "\n\n";
//		}
//		
//		return result;
//	}

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
