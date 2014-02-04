package calendar.daydream.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import calendar.daydream.R;
import calendar.daydream.data.CalendarAttendee;

public class AttendeeAdapter extends BaseAdapter {

	private List<CalendarAttendee> attendees;
	private Context context;
	
	public AttendeeAdapter(List<CalendarAttendee> attendees, Context context) {
		super();
		this.attendees = attendees;
		this.context = context;
	}

	@Override
	public int getCount() {
		return attendees.size();
	}

	@Override
	public Object getItem(int index) {
		return attendees.get(index);
	}

	@Override
	public long getItemId(int index) {
		return attendees.get(index).getName().hashCode();
	}

	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		if(convertView == null) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.calendar_item_attendee, parent, false);
		}
		
		CalendarAttendee attendee = attendees.get(index);
		((TextView) convertView.findViewById(R.id.attendee_name)).setText(attendee.getName());

		ImageView icon = (ImageView) convertView.findViewById(R.id.attendee_icon);
		
		switch(attendee.getType()) {
		case ATTENDING:
			icon.setImageResource(R.drawable.check_alt);
			break;
		case NOT_ATTENDING:
			icon.setImageResource(R.drawable.x_alt);
			break;
		case ORGANIZER:
			icon.setImageResource(R.drawable.steering_wheel);
			break;
		default:
			icon.setImageResource(R.drawable.question_mark);
		}
		
		return convertView;
	}

}
