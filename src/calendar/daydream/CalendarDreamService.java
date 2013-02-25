package calendar.daydream;

import java.text.Format;

import android.database.Cursor;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.service.dreams.DreamService;
import android.text.format.DateFormat;
import android.widget.TextView;

public class CalendarDreamService extends DreamService {

	private static final String[] COLS = new String[] {
			CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART };

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Allow user touch
		setInteractive(true);

		// Hide system UI
		setFullscreen(true);

		// Set the dream layout
		TextView txtView = new TextView(this);
		setContentView(txtView);
		txtView.setText(getLastThreeEvents());
		txtView.setTextColor(Color.rgb(184, 245, 0));
		txtView.setTextSize(5);

	}

	private String getLastThreeEvents() {
		Cursor cursor = getContentResolver().query(
				CalendarContract.Events.CONTENT_URI, COLS, null, null, null);
		//cursor.moveToLast();

		String title = "";

	      if(cursor.getCount() > 0) {
	          while (cursor.moveToNext()) {
	            String name = cursor.getString(0);
	            title += name + " ";
	          }
	        }
	      return title;
	}

}
