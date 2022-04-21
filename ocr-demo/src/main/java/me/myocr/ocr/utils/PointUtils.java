package me.myocr.ocr.utils;


import me.myocr.ocr.model.Point;

import java.util.List;


public class PointUtils {

    /**
     * Get (x1,y1,w,h) coordinations
     *
     * @param points
     * @return
     */
    public static int[] rectXYWH(List<Point> points) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : points) {
            int x = point.getX();
            int y = point.getY();
            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
        }

        int w = maxX - minX;
        int h = maxY - minY;
        return new int[]{minX, minY, w, h};  // 与openCV坐标系不同，需要转换
    }

    /**
     * Get (top,left,w,h) coordinations
     *
     * @param points
     * @return
     */
    public static int[] rectTOPLEFTWH(List<Point> points) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Point point : points) {
            int x = point.getX();
            int y = point.getY();
            if (x < minX)
                minX = x;
            if (x > maxX)
                maxX = x;
            if (y < minY)
                minY = y;
            if (y > maxY)
                maxY = y;
        }

        int w = maxX - minX;
        int h = maxY - minY;
        return new int[]{minY, minX, w, h};  // 与openCV坐标系不同，需要转换
    }

}
