package nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.util;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import java.util.List;
import java.util.function.Consumer;

import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.R;
import nl.vu.cs.s2group.nappa.sample.app.yetanotherpokemonlist.apiresource.named.NamedAPIResource;

public class ViewUtil {
    private ViewUtil() {
        throw new IllegalStateException("ViewUtil is an utility class and should be instantiated!");
    }

    public static TextView createTextView(Context context, String text) {
        return createTextView(context, text, 1.0f);
    }

    public static TextView createTextView(Context context, String text, float weight) {
        return createTextView(context, text, weight, R.style.TextViewItem);
    }

    public static TextView createTextView(Context context, String text, float weight, int styleId) {
        TextView textView = new TextView(context, null, 0, styleId);
        textView.setText(text);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                weight));

        return textView;
    }

    public static void addNamedAPIResourceListToUI(AppCompatActivity activity, int viewId, List<?> list, String getterMethod) {
        addNamedAPIResourceListToUI(activity, viewId, list, getterMethod, null);
    }

    public static void addNamedAPIResourceListToUI(AppCompatActivity activity, int viewId, List<?> list, String getterMethod, Consumer<View> callback) {
        List<NamedAPIResource> namedAPIResourceList = PokeAPIUtil.parseListToNamedAPOResourceList(list, getterMethod);
        activity.runOnUiThread(() -> {
            LinearLayoutCompat linearLayout = activity.findViewById(viewId);
            if (namedAPIResourceList.isEmpty()) {
                String emptyListStr = activity.getResources().getString(R.string.empty_list);
                linearLayout.addView(createTextView(activity, emptyListStr));
            } else {
                for (NamedAPIResource namedAPIResource : namedAPIResourceList) {
                    TextView tv = createTextView(activity, namedAPIResource.getName());
                    tv.setTag(namedAPIResource.getUrl());
                    if (callback != null) tv.setOnClickListener(callback::accept);
                    linearLayout.addView(tv);
                }

            }
        });
    }
}
