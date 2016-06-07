package us.gpop.aid;

import android.content.Context;
import android.graphics.Typeface;

public class CustomFont {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    private final Context context;
    private static CustomFont instance;

    /** CLASS FUNCTIONALITY ____________________________________________________________________ **/

    // CusFont(): Constructor for the CustomFont class.
    private CustomFont(Context context) {
        this.context = context;
    }

    // getInstance(): Creates an instance of the CustomFont class.
    public static CustomFont getInstance(Context context) {
        synchronized (CustomFont.class) {
            if (instance == null)
                instance = new CustomFont(context);
            return instance;
        }
    }

    // getTypeFace(): Retrieves the custom font family from resources.
    public Typeface getTypeFace() {
        return Typeface.createFromAsset(context.getResources().getAssets(),
                "fonts/consolas.ttf");
    }
}
