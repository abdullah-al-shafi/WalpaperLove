package sbf.shafi.walpaperlove;

import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * Created by Black Shadow on 10/6/2017.
 */

public class GIFWallpaperService extends WallpaperService {


    @Override
    public Engine onCreateEngine() {
        try {
            Movie movie = Movie.decodeStream(getResources().getAssets().open("love.gif"));
            return new GIFWallpaperEngin(movie);
        } catch (IOException e){
            Log.d("GIF","Could not load asset");
            return null;
        }
    }

    private class GIFWallpaperEngin extends Engine{

        private final int frameDuration = 20;

        private SurfaceHolder holder;
        private Movie movie;
        private Boolean visible;
        private Handler handler;

        public GIFWallpaperEngin(Movie movie){
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;
        }

        private Runnable drawGIF = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };

        private void draw(){
            if (visible){
                Canvas canvas = holder.lockCanvas();
                canvas.save();
                //adjust size and position
                canvas.scale(3f,3.5f);
                movie.draw(canvas, 0,0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int)(System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            this.visible = visible;
            if (visible){
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }
}
