# Informe del laboratorio N°3 de Paradigmas de Programación 2024

### Integrantes del grupo T++ (g42)
Tomás Achával, Tomás Maraschio, Tomás Peyronel

## Índice

- [Introducción](#introducción)
- [Lab 3](#laboratorio-3)
    - [Qué es Spark](#qué-es-spark)
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

### Tareas distribuidas
Para compilar y correr el proyecto usando spark se puede usar:

```mvn install && $SPARK_HOME/bin/spark-submit --master local[2] target/App-1.0.jar -ne ID```

En $SPARK_HOME debería estar el path al directorio de spark. El 2 en ```--master local[2]``` indica la cantidad de hilos workers que se desean utilizar.
En general, SPARK_HOME es ~/spark-3.5.1-bin-hadoop3.

### Complicaciones

## Lab 2 vs Lab 3

## Conclusiones