package me.myocr.ocr.utils;

public class CalculateIOU {
    /**
     * Get (top,left,w,h) coordinate
     *
     * @param rect1,rect2
     * @return iou
     */
    public static float getIOU(int []rect1, int []rect2) {
        int leftColumnMax = Math.max(rect1[1], rect2[1]);
        int rightColumnMin = Math.min(rect1[1] + rect1[2], rect2[1] + rect2[2]);
        int upRowMax = Math.max(rect1[0], rect2[0]);
        int downRowMin = Math.min(rect1[0] + rect1[3], rect2[0] + rect2[3]);

        if (leftColumnMax >= rightColumnMin || downRowMin <= upRowMax) {
            return 0;
        }

        int s1 = rect1[2] * rect1[3];
        int s2 = rect2[2] * rect2[3];
        float sCross = (downRowMin - upRowMax) * (rightColumnMin - leftColumnMax);
        return sCross / (s1 + s2 - sCross);
    }
}
