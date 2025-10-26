import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


class prewitt
{
    final  static int GRAYSCALE = 0;
    final  static int RGB = 1;

    public static int[][] getPaddedArray(int[][] arr,int n)
    {
        int[][] paddedArray = new int[arr.length + 2*n][arr[0].length + 2*n];


        for(int i=0;i<paddedArray.length;i++)
        {
            for(int j =0;j<paddedArray[0].length;j++)
            {
                if(i == 0 | j == 0 | i == paddedArray.length -1 | j == paddedArray[0].length - 1)
                {
                    paddedArray[i][j] = 0;
                }
                else
                {
                    paddedArray[i][j] = arr[i-1][j-1];
                }
            }
        }
        return paddedArray;
    }


    public static int[][] applyPrewittFilter(int[][] arr) {
        int width = arr[0].length;
        int height = arr.length;
        int[][] out = new int[height][width];

        // Prewitt kernels
        int[][] Gx = {
            { -1, 0, 1 },
            { -1, 0, 1 },
            { -1, 0, 1 }
        };

        int[][] Gy = {
            { -1, -1, -1 },
            {  0,  0,  0 },
            {  1,  1,  1 }
        };

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = arr[y + i][x + j];
                        gx += pixel * Gx[i + 1][j + 1];
                        gy += pixel * Gy[i + 1][j + 1];
                    }
                }

                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                if (magnitude > 255) magnitude = 255;
                out[y][x] = magnitude;
            }
        }

        return out;
    }

    public static int[][] applyNormalizedPrewittFilter(int[][] arr) {
        int width = arr[0].length;
        int height = arr.length;
        int[][] out = new int[height][width];

        int[][] Gx = {
            { -1, 0, 1 },
            { -1, 0, 1 },
            { -1, 0, 1 }
        };

        int[][] Gy = {
            { -1, -1, -1 },
            {  0,  0,  0 },
            {  1,  1,  1 }
        };

        int maxGradient = 0;

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int pixel = arr[y + i][x + j];
                        gx += pixel * Gx[i + 1][j + 1];
                        gy += pixel * Gy[i + 1][j + 1];
                    }
                }

                int magnitude = (int) Math.sqrt(gx * gx + gy * gy);
                out[y][x] = magnitude;
                if (magnitude > maxGradient) maxGradient = magnitude;
            }
        }

        if (maxGradient == 0) return out; // avoid divide by zero

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                out[y][x] = (int) (((double) out[y][x] / maxGradient) * 255);
            }
        }

        return out;
    }

    public static void printArray(int[][] arr)
    {
        for(int i =0;i<arr.length;i++)
        {
            for(int j = 0;j<arr[0].length;j++)
            {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static Boolean byteArayToImage(int[][] pixelArray,String outPath,int imageType)
    {
        if(pixelArray == null)
        {
            return false;
        }
        int height = pixelArray.length;
        int width = pixelArray[0].length;
        BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);

        for(int i = 0;i < height;i++)
        {
            for(int j=0;j<width;j++)
            {
                int pixel = pixelArray[i][j];
                if(imageType == RGB)
                    image.setRGB(j, i, pixel);
                else if(imageType == GRAYSCALE)
                {
                    pixel = (pixel << 16) | (pixel << 8) | pixel;
                    image.setRGB(j, i, pixel);
                }

            }
        }

        try{
            ImageIO.write(image, "jpg", new File(outPath));
            return true;
        }
        catch(IOException e)
        {
            System.out.println(e.getStackTrace());
        }
        return false;

    }

    public static int[][] getPixelArray(String path)
    {
        try{

            File inputFile = new File(path);
            BufferedImage image = ImageIO.read(inputFile);

            int width = image.getWidth();
            int height = image.getHeight();

            int[][] pixelArray= new int[height][width];

            for(int i = 0;i < height;i++)
            {
                for(int j=0;j<width;j++)
                {
                    int pixel = image.getRGB(j,i);
                    pixelArray[i][j] = pixel;
                }
            }

            return pixelArray;

            }catch(IOException e)
            {
                System.out.println(e.getStackTrace());
            }

            return null;
    }

    public static int[][] convertToGrayscale(int[][] pixelArray)
    {
        int[][] grayscaleArray = new int[pixelArray.length][pixelArray[0].length];
        for(int i = 0;i < pixelArray.length;i++)
        {
            for(int j=0;j<pixelArray[0].length;j++)
            {

                int pixel = pixelArray[i][j];
                int blue = pixel & 0xff;
                int green = (pixel & 0xff00) >> 8;
                int red = (pixel & 0xff0000) >> 16;
                grayscaleArray[i][j] = (int)(0.299*red + 0.587*green + 0.114*blue);
            }
        }
        return grayscaleArray;
    }

    public static void main(String[] args) {

       int[][] b = getPixelArray("E:\\convolution\\edgeDetection\\a.jpg");
       int[][] bpa = getPaddedArray(b, 1);
       int[][] bpre = applyPrewittFilter(bpa);
       byteArayToImage(bpre, "prewitt_out.jpg", GRAYSCALE);

    }
}
