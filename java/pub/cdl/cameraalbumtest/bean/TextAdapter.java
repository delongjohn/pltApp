package pub.cdl.cameraalbumtest.bean;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import pub.cdl.cameraalbumtest.R;

import java.util.List;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/2 14:44
 * 4
 */
public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {
    private List<Text> textList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;

        public ViewHolder(View view) {
            super(view);
            time = view.findViewById(R.id.time);
        }
    }

    public TextAdapter(List<Text> s) {
        textList = s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Text text = textList.get(i);
        viewHolder.time.setText(text.getTime());
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }
}
