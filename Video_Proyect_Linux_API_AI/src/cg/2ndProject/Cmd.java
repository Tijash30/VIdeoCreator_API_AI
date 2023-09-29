package pruebas;

import java.io.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//exiftool          Graficas/proyecto2$ exiftool imgs/deboris.jpg

public class Cmd {

    public static List<String> abertor(String dir){

        /*
         * This function will only open the folder inputed at the start of the program (in the main)
         * then it will read the info with the command dir, and finally write those file names in a list,
         * that then will be passed to the main, as the most important list
         * it will only add to the list the files with the extension:
         * jpg, png, mov, heic and mp4
         * */

        List<String> lista = new ArrayList<>();
        List<String> nomArchivos = new ArrayList<>();{
            lista.add("png");
            lista.add("jpg");
            //lista.add("heic");
            lista.add("mp4");
            //lista.add("mov");
        }

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("dir");
        processBuilder.directory(new File(dir+"/"+ pruebas.Main.imgsFolder));

        try {
            Process process = processBuilder.start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;

            while((line = reader.readLine()) != null){
                String[] files= line.split("\\s+");
                if(files.length!=0){
                    for(int i=0; i<files.length; i++){
                        //System.out.println(files[i]);
                        String[] extensiones= files[i].split("[.]");
                        for (String s: lista) {
                            if (extensiones[extensiones.length - 1].equals(s)) {
                                nomArchivos.add(extensiones[0]+"."+extensiones[extensiones.length-1]);
                                break;
                            }else if(extensiones[extensiones.length - 1].equals("heic") || extensiones[extensiones.length - 1].equals("mov")){
                                renameFileExtension(files[i], extensiones[0], extensiones[extensiones.length-1]);
                                //nomArchivos.add(extensiones[0]+".jpg");
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return nomArchivos;
    }
    public static List<String> checadorFechas(List<String> archivos, String dir) throws ParseException{

        /*
         * this is my favorite function, first i recive the files names list and get all of the create date of }
         * each image, this will return a string, so i have to parse that to a date format with the class Date
         * then it will create another list of Dates and..
         * */

        int cont=0, continterior, indexnum=0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

        List<String> archivosfinal= new ArrayList<>(archivos);

        String defaultdate= "1023:04:12 19:10:18";

        List<Date> fechas = new ArrayList<>();
        for(String s: archivos){
            String sDate1 = comunicacionNueva(pruebas.Main.imgsFolder+"/"+s, dir, 2);
            if(!sDate1.equals("")) {
                fechas.add(format.parse(sDate1));
            }else{
                fechas.add(format.parse(defaultdate));
            }
        }

        /*
         * Then, when i have my list of dates i check each and every one of them with this data structure
         * and will reorganize the list with a but sorted by the oldest image to the newest.
         * it will return another list of the file names but now all sorted correctly.
         * */

        //System.out.println("\n");
        for(String s: archivos){
            continterior=0;
            indexnum=0;
            if(cont<archivos.size()) {
                for(Date d: fechas){
                    if(continterior<fechas.size()){
                        Date fecha2= fechas.get(cont);
                        if(fecha2.after(d)){
                            indexnum++;
                        }
                    }
                    continterior++;
                }
                archivosfinal.set(indexnum, s);
            }
            cont++;
        }
        return archivosfinal;

    }
    public static  String comunicacionNueva(String archivo, String dir, int indicador){

        /*
         * This is one of the most important functions of my code, in here i get all the information i require of each image and video
         * i use exiftool to get:
         * Filename,  CreateDate, GPSLongitude, and GPSLatitude
         * i will only recieve what i ask for, so it will return inmediatly the information i request.
         * */

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(dir));

        switch (indicador) {
            /*
             * here i have the switch where the user decided which information he eants
             * */
            case 1 -> processBuilder.command("sh", "-c", "exiftool -FileName " + archivo);
            case 2 -> processBuilder.command("sh", "-c", "exiftool -CreateDate "+ archivo);
            case 3 -> processBuilder.command("sh", "-c", "exiftool -GPSLongitude "+ archivo);
            case 4 -> processBuilder.command("sh", "-c", "exiftool -GPSLatitude "+ archivo);
        }
        try {
            Process process = processBuilder.start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line= reader.readLine();
            if(line!=null) {
                String[] parts = line.split(":", 2);
                return parts[parts.length - 1];
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }
    public static void renameFileExtension(String file, String name, String extension) {
        /*
         * This is just a simple funciton where i change the extension if the folder has a tipe of file with the extension
         * heic or mov, because this two ffmpeg do not like to work with
         * so i will only rename the file with an extension that ffmpeg allows.
         * */
        Path sourceFile = Paths.get(pruebas.Main.dir+"/"+pruebas.Main.imgsFolder+"/"+file);
        try {
            if(extension.equals("heic"))
                Files.move(sourceFile, sourceFile.resolveSibling(name+".jpg"));
            else if(extension.equals("mov"))
                Files.move(sourceFile, sourceFile.resolveSibling(name+".mp4"));
        }catch (IOException e){
            System.out.println(e);
        }

    }

}
