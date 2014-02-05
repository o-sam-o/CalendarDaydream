package calendar.daydream.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Instances;

public class CalendarCursorPresenter {

	public static final String[] CURSOR_PROJECTION = new String[] {
		CalendarContract.Instances._ID, 
		CalendarContract.Instances.EVENT_ID,
		CalendarContract.Instances.BEGIN,
		CalendarContract.Instances.TITLE,
		CalendarContract.Instances.HAS_ALARM,
		CalendarContract.Instances.EVENT_LOCATION,
		CalendarContract.Instances.DISPLAY_COLOR,
		CalendarContract.Instances.END
	};
	
	public static final String[] ATTENDEE_CURSOR_PROJECTION = new String[] {
		Attendees.ATTENDEE_NAME,
		Attendees.ATTENDEE_EMAIL,
		Attendees.ATTENDEE_RELATIONSHIP,
		Attendees.ATTENDEE_STATUS
	};
	
	private Cursor cursor;
	private Context context;
	
	public CalendarCursorPresenter(Cursor cursor, Context context) {
		this.cursor = cursor;
		this.context = context;
		//Stash date for cursor position so we can detect when a date changes
		cursor.getExtras().putString("position-date-" + cursor.getPosition(), 
				getDate());
	}

	public boolean isNewDate() {
		String previous = cursor.getExtras().getString("position-date-" + (cursor.getPosition() - 1));
		return !getDate().equals(previous);
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

	public String getDateTime() {
		if(isNow()) {
			return "Now";
		}
		
		return android.text.format.DateFormat.getTimeFormat(context).format(getCalendar(Instances.BEGIN).getTime()) + " " + getDate();
	}
	
	public String getTime() {
		if(isNow()) {
			return "Now";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(getCalendar(Instances.BEGIN).getTime());
	}

	private String getString(String key) {
		return getString(key, cursor);
	}

	private String getString(String key, Cursor cursor) {
		int column = cursor.getColumnIndex(key);
		return cursor.getString(column);
	}
	
	private int getInt(String key) {
		return getInt(key, cursor);
	}
	
	private int getInt(String key, Cursor cursor) {
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
	
	//TODO fix plurals
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
	
	public List<CalendarAttendee> getAttendees() {
		List<CalendarAttendee> attendees = new ArrayList<CalendarAttendee>();
		Cursor cursor = CalendarContract.Attendees.query(context.getContentResolver(), getEventId(), ATTENDEE_CURSOR_PROJECTION);
		while(cursor.moveToNext()) {
			attendees.add(new CalendarAttendee(
					getString(Attendees.ATTENDEE_NAME, cursor),
					getString(Attendees.ATTENDEE_EMAIL, cursor),
					getInt(Attendees.ATTENDEE_RELATIONSHIP, cursor),
					getInt(Attendees.ATTENDEE_STATUS, cursor)
				));
		}
		cursor.close();
		
		Collections.sort(attendees);
		
		return attendees;
	}
	
	public boolean isSoon() {
		if(!isNow() && getBoolean(Instances.HAS_ALARM)) {
			Cursor cursor = CalendarContract.Reminders.query(context.getContentResolver(), getEventId(), new String[] { CalendarContract.Reminders.MINUTES });
			if(cursor.moveToFirst()) {
				int reminderMin = getInt(CalendarContract.Reminders.MINUTES, cursor);
				Calendar remindTime = Calendar.getInstance();
				remindTime.add(Calendar.MINUTE, -reminderMin);
				
				return remindTime.after(Calendar.getInstance());
			}
			cursor.close();
		}
		return false;
	}
}
