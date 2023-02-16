package com.geega.bsc.captcha.starter.utils;

import com.geega.bsc.captch.common.vo.PointVO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 滑块工具类
 *
 * @author Jun.An3
 * @date 2021/11/29
 */
public class BlockPuzzleUtils {

    /**
     * 检查滑块的位置信息是否匹配
     *
     * @param point     目标位置1
     * @param point1    目标位置2
     * @param tolerance 误差范围
     * @return true:误差范围内 false:误差范围外
     */
    public static boolean checkPoint(PointVO point, PointVO point1, int tolerance) {
        try {
            if (point.x - tolerance > point1.x
                    || point1.x > point.x + tolerance
                    || point.y != point1.y) {
                return false;
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    /**
     * 随机生成拼图坐标
     *
     * @param originalWidth  原图宽
     * @param originalHeight 原图高
     * @param jigsawWidth    抠图宽
     * @param jigsawHeight   抠图高
     */
    public static PointVO generateJigsawPoint(int originalWidth, int originalHeight, int jigsawWidth, int jigsawHeight) {
        Random random = new Random();
        int widthDifference = originalWidth - jigsawWidth;
        int heightDifference = originalHeight - jigsawHeight;
        int x, y;
        if (widthDifference <= 0) {
            x = 5;
        } else {
            x = random.nextInt(originalWidth - jigsawWidth - 100) + 100;
        }
        if (heightDifference <= 0) {
            y = 5;
        } else {
            y = random.nextInt(originalHeight - jigsawHeight) + 5;
        }
        return new PointVO(x, y, null);
    }

    /**
     * 根据缺块抠图
     *
     * @param oriImage       原图
     * @param jigsawImage    模板图
     * @param newJigsawImage 新抠出的小图
     * @param x              随机扣取坐标X
     * @param y              随机扣取坐标y
     */
    public static void cutByTemplate(BufferedImage oriImage, BufferedImage jigsawImage, BufferedImage newJigsawImage, int x, @SuppressWarnings("SameParameterValue") int y) {
        //临时数组遍历用于高斯模糊存周边像素值
        int[][] matrix = new int[3][3];
        int[] values = new int[9];

        int xLength = jigsawImage.getWidth();
        int yLength = jigsawImage.getHeight();
        // 模板图像宽度
        Boolean[][] boarderMatrix = new Boolean[xLength][yLength];
        for (int xI = 0; xI < xLength; xI++) {
            // 模板图片高度
            for (int yJ = 0; yJ < yLength; yJ++) {
                int rgb = jigsawImage.getRGB(xI, yJ);
                if (rgb < 0) {
                    //如果模板图像当前像素点不是透明色,说明是抠图区域,copy源文件信息到目标图片中
                    newJigsawImage.setRGB(xI, yJ, oriImage.getRGB(x + xI, y + yJ));
                    readPixel(oriImage, x + xI, y + yJ, values);
                    fillMatrix(matrix, values);
                    //抠图区域高斯模糊
                    oriImage.setRGB(x + xI, y + yJ, avgMatrix(matrix));
                    boarderMatrix[xI][yJ] = true;
                } else {
                    boarderMatrix[xI][yJ] = false;
                }
            }
        }

        Color[] colors = getColors();
        Color[] whiteColors = getWhiteColors();
        int length = colors.length;
        //如果离边界的位置小于等于4的点
        for (int xI = 0; xI < xLength; xI++) {
            for (int yJ = 0; yJ < yLength; yJ++) {
                //只有在边界中才会判断
                int colorIndex = 0;
                if (boarderMatrix[xI][yJ]) {
                    boolean isOk = false;
                    for (int i = 0; i < length; i++) {
                        //上
                        boolean isOk1 = yJ + i >= yLength || xI + i >= xLength || !boarderMatrix[xI + i][yJ + i];
                        boolean isOk2 = yJ + i >= yLength || !boarderMatrix[xI][yJ + i];
                        boolean isOk3 = xI + i >= xLength || !boarderMatrix[xI + i][yJ];

                        boolean isOk4 = yJ - i <= 0 || xI - i <= 0 || !boarderMatrix[xI - i][yJ - i];
                        boolean isOk5 = xI - i <= 0 || !boarderMatrix[xI - i][yJ];
                        boolean isOk6 = yJ - i <= 0 || !boarderMatrix[xI][yJ - i];

                        boolean isOk7 = xI - i <= 0 || yJ + i >= yLength || !boarderMatrix[xI - i][yJ + i];
                        boolean isOk8 = yJ - i <= 0 || xI + i >= xLength || !boarderMatrix[xI + i][yJ - i];

                        if (isOk1 || isOk2 || isOk3 || isOk4 || isOk5 || isOk6 || isOk7 || isOk8) {
                            isOk = true;
                            colorIndex = i;
                            break;
                        }
                    }
                    if (isOk) {
                        newJigsawImage.setRGB(xI, yJ, colors[colorIndex].getRGB());
                        oriImage.setRGB(x + xI, y + yJ, whiteColors[colorIndex].getRGB());
                    }
                }
            }
        }
    }

    private static Color[] getColors() {
        Color[] colors = new Color[4];
        colors[0] = new Color(38,178,118);
        colors[1] = new Color(38,178,118);
        colors[2] = new Color(38,178,118);
        colors[3] = new Color(38,178,118);
//        colors[4] = new Color(38,178,118);
//        colors[5] = new Color(38,178,118);
        return colors;
    }

    private static Color[] getWhiteColors() {
        Color[] colors = new Color[4];
//        colors[0] = new Color(155, 155, 155);
//        colors[1] = new Color(175, 175, 175);
        colors[0] = new Color(195, 195, 195);
        colors[1] = new Color(215, 215, 215);
        colors[2] = new Color(235, 235, 235);
        colors[3] = new Color(255, 255, 255);
        return colors;
    }

    private static void readPixel(BufferedImage img, int x, int y, int[] pixels) {
        int xStart = x - 1;
        int yStart = y - 1;
        int current = 0;
        for (int i = xStart; i < 3 + xStart; i++) {
            for (int j = yStart; j < 3 + yStart; j++) {
                int tx = i;
                if (tx < 0) {
                    tx = -tx;

                } else if (tx >= img.getWidth()) {
                    tx = x;
                }
                int ty = j;
                if (ty < 0) {
                    ty = -ty;
                } else if (ty >= img.getHeight()) {
                    ty = y;
                }
                pixels[current++] = img.getRGB(tx, ty);

            }
        }
    }

    private static void fillMatrix(int[][] matrix, int[] values) {
        int filled = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; j++) {
                x[j] = values[filled++];
            }
        }
    }

    private static int avgMatrix(int[][] matrix) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int[] x : matrix) {
            for (int j = 0; j < x.length; j++) {
                if (j == 1) {
                    continue;
                }
                Color c = new Color(x[j]);
                r += c.getRed();
                g += c.getGreen();
                b += c.getBlue();
            }
        }
        return new Color(r / 8, g / 8, b / 8).getRGB();
    }

}
