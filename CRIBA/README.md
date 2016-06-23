# Criba de Eratóstenes
La Criba de Eratóstenes es un algoritmo que permite encontrar los numeros primos menores que un numero natural n. A continuación se presentan dos implementaciones del mismo, uno de los cuales utiliza OpenMPI para paralelizar.

## ¿Como compilar?
Dentro de cada una de las carpetas se encuentra un Makefile, con lo cual lo unico necesario para compilar el programa es utilizar el comando:
          `make`

## ¿Como ejecutar?
Criba:
    `mpirun Criba`

Criba_MPI:
  `mpirun -np <# procesos> Criba_MPI`

*<# procesos> : Es el numero de procesos que se desean crear a la hora de correr el programa.
