package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

public class ViewUtil {
    private ViewUtil() {
        throw new IllegalStateException("ViewUtil is an utility class and should be instantiated!");
    }

    public static TextView createTextViewFromNamedAPIResource(Context context, NamedAPIResource namedAPIResource) {
        TextView textView = new TextView(context, null, 0, R.style.TextViewItem);
        textView.setText(namedAPIResource.getName());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return textView;
    }
}
