# Informe del laboratorio N°3 de Paradigmas de Programación 2024

### Integrantes del grupo T++ (g42)
Tomás Achával, Tomás Maraschio, Tomás Peyronel

## Índice

- [Introducción](#introducción)
- [Lab 3](#laboratorio-3)
    - [Qué es Spark](#qué-es-spark)
    - [Cómo probar el proyecto](#cómo-probar-el-proyecto)
    - [Cómo usamos Spark](#tareas-distribuidas)
    - [Complicaciones](#complicaciones)
- [Lab 2 vs Lab 3](#lab-2-vs-lab-3)
- [Conclusiones](#conclusiones)

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

Ahora para **compilar y correr el proyecto** usando spark se puede ejecutar el siguiente comando desde el directorio del laboratorio:

```mvn install && $SPARK_HOME/bin/spark-submit --master local[2] target/App-1.0.jar -ne ID```

El ```2``` en ```--master local[2]``` indica la cantidad de hilos "trabajadores" que se desean utilizar.

### Tareas distribuidas

Para hacer uso del poder de spark, distribuimos la extracción de entidades nombradas entre los trabajadores que estén disponibles.

Esto requirió crear un "big data", es decir, un archivo de texto grande el cual se puede subdividir y repartir entre los distintos trabajadores.
Luego cada uno de ellos podrá realizar el cómputo de entidades nombradas sobre su porción del archivo.

Al correr el proyecto, se escriben en el archivo **"big data"** los títulos y descripciones de todos los articulos que se encuentren en los feeds especificados por el usuario.

Luego funciona de la siguente manera:

- El **master** de spark se encarga de la distribución del archivo entre los trabajadores.
- Cada **trabajador** extrae las entidades nombradas de la porción del archivo que recibió y devuelve sus resultados al **master**.
- El **master** realiza la unificación de los resultados, imprimiendo en la consola los resultados obtenidos.

### Complicaciones

## Lab 2 vs Lab 3

## Conclusiones