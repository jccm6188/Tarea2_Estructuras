#include <math.h>
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#define num 48612
int main(int argc, char **argv) {
    
    int Rnum = 220, i, j, rank, p, Size, Resid, Low, High;
    int Lista_A[Rnum + 1]; 
    int Lista_B[num - Rnum + 1]; 
    double runtime;


    MPI_Init(&argc, &argv);

    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &p);
    runtime = -MPI_Wtime();

  // EL algoritmo implementado consta en dividir el vector de 48612 numeros en 2 vectores
  // Uno de tamaño Rnum = (int)sqrt(num) el cual es controlado por el proceso 0 y otro de tamaño
  // num - Rum +1 el cual sera dividido entre la cantidad de procesos restantes en partes iguales.
  // Cada uno de los procesos realizará la criba de Eratostenes a su parte del vector y la devolvera al
  // proceso 0 para que imprima los números.
  //---------------------------------------------------------------------

    Size = (num-(Rnum+1)) / p;
    Resid = (num-(Rnum+1)) % p;
    Low = Rnum + rank * Size + 1;
    High = Low+Size-1;

    if(rank == p-1) {
        High = High + Resid;
    }

    for(i = 2; i <= Rnum; i++) {
        Lista_A[i] = 0;
    }
    
    for(i = Low; i <= High; i++) {
        Lista_B[i-Low] = 0;
    }

//Implementación de la Criba
//-----------------------------------------------------------------------
    for(i = 2; i <= Rnum; i++) {
        if(Lista_A[i] == 0) {
            for(j = i+1; j <= Rnum; j++) {
               if(j%i == 0) {
                   Lista_A[j] = 1;
                }
            }

             for(j = Low; j <= High; j++)
            {
                if(j%i == 0)
                {
                    Lista_B[j-Low] = 1;
                }
            }
        }
    }

// Tareas especificas para cada proceso
//-----------------------------------------------------------------------
// 1 - Los procesos distintos del proceso 0 envian su parte de Lista_B a proceso 0
//-----------------------------------------------------------------------

     if(rank != 0) {
  
        MPI_Send(Lista_B, High-Low+1, MPI_INT, 0, 0, MPI_COMM_WORLD);


//-----------------------------------------------------------------------
// 2 - El proceso 0 se encarga de recibir las partes de Lista_B de cada uno de los procesos
//     e imprimir los numeros que no se hayan marcado.
//-----------------------------------------------------------------------
    } else { 

// Se imprimen los números a los que tenia acceso proceso 0
        for(i = 2; i <= Rnum; i++) {
            if(Lista_A[i] == 0) {
                printf("%d ", i);
            }
        }
        for(i = Low; i <= High; i++) {
            if(Lista_B[i-Low] == 0) {
                printf("%d ", i);
            }
        }

// Se prepara a proceso 0 para recibir la información de los demás procesos
        for(rank = 1; rank <= p-1; rank++) {
            Low = Rnum + rank*Size + 1;
            High = Low+Size-1;
            if(rank == p-1) {
                High += Resid;
            }
//Recepción de los datos de los otros procesos
            MPI_Recv(Lista_B, High-Low+1, MPI_INT, rank, 0, MPI_COMM_WORLD,MPI_STATUS_IGNORE);

//Se imprimen los datos faltantes.
            for(i = Low; i <= High; i++) {
                if(Lista_B[i-Low] == 0) {
                    printf("%d ", i);
                }
            }
        }
        printf("\n");
	runtime += MPI_Wtime(); //Toma el tiempo que llevo implementar la Criba 
        printf ("Tiempo de Ejecución: %f s\n", runtime);
    }

    MPI_Finalize();

    return 0;
}
