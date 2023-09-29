README
Tijash Salamanca

Tomando en cuenta que el código se ejecutará en el sistema operativo de linux:
	1. Mis comandos utilizan dos tres herramientas: 
		a. exiftool
		b. ffmpeg
		c. curl 
	   Cada una de estas deberían de estar instaladas como variables de entorno en linux,
	   Se pueden instalar facilmente poniendo el siguiente comando desde la terminal de linux:
		-sudo apt-get update
		-sudo apt-get install (y aquí pones cada una de las herramientas: "exiftool", "ffmpeg", "curl")

	2. ya que se tienen instaladas estas aplicaciones, como variables de entorno, el código de java podrá ser ejecutado 
	   de manera correcta desde la clase Main, que está en: /proyecto2/src/cg/2ndProject
	
	3. Antes de correr el código debes de asegurarte de estar en el path correcto, en la clase Main, al inicio, verás dos
	   variables tipo String una de nombre "dir" y la otra "imgsfolder", a estas les tendrás que cambiar de valor a:
		a. dir:
			"dir" es la variable del path de donde se encuantra la carpeta del archivo, la carpeta se llama proyecto2
			entonces, abres esta carpeta desde la terminal de linux y pones el comando: "pwd", y te debe aparecer la 
			entera del directorio en donde te encuentras, esta ruta la copias y la pegas como valor de la variable dir 
			asegurándote que quede así: 
				String dir= "ruta/del/proyecto";
		b. imgsfolder:
			"imgsfolder" es nada más el nombre de la carpeta en donde quieres hacer la pruebas del código (donde están
			las imágenes y videos, esta carpeta	debe estar adentro de la carpeta general del proyecto:	
				/proyecto2/imgsfolder	
			entonces solo se debe cambiar la variable imgsfolder a:
				String imgsfolder= "nombreDeCarpetaDePruebas";
	4. Cyuando estos pasos estén completados, entonces es seguro correr el programa.


