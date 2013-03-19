package calendar.daydream.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.database.Cursor;
import android.provider.CalendarContract.Instances;

public class CalendarCursorPresenter {

	public static final String[] CURSOR_PROJECTION = new String[] {
			Instances._ID, Instances.EVENT_ID, // 0
			Instances.BEGIN, // 1
			Instances.TITLE // 2
	};

	private Cursor cursor;

	public CalendarCursorPresenter(Cursor cursor) {
		this.cursor = cursor;
	}

	public String getTitle() {
		return getString(Instances.TITLE);
	}

	public String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		return formatter.format(getCalendar(Instances.BEGIN).getTime());
	}

	public String getTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(getCalendar(Instances.BEGIN).getTime());
	}

	private String getString(String key) {
		int column = cursor.getColumnIndex(key);
		return cursor.getString(column);
	}

	private Calendar getCalendar(String key) {
		int column = cursor.getColumnIndex(key);
		long value = cursor.getLong(column);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);

		return calendar;
	}
}
