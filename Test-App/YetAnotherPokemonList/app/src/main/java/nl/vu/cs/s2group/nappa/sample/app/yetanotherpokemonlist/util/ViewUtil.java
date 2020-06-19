package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;

public class ViewUtil {
    private ViewUtil() {
        throw new IllegalStateException("ViewUtil is an utility class and should be instantiated!");
    }

    public static TextView createTextView(Context context, String text) {
        return createTextView(context, text, 1.0f);
    }

    public static TextView createTextView(Context context, String text, float weight) {
        TextView textView = new TextView(context, null, 0, R.style.TextViewItem);
        textView.setText(text);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                weight));

        return textView;
    }
}
