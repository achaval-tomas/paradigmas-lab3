# Informe del laboratorio N°3 de Paradigmas de Programación 2024

### Integrantes del grupo T++ (g42)
Tomás Achával, Tomás Maraschio, Tomás Peyronel

## Índice

- [Introducción](#introducción)
- [Lab 3](#laboratorio-3)
    - [Qué es Spark](#qué-es-spark)
    - [Cómo probar el proyecto](#cómo-probar-el-proyecto)
    - [Cómo usamos Spark](#tareas-distribuidas)
    - [Complicaciones](#algunas-complicaciones)
- [Lab 2 vs Lab 3](#lab-2-vs-lab-3)
- [Posibles Mejoras](#posibles-mejoras)

## Introducción

Este informe contiene un resumen de nuestra experiencia utilizando el framework y la [API para Java](https://spark.apache.org/docs/latest/api/java/index.html) de [Spark](https://spark.apache.org/docs/latest/index.html) en el laboratorio número 3 de la materia Paradigmas de Programación.
Analizaremos los resultados obtenidos al distribuir el cómputo de entidades nombradas y realizaremos comparaciones con los resultados obtenidos en el laboratorio 2.

## Laboratorio 3

### Qué es Spark

Spark es un framework de computación distribuida muy utilizado para trabajar con bases de datos a gran escala.

Cuando se trabaja con spark, una máquina (o un proceso) **"master" divide los datos** y los **distribuye** hacia **distintos "trabajadores"** (otras máquinas o procesos).
Luego, los trabajadores realizan el cómputo sobre sus porciones de los datos y envían los resultados al "master", quien se encarga de unificarlos y devolver una única respuesta, como si todo el trabajo hubiera sido realizado por una sola computadora o proceso.

### Cómo probar el proyecto

Primero se deben instalar las dependencias necesarias:

- [Spark 3.5.1](https://spark.apache.org/downloads.html)
- [Maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

Al descargar spark, se debe descomprimir en un directorio ```<dir>``` y luego ejecutar ```export SPARK_HOME=<dir>/<nombre-spark-descomprimido>```.
En general, ```<nombre-spark-descomprimido> = spark-3.5.1-bin-hadoop3```.

Para **compilar el proyecto** se debe ejecutar ```mvn install``` desde la carpeta del laboratorio.

Para **correr el proyecto** montamos y utilizamos un cluster de spark a través del siguiente procedimiento:

1. Iniciar el proceso master, corriendo ```$SPARK_HOME/bin/spark-class org.apache.spark.deploy.master.Master```
   Esto nos dará una dirección de la forma **spark://ip:puerto** en donde "corre" el master.
2. Correr los siguientes comandos en **N terminales distintas**, donde **N** es la **cantidad de workers** deseados:<br>
    - ```export SPARK_WORKER_CORES=1``` (o el equivalente en su sistema operativo).
    - ```$SPARK_HOME/bin/spark-class org.apache.spark.deploy.worker.Worker spark://<ip>:<puerto>```, donde **ip** y **puerto** son los valores obtenidos en el paso 1.
3. Habiendo compilado el proyecto, correr en otra terminal (desde el directorio del laboratorio): ```$SPARK_HOME/bin/spark-submit --master spark://<ip>:<puerto> target/App-1.0.jar -ne NID```, pudiendo reemplazar "-ne NID" con los argumentos que se deseen.

### Tareas distribuidas

Para hacer uso del poder de spark, distribuimos la extracción de entidades nombradas entre los trabajadores que estén disponibles.
Esto requirió crear un "big data", es decir, un archivo de texto grande el cual se puede subdividir y repartir entre los distintos trabajadores.
Luego cada uno de ellos podrá realizar el cómputo de entidades nombradas sobre su porción del archivo.

Al correr el proyecto, se escriben en el archivo **"big data"** los títulos y descripciones de todos los artículos que se encuentren en los feeds especificados por el usuario.
Luego funciona de la siguente manera:

1. El **master** de spark se encarga de la **distribución del archivo** entre los trabajadores. 
2. Cada **trabajador** extrae las entidades nombradas de la porción del archivo que recibió y devuelve sus resultados al **master**. 
3. El **master** unifica los resultados parciales de los trabajadores y luego imprime en consola los resultados finales del programa.

### Algunas complicaciones

- Instalar Spark en Windows fue bastante complicado.
- Tuvimos que modificar el código en una parte que interactuaba con la librería json, pues nos tiraba error al correrlo. 
  El código en cuestión utilizaba un ciclo "foreach" para acceder a los elementos de un JSONArray.  
- Al principio nos costó entender cómo correr el código usando spark y la dinámica de crear trabajadores y asignarles recursos. 
  Por ejemplo, tuvimos que darnos cuenta de que un worker solo debe utilizar 1 hilo, ya que si no cada worker usa todos los hilos del sistema y los benchmarks comparando la cantidad de workers no tendrían sentido.
- Tuvimos que resolver un bug causado por la serialización y deserialización de objetos cuando son mandados a los trabajadores.

## Lab 2 vs Lab 3

Para comparar el desempeño de los laboratorios 2 y 3, extrajimos entidades nombradas de un **archivo de 1.56GB**.

En el **laboratorio 2** obtuvimos este resultado:<br>
```
Computed named entities in 105931ms (105.9s)
```

Para las pruebas del laboratorio 3 se utilizó una computadora con 4 núcleos y 8 hilos (por hyper-threading) y se le otorgó un hilo a cada trabajador.

Al correrlo utilizando **1 trabajador** de spark, el resultado fue el siguiente:<br>
```
Computed named entities in 161110ms (161.1s)
```

Utilizando **2 trabajadores** obtuvimos:<br>
```
Computed named entities in 104071ms (104.1s)
```
 
Con **4 trabajadores**, el resultado fue **el mejor**:<br>
```
Computed named entities in 76583ms (76.6s)
```

Finalmente, utilizando **8 trabajadores**, se obtuvo:<br>
```
Computed named entities in 85158ms (85.2s)
```

La primera observación sobre los resultados obtenidos es que utilizar **un solo trabajador** de spark con **un solo núcleo** es **notablemente peor** que no utilizar spark (lab2) pues esto introduce una **gran sobrecarga** sin hacer uso del poder de distribución de cómputo de spark.

También se puede ver que la mejor opción fue distribuir el trabajo entre 4 trabajadores. 
El declive de rendimiento del caso de 8 trabajadores creemos que se explica con una mezcla de dos factores:

- **Hyper-threading**: al usar los 8 hilos no se consigue el doble de rendimiento que al utilizar 4, pues ahora entra en juego el hyper-threading, que mejora solo ligeramente el rendimiento multi-núcleo.
- **Overhead de comunicación**: al usar el doble de workers, el overhead de comunicación master-worker es el doble que con 4.

Luego el empeoramiento vendría de que el overhead adicional es mayor que el rendimiento que se gana por usar más workers.

Por otro lado, al correr el proyecto sobre los cuatro feeds originales (muy pocos datos), spark tiene tanto overhead que el código del lab2 era mucho más rápido.

## Posibles Mejoras

- Paralelizar también el fetch de feeds de la red, el cual ahora es responsabilidad total del master.
- Paralelizar el cómputo (parcial) de estadísticas, y después combinar estos resultados.
- Quizás configurando mejor spark se pueden obtener mejores resultados.
