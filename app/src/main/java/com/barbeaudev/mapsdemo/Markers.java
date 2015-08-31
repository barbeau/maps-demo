package com.barbeaudev.mapsdemo;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * Created by Sean on 8/31/2015.
 */
public class Markers {

    public static final String NORTH = "N";

    public static final String NORTH_WEST = "NW";

    public static final String WEST = "W";

    public static final String SOUTH_WEST = "SW";

    public static final String SOUTH = "S";

    public static final String SOUTH_EAST = "SE";

    public static final String EAST = "E";

    public static final String NORTH_EAST = "NE";

    public static final String NO_DIRECTION = "null";

    public static final int NUM_DIRECTIONS = 9; // 8 directions + undirected mStops

    private static BitmapDescriptor[] bus_stop_icons = new BitmapDescriptor[NUM_DIRECTIONS];

    private static int mPx; // Bus stop icon size

    // Bus icon arrow attributes - by default assume we're not going to add a direction arrow
    private static float mArrowWidthPx = 0;

    private static float mArrowHeightPx = 0;

    private static float mBuffer = 0;  // Add this to the icon size to get the Bitmap size

    private static float mPercentOffset = 0.5f;
    // % offset to position the stop icon, so the selection marker hits the middle of the circle

    private static Paint mArrowPaintStroke;
    // Stroke color used for outline of directional arrows on stops

    /**
     * Returns the BitMapDescriptor for a particular bus stop icon, based on the stop direction
     *
     * @param direction Bus stop direction, obtained from ObaStop.getDirection() and defined in
     *                  constants in this class
     * @return BitmapDescriptor for the bus stop icon that should be used for that direction
     */
    public static BitmapDescriptor getBitmapDescriptorForBusStopDirection(String direction) {
        if (direction.equals(NORTH)) {
            return bus_stop_icons[0];
        } else if (direction.equals(NORTH_WEST)) {
            return bus_stop_icons[1];
        } else if (direction.equals(WEST)) {
            return bus_stop_icons[2];
        } else if (direction.equals(SOUTH_WEST)) {
            return bus_stop_icons[3];
        } else if (direction.equals(SOUTH)) {
            return bus_stop_icons[4];
        } else if (direction.equals(SOUTH_EAST)) {
            return bus_stop_icons[5];
        } else if (direction.equals(EAST)) {
            return bus_stop_icons[6];
        } else if (direction.equals(NORTH_EAST)) {
            return bus_stop_icons[7];
        } else if (direction.equals(NO_DIRECTION)) {
            return bus_stop_icons[8];
        } else {
            return bus_stop_icons[8];
        }
    }

    /**
     * Cache the BitmapDescriptors that hold the images used for icons
     */
    public static final void loadIcons(Context context) {
        // Initialize variables used for all marker icons
        Resources r = context.getResources();
        mPx = r.getDimensionPixelSize(R.dimen.map_stop_shadow_size_6);
        mArrowWidthPx = mPx / 2f; // half the stop icon size
        mArrowHeightPx = mPx / 3f; // 1/3 the stop icon size
        float arrowSpacingReductionPx = mPx / 10f;
        mBuffer = mArrowHeightPx - arrowSpacingReductionPx;

        // Set offset used to position the image for markers (see getX/YPercentOffsetForDirection())
        // This allows the current selection marker to land on the middle of the stop marker circle
        mPercentOffset = (mBuffer / (mPx + mBuffer)) * 0.5f;

        mArrowPaintStroke = new Paint();
        mArrowPaintStroke.setColor(Color.WHITE);
        mArrowPaintStroke.setStyle(Paint.Style.STROKE);
        mArrowPaintStroke.setStrokeWidth(1.0f);
        mArrowPaintStroke.setAntiAlias(true);

        bus_stop_icons[0] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, NORTH));
        bus_stop_icons[1] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, NORTH_WEST));
        bus_stop_icons[2] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, WEST));
        bus_stop_icons[3] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, SOUTH_WEST));
        bus_stop_icons[4] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, SOUTH));
        bus_stop_icons[5] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, SOUTH_EAST));
        bus_stop_icons[6] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, EAST));
        bus_stop_icons[7] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, NORTH_EAST));
        bus_stop_icons[8] = BitmapDescriptorFactory.fromBitmap(createBusStopIcon(context, NO_DIRECTION));
    }

    private static Bitmap createBusStopIcon(Context context, String direction) {
        Resources r = context.getResources();

        Float directionAngle = null;  // 0-360 degrees
        Bitmap bm;
        Canvas c;
        Drawable shape;
        Float rotationX = null, rotationY = null;  // Point around which to rotate the arrow

        Paint arrowPaintFill = new Paint();
        arrowPaintFill.setStyle(Paint.Style.FILL);
        arrowPaintFill.setAntiAlias(true);

        if (direction.equals(NO_DIRECTION)) {
            // Don't draw the arrow
            bm = Bitmap.createBitmap(mPx, mPx, Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds(0, 0, bm.getWidth(), bm.getHeight());
        } else if (direction.equals(NORTH)) {
            directionAngle = 0f;
            bm = Bitmap.createBitmap(mPx, (int) (mPx + mBuffer), Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds(0, (int) mBuffer, mPx, bm.getHeight());
            // Shade with darkest color at tip of arrow
            arrowPaintFill.setShader(
                    new LinearGradient(bm.getWidth() / 2, 0, bm.getWidth() / 2, mArrowHeightPx,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            // For NORTH, no rotation occurs - use center of image anyway so we have some value
            rotationX = bm.getWidth() / 2f;
            rotationY = bm.getHeight() / 2f;
        } else if (direction.equals(NORTH_WEST)) {
            directionAngle = 315f;  // Arrow is drawn N, rotate 315 degrees
            bm = Bitmap.createBitmap((int) (mPx + mBuffer),
                    (int) (mPx + mBuffer), Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds((int) mBuffer, (int) mBuffer, bm.getWidth(), bm.getHeight());
            // Shade with darkest color at tip of arrow
            arrowPaintFill.setShader(
                    new LinearGradient(0, 0, mBuffer, mBuffer,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            // Rotate around below coordinates (trial and error)
            rotationX = mPx / 2f + mBuffer / 2f;
            rotationY = bm.getHeight() / 2f - mBuffer / 2f;
        } else if (direction.equals(WEST)) {
            directionAngle = 0f;  // Arrow is drawn pointing West, so no rotation
            bm = Bitmap.createBitmap((int) (mPx + mBuffer), mPx, Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds((int) mBuffer, 0, bm.getWidth(), bm.getHeight());
            arrowPaintFill.setShader(
                    new LinearGradient(0, bm.getHeight() / 2, mArrowHeightPx, bm.getHeight() / 2,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            // For WEST
            rotationX = bm.getHeight() / 2f;
            rotationY = bm.getHeight() / 2f;
        } else if (direction.equals(SOUTH_WEST)) {
            directionAngle = 225f;  // Arrow is drawn N, rotate 225 degrees
            bm = Bitmap.createBitmap((int) (mPx + mBuffer),
                    (int) (mPx + mBuffer), Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds((int) mBuffer, 0, bm.getWidth(), mPx);
            arrowPaintFill.setShader(
                    new LinearGradient(0, bm.getHeight(), mBuffer, bm.getHeight() - mBuffer,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            // Rotate around below coordinates (trial and error)
            rotationX = bm.getWidth() / 2f - mBuffer / 4f;
            rotationY = mPx / 2f + mBuffer / 4f;
        } else if (direction.equals(SOUTH)) {
            directionAngle = 180f;  // Arrow is drawn N, rotate 180 degrees
            bm = Bitmap.createBitmap(mPx, (int) (mPx + mBuffer), Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds(0, 0, bm.getWidth(), (int) (bm.getHeight() - mBuffer));
            arrowPaintFill.setShader(
                    new LinearGradient(bm.getWidth() / 2, bm.getHeight(), bm.getWidth() / 2,
                            bm.getHeight() - mArrowHeightPx,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            rotationX = bm.getWidth() / 2f;
            rotationY = bm.getHeight() / 2f;
        } else if (direction.equals(SOUTH_EAST)) {
            directionAngle = 135f;  // Arrow is drawn N, rotate 135 degrees
            bm = Bitmap.createBitmap((int) (mPx + mBuffer),
                    (int) (mPx + mBuffer), Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds(0, 0, mPx, mPx);
            arrowPaintFill.setShader(
                    new LinearGradient(bm.getWidth(), bm.getHeight(), bm.getWidth() - mBuffer,
                            bm.getHeight() - mBuffer,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            // Rotate around below coordinates (trial and error)
            rotationX = (mPx + mBuffer / 2) / 2f;
            rotationY = bm.getHeight() / 2f;
        } else if (direction.equals(EAST)) {
            directionAngle = 180f;  // Arrow is drawn pointing West, so rotate 180
            bm = Bitmap.createBitmap((int) (mPx + mBuffer), mPx, Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds(0, 0, mPx, bm.getHeight());
            arrowPaintFill.setShader(
                    new LinearGradient(bm.getWidth(), bm.getHeight() / 2,
                            bm.getWidth() - mArrowHeightPx, bm.getHeight() / 2,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            rotationX = bm.getWidth() / 2f;
            rotationY = bm.getHeight() / 2f;
        } else if (direction.equals(NORTH_EAST)) {
            directionAngle = 45f;  // Arrow is drawn pointing N, so rotate 45 degrees
            bm = Bitmap.createBitmap((int) (mPx + mBuffer),
                    (int) (mPx + mBuffer), Bitmap.Config.ARGB_8888);
            c = new Canvas(bm);
            shape = r.getDrawable(R.drawable.map_stop_icon);
            shape.setBounds(0, (int) mBuffer, mPx, bm.getHeight());
            // Shade with darkest color at tip of arrow
            arrowPaintFill.setShader(
                    new LinearGradient(bm.getWidth(), 0, bm.getWidth() - mBuffer, mBuffer,
                            r.getColor(R.color.theme_primary), r.getColor(R.color.theme_accent),
                            Shader.TileMode.MIRROR));
            // Rotate around middle of circle
            rotationX = (float) mPx / 2;
            rotationY = bm.getHeight() - (float) mPx / 2;
        } else {
            throw new IllegalArgumentException(direction);
        }

        shape.draw(c);

        if (direction.equals(NO_DIRECTION)) {
            // Everything after this point is for drawing the arrow image, so return the bitmap as-is for no arrow
            return bm;
        }

        /**
         * Draw the arrow - all dimensions should be relative to px so the arrow is drawn the same
         * size for all orientations
         */
        // Height of the cutout in the bottom of the triangle that makes it an arrow (0=triangle)
        final float CUTOUT_HEIGHT = mPx / 12;
        Path path = new Path();
        float x1 = 0, y1 = 0;  // Tip of arrow
        float x2 = 0, y2 = 0;  // lower left
        float x3 = 0, y3 = 0; // cutout in arrow bottom
        float x4 = 0, y4 = 0; // lower right

        if (direction.equals(NORTH) || direction.equals(SOUTH) ||
                direction.equals(NORTH_EAST) || direction.equals(SOUTH_EAST) ||
                direction.equals(NORTH_WEST) || direction.equals(SOUTH_WEST)) {
            // Arrow is drawn pointing NORTH
            // Tip of arrow
            x1 = mPx / 2;
            y1 = 0;

            // lower left
            x2 = (mPx / 2) - (mArrowWidthPx / 2);
            y2 = mArrowHeightPx;

            // cutout in arrow bottom
            x3 = mPx / 2;
            y3 = mArrowHeightPx - CUTOUT_HEIGHT;

            // lower right
            x4 = (mPx / 2) + (mArrowWidthPx / 2);
            y4 = mArrowHeightPx;
        } else if (direction.equals(EAST) || direction.equals(WEST)) {
            // Arrow is drawn pointing WEST
            // Tip of arrow
            x1 = 0;
            y1 = mPx / 2;

            // lower left
            x2 = mArrowHeightPx;
            y2 = (mPx / 2) - (mArrowWidthPx / 2);

            // cutout in arrow bottom
            x3 = mArrowHeightPx - CUTOUT_HEIGHT;
            y3 = mPx / 2;

            // lower right
            x4 = mArrowHeightPx;
            y4 = (mPx / 2) + (mArrowWidthPx / 2);
        }

        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.lineTo(x4, y4);
        path.lineTo(x1, y1);
        path.close();

        // Rotate arrow around (rotationX, rotationY) point
        Matrix matrix = new Matrix();
        matrix.postRotate(directionAngle, rotationX, rotationY);
        path.transform(matrix);

        c.drawPath(path, arrowPaintFill);
        c.drawPath(path, mArrowPaintStroke);

        return bm;
    }
}
