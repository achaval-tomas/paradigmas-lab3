Para compilar y correr el proyecto usando spark se puede usar:

```mvn install && $SPARK_HOME/bin/spark-submit --master local[2] target/App-1.0.jar -ne ID```

En $SPARK_HOME debería estar el path al directorio de spark. El 2 en ```--master local[2]``` indica la cantidad de hilos workers.
