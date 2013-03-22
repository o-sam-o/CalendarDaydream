package calendar.daydream.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Instances;

public class CalendarCursorPresenter {

	public static final String[] CURSOR_PROJECTION = new String[] {
			Instances._ID, Instances.EVENT_ID,
			Instances.BEGIN,
			Instances.TITLE,
			Instances.HAS_ALARM,
			Instances.EVENT_LOCATION,
			Instances.DISPLAY_COLOR,
			Instances.END
	};

	private Cursor cursor;
	private Context context;
	
	public CalendarCursorPresenter(Cursor cursor, Context context) {
		this.cursor = cursor;
		this.context = context;
	}

	public int getColor() {
		return getInt(Instances.DISPLAY_COLOR);
	}
	
	public String getTitle() {
		return getString(Instances.TITLE);
	}

	public String getLocation() {
		return getString(Instances.EVENT_LOCATION);
	}

	public int getEventId() {
		return getInt(Instances.EVENT_ID);
	}
	
	public String getDate() {
		DateFormat formatter = android.text.format.DateFormat.getLongDateFormat(context);

		return formatter.format(getCalendar(Instances.BEGIN).getTime());
	}

	public String getTime() {
		if(isNow()) {
			return "Now";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(getCalendar(Instances.BEGIN).getTime());
	}

	private String getString(String key) {
		int column = cursor.getColumnIndex(key);
		return cursor.getString(column);
	}

	private int getInt(String key) {
		int column = cursor.getColumnIndex(key);
		return cursor.getInt(column);
	}
	
	private boolean getBoolean(String key) {
		return getInt(key) == 1;
	}
	
	private Calendar getCalendar(String key) {
		int column = cursor.getColumnIndex(key);
		long value = cursor.getLong(column);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(value);

		return calendar;
	}

	public boolean isNotToday() {
		Calendar tomorrow = Calendar.getInstance();
		tomorrow.set(Calendar.HOUR_OF_DAY, 0);
		tomorrow.set(Calendar.MINUTE, 0);
		tomorrow.set(Calendar.SECOND, 0);
		tomorrow.set(Calendar.MILLISECOND, 0);
		tomorrow.add(Calendar.DATE, 1);

		Calendar beginTime = getCalendar(Instances.BEGIN);
		
		return beginTime.after(tomorrow);
	}

	public boolean isNow() {
		Calendar beginTime = getCalendar(Instances.BEGIN);
		
		return beginTime.before(Calendar.getInstance());		
	}
	
	public String getDuration() {
		long durationMs = getCalendar(Instances.END).getTimeInMillis() - getCalendar(Instances.BEGIN).getTimeInMillis();
		
		long durationMin = (durationMs / (60 * 1000));
		
		if(durationMin < 80) {
			return durationMin + " minutes";
		}
		
		long durationHours = (durationMin / 60);
		if(durationHours < 24) {
			return durationHours + " hours";
		}
		
		return durationHours / 24 + " days";
	}
	
	public boolean isSoon() {
//		if(getBoolean(Instances.HAS_ALARM)) {
//			ContentResolver cr = context.getContentResolver();
//			//cr.
//			//TODO look in remninder table
//			return true;
//		}
		return false;
	}
}
