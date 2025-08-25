package imageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


class imageToByteArray
{
    final  static int GRAYSCALE = 0;
    final  static int RGB = 1;
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

    public static int[][] convertToGrayscale(int[][] pixelArray)
    {
        int[][] grayscaleArray = new int[pixelArray.length][pixelArray[0].length];
        for(int i = 0;i < pixelArray.length;i++)
        {
            for(int j=0;j<pixelArray[0].length;j++)
            {

                // 3          2          1          0
                // bitpos    10987654 32109876 54321098 76543210
                // ------   +--------+--------+--------+--------+
                // bits     |AAAAAAAA|RRRRRRRR|GGGGGGGG|BBBBBBBB|
                
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

        int[][] pixelArray = getPixelArray("sample.png");

        if(pixelArray != null)
        {
            int[][] grayscaleArray = convertToGrayscale(pixelArray);
            byteArayToImage(grayscaleArray, "grayscale.png", GRAYSCALE);
        }
    }
}
