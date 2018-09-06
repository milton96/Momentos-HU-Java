package Momentos;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Milton
 */
public class Funciones {
    private BufferedImage imagenCopy;
    int x,y;
    
    public BufferedImage AbrirImagen(){
        BufferedImage imagen = null;
        JFileChooser selector = new JFileChooser();
        FileNameExtensionFilter formatos = new FileNameExtensionFilter("JPG & PNG & BMP", "jpg", "png", "bmp");
        selector.setDialogTitle("Selecciona una imagen");
        selector.setFileFilter(formatos);
        int flag = selector.showOpenDialog(null);
        if(flag==JFileChooser.APPROVE_OPTION){
            try {
                File imagenSelect = selector.getSelectedFile();
                imagen = ImageIO.read(imagenSelect);
            } catch(IOException e){
                JOptionPane.showMessageDialog(null, e, "Error al cargar la imagen", JOptionPane.WARNING_MESSAGE);
            }
        }
        imagenCopy = imagen;
        x=imagenCopy.getWidth(); //ancho de la imagen
        y=imagenCopy.getHeight(); //alto de la imagen
        return imagen;
    }
    
    public String[] MomentosHU(){
        String[] resultados = new String[19]; //vector de striing donde guarda los resultados obtenidos
        int matriz[][] = new int [x][y]; //matriz de enteros del mismo tamaño que la imagen leida
        int color = 0;
        //buscamos los pixeles blancos y negros, y donde encontremos uno blanco guardaremos en una matriz un 0,
        //en caso contrario guardamos un 1
        for(int i=0; i<imagenCopy.getWidth(); i++){
            for(int j=0; j<imagenCopy.getHeight(); j++){
                if((color=imagenCopy.getRGB(i, j))==-1){
                    matriz[i][j] = 0;
                }else{
                    matriz[i][j] = 1;
                }
            }
        }   
        //calcula area y centro de masa
        float area=0; //es el momento central u00
        float Cx=0, Cy=0; //centro de masa en X e Y
        float m10=0; //este momento central vale 0
        float m01=0; //este momento central vale 0
        //recorremos la matriz de 1's y 0's para calcular el area, y los momentos geometricos m10 y m01
        for(int y=0; y<imagenCopy.getHeight(); y++){
            for(int x=0; x<imagenCopy.getWidth(); x++){
                if(matriz[x][y]==1){
                    area++;
                    m10+=x;
                    m01+=y;
                }
            }
        }
        //calculamos el centro de masa, en caso de ser fraccion se redonde hacia arriba
        Cx=Math.round(m10/area);
        Cy=Math.round(m01/area);
        
        //guardamos el area, el centro de masa y los momentos m10 y m02 en nuestro arreglo de resultados
        resultados[0] = Float.toString(area);
        resultados[1] = Float.toString(Cx);
        resultados[2] = Float.toString(Cy);
        resultados[3] = Float.toString(0);
        resultados[4] = Float.toString(0);

        //Calculamos lo momentos centrales
        double u11=0;
        double u20=0, u02=0, u21=0, u12=0;
        double u30=0, u03=0;
        for(int y=0; y<imagenCopy.getHeight(); y++){
            for(int x=0; x<imagenCopy.getWidth(); x++){
                if(matriz[x][y]==1){
                    //aplicamos la formula para calcular los momentos centrales
                    u11 += ((x-Cx)*(y-Cy));
                    u20 += (Math.pow((x-Cx), 2));
                    u02 += (Math.pow((y-Cy), 2));
                    u21 += (Math.pow((x-Cx), 2)*(y-Cy));
                    u12 += ((x-Cx)*Math.pow((y-Cy), 2));
                    u30 += (Math.pow((x-Cx), 3));
                    u03 += (Math.pow((y-Cy), 3));
                }
            }
        }
        
        //Normalizamos los momentos centrales
        double n11 = Normalizar(u11,1,1,area);
        double n20 = Normalizar(u20,2,0,area);
        double n02 = Normalizar(u02,0,2,area);
        double n21 = Normalizar(u21,2,1,area);
        double n12 = Normalizar(u12,1,2,area);
        double n30 = Normalizar(u30,3,0,area);
        double n03 = Normalizar(u03,0,3,area);
        
        //guardamos los momentos centales normalizados en nuestro arreglo de resultados
        resultados[5] = Double.toString(n11);
        resultados[6] = Double.toString(n02);
        resultados[7] = Double.toString(n20);
        resultados[8] = Double.toString(n12);
        resultados[9] = Double.toString(n21);
        resultados[10] = Double.toString(n03);
        resultados[11] = Double.toString(n30);
        
        //Calculamos los Momentos de HU usando las formulas correspondientes
        double h1 = n20+n02;
        double h2 = Math.pow((n20-n02), 2) + (4*Math.pow(n11, 2));
        double h3 = Math.pow((n30-(3*n12)), 2) + Math.pow(((3*n21)-n03), 2);
        double h4 = Math.pow((n30+n12), 2) + Math.pow((n21+n03), 2);
        double h5 = (n30-(3*n12))*(n30+n12)*(Math.pow((n30+n12), 2)-(3*Math.pow((n21+n03), 2))) + ((3*n21)-n03)*(n21+n03)*(3*(Math.pow((n30+n12),2)) - Math.pow((n21+n03), 2));
        double h6 = (n20-n02)*((Math.pow((n30+n12), 2))-Math.pow((n21+n03), 2))+(4*n11*(n30+n12)*(n21+n03));
        double h7 = ((3*n21)-n03)*(n30+n12)*(Math.pow((n30+n12), 2)-(3*Math.pow((n21+n03), 2))) + ((3*n12)-n30)*(n21+n03)*(3*Math.pow((n30+n12),2) - Math.pow((n21+n03), 2));        
        
        //guardamos los momentos de hu en nuestro arreglo de resultados
        resultados[12] = Double.toString(h1);
        resultados[13] = Double.toString(h2);
        resultados[14] = Double.toString(h3);
        resultados[15] = Double.toString(h4);
        resultados[16] = Double.toString(h5);
        resultados[17] = Double.toString(h6);
        resultados[18] = Double.toString(h7);
        
        return resultados;
    }
    
    //funcion apra normalizar los momentos centrales
    public double Normalizar(double upq, int p, int q, float area){
        //double r = ((p+q)/2)+1;
        double npq;
        if((p+q)==3){
            npq = (upq/(Math.pow(area, 2.5)));
        }else{
            npq = (upq/(Math.pow(area, 2)));
        }
        return npq;
    }
    
    //las siguientes funciones sirven para binarizar la imagen en caso de ser necesario
    public BufferedImage Binarizar(int umbral){
        Color color;
        BufferedImage imagen = new BufferedImage(imagenCopy.getWidth(), imagenCopy.getHeight(), imagenCopy.getType());
        for(int i=0; i<imagenCopy.getWidth(); i++){
            for(int j=0; j<imagenCopy.getHeight(); j++){
                color = new Color(imagenCopy.getRGB(i, j));
                imagen.setRGB(i, j, RGB(color,umbral));
            }
        }
        imagenCopy=imagen;
        return imagen;
    }
    
    private int RGB(Color color, int umbral){
        int media = (int) ((color.getRed()+color.getGreen()+color.getBlue())/3);
        if(media>=umbral)
            color = new Color(255,255,255,color.getAlpha());
        else
            color = new Color(0,0,0,color.getAlpha());
        int RGB=0;
        RGB = ((color.getRed()<<16) | (color.getGreen()<<8) | (color.getBlue()));
        return RGB;
    }
    
}
