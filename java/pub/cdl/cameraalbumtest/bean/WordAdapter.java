package pub.cdl.cameraalbumtest.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import pub.cdl.cameraalbumtest.R;

import java.util.List;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/4/25 19:36
 * 4
 */
public class WordAdapter extends ArrayAdapter<Word> {
    private int resourceId;

    public WordAdapter(Context context, int textViewId, List<Word> words) {
        super(context, textViewId, words);
        resourceId = textViewId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Word word = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView id = view.findViewById(R.id.word_id);
        TextView tr = view.findViewById(R.id.word_tr);
        String count = "(" + word.getCount() + ")";
        String text = (word.getCount() == 1 ? "" : count) +
                word.getWord();

        String translate = word.getTranslate();
        id.setText(text);
        tr.setText(translate);
        return view;
    }
}
