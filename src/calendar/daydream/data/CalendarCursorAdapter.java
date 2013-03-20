package calendar.daydream.data;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import calendar.daydream.R;

public class CalendarCursorAdapter extends CursorAdapter {

	public CalendarCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		CalendarCursorPresenter item = new CalendarCursorPresenter(cursor, context);
		
		TextView titleView = (TextView) view.findViewById(R.id.cal_item_title);
		if (titleView != null) {
			titleView.setText(item.getTitle());
			titleView.setTextColor(item.getColor());
		}
		
		TextView subtitleView = (TextView) view.findViewById(R.id.cal_item_subtitle);
		if (subtitleView != null) {
			subtitleView.setText(item.getDate());
		}

		TextView timeView = (TextView) view.findViewById(R.id.cal_item_time);
		if (timeView != null) {
			timeView.setText(item.getTime());
//			if(item.isSoon()) {
//				timeView.setTextColor(Color.RED);
//			} else if (item.isNotToday()) {
//				timeView.setTextColor(Color.GRAY);
//			}
		}
		
		TextView locationView = (TextView) view.findViewById(R.id.cal_item_summary);
		if (locationView != null) {
			locationView.setText(item.getLocation());
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.calender_item, parent, false);

		bindView(v, context, cursor);
		
		return v;
	}

}