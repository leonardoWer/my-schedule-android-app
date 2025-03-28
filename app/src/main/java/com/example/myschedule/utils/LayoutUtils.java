package com.example.myschedule.utils;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

public class LayoutUtils {

    public static void setMargins(Context context, View item, int ml, int mt, int mr, int mb) {
        // Создаём новые параметры
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Переводим отступы в dp
        int mlDp = getMarginInDp(context, ml);
        int mtDp = getMarginInDp(context, mt);
        int mrDp = getMarginInDp(context, mr);
        int mbDp = getMarginInDp(context, mb);

        // Устанавливаем параметры
        params.setMargins(mlDp, mtDp, mrDp, mbDp);
        item.setLayoutParams(params);
    }

    public static int getMarginInDp(Context context, int m) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, m, context.getResources().getDisplayMetrics());
    }
}
