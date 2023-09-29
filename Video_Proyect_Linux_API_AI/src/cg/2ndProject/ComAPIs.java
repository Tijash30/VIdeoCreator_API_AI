package pruebas;


import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class ComAPIs {
    public static void getFirstAIImage(){
        /*
         * This is the function that will download the ai generated image, i have 3 steps to do it, the 3 of them woks with the linux terminal
         * (processbuilder class)
         * 1. get the url of the image by requesting it to OpenAi servers
         * 1. Once i get it, i separete the url info to the rest of the trasg info that i dont need, by reading the output that the terminal gave me
         * 2. when i have the url as a string, i download the image with the curl command, also with the terminal
         * 3. finally when i have the image downloaded in my folder, i create aa video of that image of 3 seconds, just as the same as the rest of the other images
         * */
        String urlimgfinal="";

        ProcessBuilder processBuilder = new ProcessBuilder();

        processBuilder.command("sh", "-c", "curl https://api.openai.com/v1/images/generations \\\n" +
                "  -H \"Content-Type: application/json\" \\\n" +
                "  -H \"Authorization: Bearer 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\" \\\n" +
                "  -d '{\n" +
                "    \"prompt\": \"pintura de renoir del mar\",\n" +
                "    \"n\": 2,\n" +
                "    \"size\": \"1024x1024\"\n" +
                "  }'");
        processBuilder.directory(new File(pruebas.Main.dir));
        try {
            Process process = processBuilder.start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                if(line.contains("\"url\": \"")){
                    String[] parts = line.split("\"url\": ", 2);
                    urlimgfinal= parts[1];
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        processBuilder.directory(new File(pruebas.Main.dir+"/"+ pruebas.Main.imgsFolder+"/imgvidtxt"));
        processBuilder.command("sh", "-c", "curl -o aiTijashsh3010.jpg "+urlimgfinal);
        try {
            processBuilder.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try{
            Thread.sleep(10000);
        }catch (Exception e) {
            System.out.println(e);
        }

        processBuilder.directory(new File(pruebas.Main.dir+"/"+ pruebas.Main.imgsFolder+"/imgvidtxt"));
        processBuilder.command("sh", "-c", "ffmpeg -r 1/3 -i aiTijashsh3010.jpg -pix_fmt yuv420p aiTijashsh3010.mp4");
        try {
            processBuilder.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            Thread.sleep(15000);
        }catch (Exception e) {
            System.out.println(e);
        }
    }
    public static String reproducirMensajeInspirador(List<Double> cords){
        /*
         * in here i have almost the same steps as the function where i get the aiimage, just:
         * 1. i get the location of the first and last image with its coordinates with the API of openweather
         * 2. once i get the location of both images i put that as a request to openaqi, so it can generate me a new original message, that i save in a String
         * 3. and finally i read the message from the terminal, i also separete the real message forom all the trash information taht i dont need.
         * */

        ProcessBuilder processBuilder = new ProcessBuilder();
        int i=0;
        String lat, longi;
        List<String> ciudades= new ArrayList<>();

        while(i<4){
            lat= ""+cords.get(i);
            i++;
            longi= ""+cords.get(i);

            processBuilder.command("sh", "-c", "curl \"https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + longi + "&appid=5f2e1912f22b053dfb5f5828d328dec3&units=metric\"");
            processBuilder.directory(new File(pruebas.Main.dir));

            try {
                Process process = processBuilder.start();

                InputStream stream = process.getInputStream();
                InputStreamReader streamReader = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(streamReader);

                String line;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\"name\":\"", 2);
                    String[] finalname = parts[parts.length - 1].split("\"");
                    ciudades.add(finalname[0]);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }

        processBuilder.command("sh", "-c", "curl https://api.openai.com/v1/chat/completions   -H \"Content-Type: application/json\"   -H \"Authorization: Bearer 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\"   -d '{\n" +
                "    \"model\": \"gpt-3.5-turbo\",\n" +
                "    \"messages\": [{\"role\": \"user\", \"content\": \"Un mensaje de 15 palabras despidiendo "+ciudades.get(0)+" y llegando a " +ciudades.get(1)+" sin emojis\"}]\n" +
                "  }'");
        processBuilder.directory(new File(pruebas.Main.dir));
        try {
            Process process = processBuilder.start();

            InputStream stream = process.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line=reader.readLine();
            String[] parts = line.split("\"content\":\"", 2);
            String[] finalmessage = parts[parts.length - 1].split("\"},\"");
            return finalmessage[0];

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static List<String> readClimate(List<String> files){

        /*
         *This function will work with the openweather API, i input the coordinates of every image and video in mi list of file
         * and then get the information of climates with a json file that is return in the terminal, so i also have to separte the information there
         * all the images that do have GPS data will get their own climate message, if they dont have gps data, then it will write No data
         * a list of string will be returned, with the information that will be inserted in each image and video.
         * */

        List<String> climalista= new ArrayList<>();

        ProcessBuilder processBuilder = new ProcessBuilder();
        double latitude, longitude;
        for(String s: files) {
            latitude=0; longitude=0;
            String lat = pruebas.Cmd.comunicacionNueva(pruebas.Main.imgsFolder + "/" + s, pruebas.Main.dir, 4);
            String lon = pruebas.Cmd.comunicacionNueva(pruebas.Main.imgsFolder + "/" + s, pruebas.Main.dir, 3);

            if (!lat.equals("")&&!lon.equals("")){
                latitude = parsePosition(lat, 1);
                longitude = parsePosition(lon, 0);
            }
            //System.out.println(latitude+"  "+longitude);
            if(latitude!=0&&longitude!=0) {
                processBuilder.command("sh", "-c", "curl \"https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=5f2e1912f22b053dfb5f5828d328dec3&units=metric\"");
                processBuilder.directory(new File(pruebas.Main.dir));

                try {
                    Process process = processBuilder.start();

                    InputStream stream = process.getInputStream();
                    InputStreamReader streamReader = new InputStreamReader(stream);
                    BufferedReader reader = new BufferedReader(streamReader);

                    String line;

                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split("\"temp\":", 2);
                        String[] fineltemp = parts[parts.length - 1].split(",");
                        climalista.add(fineltemp[0] + " C");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }else{
                climalista.add("No data");
            }
        }
        return climalista;
    }
    public static double parsePosition(String latlon, int indicador){
        /*
         * This function is pretty useful because the info proportionated by exiftool of the gps data is returned as a String,
         * but needed as integers, or coordinates,l so this function will recieve a String, and then it will separete
         * the numbers and finally transform all of that numbers to real coordinates, wich will be returned as a double valua.
         * */
        String[] parts= latlon.split("\\s");
        if(parts.length>1) {
            int grados = Integer.parseInt(parts[1]);
            String[] parts2 = parts[3].split("'");
            double minutos = 0.0166666 * Integer.parseInt(parts2[0]);
            String[] parts3 = parts[4].split("\"");
            double segundos = 0.00027777 * Double.parseDouble(parts3[0]);
            if (parts[5].equals("S") || parts[5].equals("W"))
                return -1 * (minutos + grados + segundos);
            return minutos + grados + segundos;
        }
        return 0;
    }
    public static void getFinalMap(List<Double> cords, String finalmessage){
        /*
         * this functuion will request a map from mapquest API, i just have to pass the parameters of latitudde and longitude to teh url and it will returnn
         * a }n image with two markers, the first one, in the psition where i took the first image in the list, and the second ,ark will b e in the position o
         * where i took the last image of the list
         * */
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(pruebas.Main.dir+"/"+ pruebas.Main.imgsFolder+"/imgvidtxt"));

        String lat1= ""+cords.get(0), long1=""+cords.get(1);
        String lat2= ""+cords.get(2), long2=""+cords.get(3);


        processBuilder.command("sh", "-c", "curl -o firstmapTijashsh3010.jpg "+"\"https://www.mapquestapi.com/staticmap/v5/map?key=qrQa0tTEfRmdjn0cPzqzzThRZn9iWNKf&size=600,400@2x&locations=||"+lat1+","+long1+"||"+lat2+","+long2+"&defaultMarker=marker-num\"");

        try {
            processBuilder.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*
         * I here i tried to put the message generated by openAI into this final image but i decidied to comment this part
         * becuase sometimes i works and others do not
         * so i went to the safe way.
         * */
        /*try{
            Thread.sleep(8000);
        }catch (Exception e) {
            System.out.println(e);
        }

        processBuilder.directory(new File(pruebas.Main.dir));
        processBuilder.command("sh", "-c", "ffmpeg -i " + pruebas.Main.imgsFolder + "/imgvidtxt/firstmapTijashsh3010.jpg -vf drawtext=text='"+ finalmessage+"':fontsize=25:fontcolor=black " + pruebas.Main.imgsFolder + "/imgvidtxt/mapTijashsh3010.jpg");
        //System.out.println(finalmessage);
        //String command_= "ffmpeg -i " + pruebas.Main.imgsFolder + "/imgvidtxt/firstmapTijashsh3010.jpg -vf drawtext=text=\"" + finalmessage + "\":fontsize=50:fontcolor=black " + pruebas.Main.imgsFolder + "/imgvidtxt/mapTijashsh3010.jpg";
        //processBuilder.command("sh", "-c", "ffmpeg", "-i", pruebas.Main.imgsFolder + "/imgvidtxt/firstmapTijashsh3010.jpg", "-vf", "drawtext=text='"+ finalmessage+"':fontsize=30", pruebas.Main.imgsFolder + "/imgvidtxt/mapTijashsh3010.jpg");
        try {
            processBuilder.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try{
            Thread.sleep(10000);
        }catch (Exception e) {
            System.out.println(e);
        }*/
    }
}