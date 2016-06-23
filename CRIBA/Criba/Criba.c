#include <math.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#define num 48612

int main(int argc, char **argv) {
    
    int i, j; 
    int Lista_A[num]; 
    int Lista_B[num];
    double t_ini, t_fin, runtime;

    t_ini = clock();

    //Llena el vector Lista_A con 0, lo que significa que los campos estan no marcados
    for(i = 2; i <= num; i++) {
        Lista_A[i] = 0;
    }

    //Llena el vector Lista_B con numeros para luego imprimirlos
    for(i = 0; i <= num; i++) {
        Lista_B[i] = i;
    } 
   // Criba de Eratóstenes
    for(i = 2; i*i <= num; i++) // Para i=2 hasta i^2 = num  
    {
        if(Lista_A[i]==0) {     //Revisa si el número esta marcado
            for(j = 2; i * j <= num; j++){  // Si no esta marcado, busca sus multiplos
                Lista_A[i*j] = 1;           // y los marca
	    }
        }
     }
   // Impresión de los números primos 
        for(i = 2; i <= num; i++) {
            if(Lista_A[i] == 0) {
                printf("%d ", Lista_B[i]);
            }
        }
  
    printf("\n");
    t_fin = clock();
    runtime = (double)(t_fin - t_ini) / CLOCKS_PER_SEC;
    printf ("Tiempo de Ejecución: %f s\n", runtime);

    return 0;
}
