package pub.cdl.cameraalbumtest.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import pub.cdl.cameraalbumtest.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/2 16:21
 * 4
 */
public class DateText extends ArrayAdapter<Text> {
    private int resourceId;

    public DateText(Context context, int resourceId, List<Text> obj) {
        super(context, resourceId, obj);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Text text = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView time = view.findViewById(R.id.time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String i = sdf.format(new Date(Long.valueOf(text.getTime())));
        time.setText(i);
        return view;
    }
}
