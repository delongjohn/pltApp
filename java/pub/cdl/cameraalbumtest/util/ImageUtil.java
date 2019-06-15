package pub.cdl.cameraalbumtest.util;

import android.graphics.Bitmap;

/**
 * 2 * @Author: cdlfg
 * 3 * @Date: 2019/5/3 5:08
 * 4
 */
public class ImageUtil {
    public static Bitmap handlerBitmap(Bitmap bm) {
        Bitmap bitmap;
        int width = bm.getWidth();
        int height = bm.getHeight();
        bitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = bitmap.getPixel(i, j);
                int alpha = pixel & 0xFF000000;
                int red = (pixel & 0x00FF0000) >> 16;
                int green = (pixel & 0x0000FF00) >> 8;
                int blue = pixel & 0x000000FF;
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                int newPiexl = alpha | (gray << 16) | (gray << 8) | gray;
                bitmap.setPixel(i, j, newPiexl);
            }
        }
        return bitmap;
    }
}
