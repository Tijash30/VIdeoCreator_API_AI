package pruebas;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    /*
    * dir and imgsFolder are just 2 Strings that give the program the path to execute the terminal commands
    * */
    public static String dir= "/home/tijashi/Escritorio/graficas/proyecto2";
    public static String imgsFolder= "imgs";
    public static Boolean haymapa;

    public static void main(String[] args) throws ParseException {

        /*
        * This is the class Main, i have the main where every function compiles, here all my 3 other classes also are used, not every function in the classes
        * compile here, but almost all of them.
        * The order in which my code works is:
        *
        * 1.    makedir()                       makes a new directory where all my new images with text and new videos will be seved
        * 2.    ComAPIs.getFirstAIImage()       Is the function in which i ask the terminal to give me a new AI generated image from OpenAI
        * 3.    Cmd.abertor()                   This function returns a list with all the files accepted in the directory where you have your test images and videos
        * 4.    Cmd.checadorFechas()            gives the same list as above but sorted by dates, I created my own data structure, very interesting.
        * 5.    ComAPIs.readClimate()           Thsi function will return another list with the climates of each place in where the photos where taken, also sorted the same way as the other list
        * 6.    sacarPosIniFinal()              here yo give the list of hte files, and will return al list of 4 doubles, the first 2 of the latitude and longitude of the first image in the list and the last 2 the latitude and longitude of the last image
        *
        * 7.    If the coordinates of the first and last image exists, the it will compile:
        *   8.  ComAPis.reproducirMensajeI)nspirador()  This function will only return a string with a message created by AI about something relationated with the first and last place visited
        *   9.  ComAPIs.getFinalMap()           in here, is hust a call to an API that gives you an image with an especific position you give. The image will be download to the directory that the program created
        *
        * 10.   CreadorVideo.escritorTexto()    this function will write on every image on the lsit the climate especified in the other list where i had the climates, and save this new files in the directory i created
        * 11.   CreadorVideo.creaMiniVideos()   If the file is an image it will need to be create a video of 3 seconds of the image, i use ffmpeg to create a video.
        * 12.   CreadorVideo.creaVideoFinal()   and finally the last function will concatenate the videos created, and output the final video on the proyect directory.
        *
        * in all my program i use a lot of request to the linux terminal, I use the class ProcessBuilder to comunicate with if.
        *
        * */
        String mensajeInspirador="";

        makeDir();
        pruebas.ComAPIs.getFirstAIImage();

        List<String> nomarchivos= pruebas.Cmd.abertor(dir);
        nomarchivos= pruebas.Cmd.checadorFechas(nomarchivos, dir);

        List<String> climas= pruebas.ComAPIs.readClimate(nomarchivos);
        List<Double> cords= sacarPosIniFinal(nomarchivos);


        if(!cords.isEmpty()) {
            mensajeInspirador= pruebas.ComAPIs.reproducirMensajeInspirador(cords);
            pruebas.ComAPIs.getFinalMap(cords, mensajeInspirador);
            nomarchivos.add("firstmapTijashsh3010.jpg");
            System.out.println(mensajeInspirador);
            haymapa=true;
        }
        else {
            System.out.println("no hay coordenadas en la primera o última foto");
            haymapa=false;
        }

        pruebas.CreadorVideo.escritorTexto(climas, nomarchivos);

        /*
        * I have this series of blocks where i put the programm to "sleep", to wait to the folder updates it self so then i can show us the new files.
        * Some functions will need a little more time than others, thats why in some i have only 3 second and in others 30.
        * */

        try{
            Thread.sleep(3000);
        }catch (Exception e) {
            System.out.println(e);
        }

        pruebas.CreadorVideo.creaMiniVideos(nomarchivos);

        int time;
        time= pruebas.CreadorVideo.contvids*33000;
        if(time==0)
            time=13000;

        try{
            //System.out.println(time);
            Thread.sleep(time);
        }catch (Exception e) {
            System.out.println(e);
        }

        pruebas.CreadorVideo.creaVideoFinal();

        try{
            Thread.sleep(time);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    /*
    * This is the funcitohn where i get the coordinates of hte position of the first and last image.
    * */
    public static List<Double> sacarPosIniFinal(List<String> files){
        List<Double> latsLongs= new ArrayList<>();

        String nomarchinii= files.get(0);
        String archfinal= files.get(files.size()-1);
        double latitude=0, longitude=0;

        /*
        * The way this works is just request the name of the file of the first and last image, get their gpsData with exiftool
        * with anotehr function, then parse that srting of latitude and longitude to decimal coordinates, also with another function I created
        * and save thos double values in a list that then will return to the main.
        * */

        String lats= pruebas.Cmd.comunicacionNueva(imgsFolder + "/"+nomarchinii, dir, 4);
        String longs= pruebas.Cmd.comunicacionNueva(imgsFolder + "/"+nomarchinii, dir, 3);
        latitude= pruebas.ComAPIs.parsePosition(lats, 0);
        longitude= pruebas.ComAPIs.parsePosition(longs, 1);
        latsLongs.add(latitude);
        latsLongs.add(longitude);
        if(latitude==0.0){
            latsLongs.clear();
            return latsLongs;
        }

        lats= pruebas.Cmd.comunicacionNueva(imgsFolder + "/"+archfinal, dir, 4);
        longs= pruebas.Cmd.comunicacionNueva(imgsFolder + "/"+archfinal, dir, 3);
        latitude= pruebas.ComAPIs.parsePosition(lats, 0);
        longitude= pruebas.ComAPIs.parsePosition(longs, 1);
        latsLongs.add(latitude);
        latsLongs.add(longitude);
        if(latitude==0.0)
            latsLongs.clear();

        return latsLongs;
    }
    /*
    * makedir() is a pretty simple function, it will only create a new folder named imgvidtxt, where i will sage all the new files i create in the program
    * I use the class ProcessBuilder to comunicate with the linux terminal.
    * */
    public static void makeDir(){
        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.directory(new File(dir+"/"+imgsFolder));
        processBuilder.command("sh", "-c", "mkdir imgvidtxt");
        try {
            processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            Thread.sleep(2000);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
}
