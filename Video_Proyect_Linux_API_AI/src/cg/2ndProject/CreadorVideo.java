package pruebas;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


//cambiar carpeta donde están las imágenes en:
//      abertorCarpeta(hasta el final)
//      clase Cmd en la función checadorFechas, ir a la función comunicaciónNueva(en el primer parametro)
//      en función renameALL(En último parámetro)

//Rename        GRAFICAS\exiftool>exiftool -filename=newpale.jpg D:\AATijash\TIJASH\UNI\3er4to\4to\GRAFICAS\exiftool\pale.jpg
//CreateVideo   \4to\GRAFICAS\proyecto2\ffmpeg\bin>ffmpeg -r 1/3 -i ..\..\imgs\1%4d.jpg -pix_fmt yuv420p ..\..\imgs\vid.mp4
//create txt    (echo file 'vid.mp4' & echo file 'vid2.mp4' & echo file 'out.mp4')>lost.txt
//concat vids   \4to\GRAFICAS\proyecto2\ffmpeg\bin>ffmpeg -f concat -safe 0 -i ..\..\borra\vids\list.txt -c copy ..\..\borra\vids\out.mp4
//copy files    \4to\GRAFICAS\proyecto2>copy out.mp4 D:\AATijash\TIJASH\UNI\3er4to\4to\GRAFICAS\proyecto2\imgs
//change AR     \4to\GRAFICAS\proyecto2\ffmpeg\bin>ffmpeg -i ..\\..\\imgs\\seguns.mp4 -aspect 900:1200 -c copy ..\\..\\imgs\\aaaseguns.mp4
//add text      ffmpeg -i ..\..\imgs\sla.jpg -vf drawtext=text="el valedor":fontsize=200:fontcolor=white ..\..\imgs\elvale.jpg
//get map       wget "https://www.mapquestapi.com/staticmap/v5/map?locations=||20.77,-103.47||20.68,-103.44&size=@2x&key=qrQa0tTEfRmdjn0cPzqzzThRZn9iWNKf"

public class CreadorVideo {

    /*
    * In this class i will do everything (well, almost every process that includes with creating a new video file or add text to some image or video
    * almost all of the funcitons in here comunicates with the linux terminal to work with ffmpeg program
    * */
    public static int contvids=0;
    public static void escritorTexto(List<String> climas, List<String> files){
        /*In here i take every file in the list and write its climate data with ffmpeg

        * */
        ProcessBuilder processBuilder = new ProcessBuilder();

        //add climate text to each image:

        String clima;
        int cont=0;
        try {
            for(String s: files) {
                //para que no cuente la última foto que es la del mapa
                if(pruebas.Main.haymapa) {
                    if (cont < files.size() - 1) {
                        clima = climas.get(cont);
                        processBuilder.directory(new File(pruebas.Main.dir));
                        processBuilder.command("sh", "-c", "ffmpeg -i " + pruebas.Main.imgsFolder + "/" + s + " -vf drawtext=text=\"" + clima + "\":fontsize=200:fontcolor=black " + pruebas.Main.imgsFolder + "/imgvidtxt/" + s);
                        processBuilder.start();
                        //filesNames.add("'vid"+cont+".mp4'");
                        cont++;
                    }
                }else {
                    clima = climas.get(cont);

                    processBuilder.directory(new File(pruebas.Main.dir));
                    processBuilder.command("sh", "-c", "ffmpeg -i " + pruebas.Main.imgsFolder + "/" + s + " -vf drawtext=text=\"" + clima + "\":fontsize=200:fontcolor=black " + pruebas.Main.imgsFolder + "/imgvidtxt/" + s);
                    processBuilder.start();
                    //filesNames.add("'vid"+cont+".mp4'");
                    cont++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    public static void creaMiniVideos(List<String> files){

        /*
        * In this function i have 2 importn process to run, the first one is to create the "minivideos" of each image, so then, every image will have
        * its own video of 3 seconds, so then i can concatenate this new videos with the rest of the videos in the final function in the main
        * all thi snew files will be saved in the new folder i created in the first function in the main.
        * */

        List<String> filesNames= new ArrayList<>();

        //to create the videos for each image
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(pruebas.Main.dir+"/"+ pruebas.Main.imgsFolder+"/imgvidtxt"));
        String extension, nom;
        int cont=0;
        try {
            /*If the file inputed in this for is already a video file it will obvously not be necessary to create a video of it, so it weill only
            * write the name of the file in a list and pass the if*/
            for(String s: files) {
                String[] parts = s.split("[.]", 2);
                extension= parts[parts.length - 1];
                nom = parts[0];
                if(extension.equals("jpg")||extension.equals("png")) {
                    processBuilder.directory(new File(pruebas.Main.dir));
                    processBuilder.command("sh", "-c", "ffmpeg -r 1/3 -i "+ pruebas.Main.imgsFolder + "/imgvidtxt/" + nom + "." + extension + " -pix_fmt yuv420p  "+ pruebas.Main.imgsFolder+"/imgvidtxt/vid" + cont + ".mp4");
                    processBuilder.start();
                    filesNames.add("'vid"+cont+".mp4'");
                }else if (extension.equals("mp4")){
                    filesNames.add("'"+nom+".mp4'");
                    contvids++;
                }
                cont++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //create the txt file with the names of the mini vids
        /*
        * then, the second process is where i create a new txt file where i will write all the name of the videos files in here
        * this will then be used as an input in the command of ffmpeg to concatenate the videos, and mak the final video.
        * */

        try {
            FileWriter myWriter = new FileWriter(pruebas.Main.dir+"/"+ pruebas.Main.imgsFolder+"/imgvidtxt/vidlist.txt");
            myWriter.write("file 'aiTijashsh3010.mp4'\n");
            for(String s: filesNames) {
                myWriter.write("file "+s+"\n");
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void creaVideoFinal(){
        /*
         * This is a pretty simple function, i just have process builder to comunicate with the linux terminal
         * and then put the command of ffmpeg to concatenate all my videos i create, this command will recive as an input the txt file menntioned before
         * */
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.directory(new File(pruebas.Main.dir));
        processBuilder.command("sh", "-c", "ffmpeg -f concat -safe 0 -i "+ pruebas.Main.imgsFolder+"/imgvidtxt/vidlist.txt -c copy aaaout.mp4");
        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}