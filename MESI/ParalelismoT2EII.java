/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paralelismo.t2eii;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author trejkev
 */
public class ParalelismoT2EII {
    private static String kine;
    private static Object line;

    
    public static void main(String[] args)  throws IOException {
        int tamcacheL1 = 8000;
        int tamcacheL2 = 64000;
        int tambloq = 16;
        int byteoffset = 1;
        int indexL1 = 9;
        int indexL2 = 12;
        int tagL1;
        int tagL2;
        long contadorp1 = 0;
        //long contadorp2 = 0;
        long contaux1 = 0;
        long contaux2 = 0;        
        long datos1 = 6240679;
        long datos2 = 18580385;
        String M ="M";
        String E = "E";
        String S = "S";
        String I = "I";
        String nada = "";
        System.out.println("Hola, el siguiente programa está diseñado para la simulación de una memoria");
        System.out.println("cache utilizando el protocolo MESI para la coherencia de datos. A continuación");
        System.out.println("se muestran los datos de interés. \n");
        System.out.println("El tamaño de la cache del nivel 1 es: " + tamcacheL1 + " bits");
        System.out.println("El tamaño de la cache del nivel 2 es: " + tamcacheL2 + " bits");
        System.out.println("El tamaño del bloque para ambos niveles es: " + tambloq + " bits");
        System.out.println("La cantidad de bits para el byte offset es: " + byteoffset);
        System.out.println("La cantidad de bits para el index del nivel 1 de cache es: " + indexL1);
        System.out.println("La cantidad de bits para el index del nivel 2 de cache es: " + indexL2);
        tagL1 = 31-byteoffset-indexL1;
        tagL2 = 31-byteoffset-indexL2;
        System.out.println("La cantidad de bits para el tag en el nivel 1 es: " + tagL1);
        System.out.println("La cantidad de bits para el tag en el nivel 2 es: " + tagL2 + "\n");
        
        //Hasta acá ya se tienen todos los datos necesarios para el diseño de la memoria 
        
        String[] cacheL1atag = new String[520]; //Almacena el tag de  $L1a
        String[] cacheL1aestado = new String[520]; // Almacena el estado (MESI) de $L1a
        
        String[] cacheL1btag = new String[520]; //Almacena el tag de  $L1b
        String[] cacheL1bestado = new String[520]; // Almacena el estado (MESI) de $L1b
        
        String[] cacheL2tag = new String[4100]; //Almacena el tag de  $L2
        String[] cacheL2estado = new String[4100]; // Almacena el estado (MESI) de $L2
        
        
        
        try(FileReader fr = new FileReader("C:/aligned.trace")){
            
            //if(contadorp1>=contadorp2){
            //  contadorp2=contadorp2+1;  
            //}else{
            //    contadorp1=contadorp1+1;
            //}
            
            
            BufferedReader in = new BufferedReader(fr);
            
            while((line = in.readLine()) != null) {
                int contaccion = 0;
                //System.out.println(line);
                //System.out.println("La línea sin manipular es: " + line);
                String linea = null; //reinicio linea
                linea = in.readLine();
                StringTokenizer tokens = new StringTokenizer(linea, "    ");
                String direccion = tokens.nextToken().trim();
                Long k = Long.parseLong(direccion, 16); //Convierte el string direccion en long k
                String direcbin = Long.toBinaryString(k); //Convierte el long k en string binario
                String accion = tokens.nextToken().trim();
                //System.out.println("La dirección es: " + direccion); 
                //System.out.println("La dirección en binario es: " + direcbin);
                //System.out.println("La accion es: " + accion); 
                int length = direcbin.length();
                
                //Se calculan todas las variables necesarias del dato en particular
                String bodato = direcbin.substring(length - byteoffset,length);
                String indexL1dato = direcbin.substring(length - byteoffset - indexL1 , length - byteoffset);
                String indexL2dato = direcbin.substring(length - byteoffset - indexL2 , length - byteoffset);
                String tagL1dato = direcbin.substring(0,length - byteoffset - indexL1);
                String tagL2dato = direcbin.substring(0,length - byteoffset - indexL2);
            
                
                //inicia algoritmo para pasar index L1 a decimal
                long indexbinarioL1 = Long.parseLong(indexL1dato.trim());
                long countL1 = 1;
                long auxdecimalL1;
                long indexdecimalL1=0;
                while(indexbinarioL1 > 0) { 
                    auxdecimalL1 = indexbinarioL1 %2;
                    indexdecimalL1 = indexdecimalL1 + auxdecimalL1*countL1;
                    indexbinarioL1 /= 10;
                    countL1 = countL1*2;
                }
                //System.out.println("El index en decimal de L1 es: " + indexdecimalL1);
                int indexL1decima = (int)indexdecimalL1;
                //Termina algoritmo para pasar index L1 a decimal
                
                //inicia algoritmo para pasar index L2 a decimal
                long indexbinarioL2 = Long.parseLong(indexL2dato.trim());
                long countL2 = 1;
                long auxdecimalL2;
                long indexdecimalL2=0;
                while(indexbinarioL2 > 0) { 
                    auxdecimalL2 = indexbinarioL2 %2;
                    indexdecimalL2 = indexdecimalL2 + auxdecimalL2*countL2;
                    indexbinarioL2 /= 10;
                    countL2 = countL2*2;
                }
                //System.out.println("El index en decimal de L2 es: " + indexdecimalL2);
                int indexL2decima = (int)indexdecimalL2;
                //Termina algoritmo para pasar index L2 a decimal
                
                String aux1 = cacheL1atag[indexL1decima];
                String aux2 = cacheL1btag[indexL1decima];
                
                
                
                //Acá empiezan los cálculos lógicos para la coherencia de los datos
		//if((contadorp1+contadorp2)%2==0){ //El contador es par, trabaja el procesador a
                contadorp1++;                
                if(contadorp1%2==0){                    
                    contaux1 = contaux1+1;
                    if(accion.equals("S")){//escritura proc a
			if(tagL1dato.equals(aux1)){//está en mi L1a, reviso estado
                            if(M.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=1;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    if(nada.equals(cacheL1bestado[indexL1decima])){
                                        cacheL1bestado[indexL1decima]="";
                                        cacheL1btag[indexL1decima]="";//por si acaso
                                    }else{
                                        cacheL1bestado[indexL1decima]="I";
                                    }
                                    cacheL2estado[indexL2decima]="I";
                                    //todo sigue igual
                                    //System.out.println("Escritura, L1a, M");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}                            
                            }
                            if(E.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=2;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    cacheL1bestado[indexL1decima]="";
                                    cacheL1btag[indexL1decima]="";//por si acaso
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1a, E");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}                               
                            }
                            if(S.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=3;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    cacheL1bestado[indexL1decima]="I";
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1a, S");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}                                
                            }
                            if(I.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=4;
                                    //por fuerza está en L1b, E o M
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    cacheL1bestado[indexL1decima]="I";
                                    cacheL2estado[indexL2decima]="I";	
                                    //System.out.println("Escritura, L1a, I");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}                                
                            }
			}else{//no está en mi L1a
                            if(tagL1dato.equals(cacheL1btag[indexL1decima])){//pruebo L1b
				//puede estar E o M
				if(contaccion==0){
                                    contaccion=5;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    cacheL1bestado[indexL1decima]="I";
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1a no, L1b si");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}                               
                            }else{//el dato está en discos
				if(contaccion==0){
                                    contaccion=6;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL2tag[indexL2decima]=tagL2dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    cacheL1bestado[indexL1decima]="";
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1a, discos");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}                                
                            }
			}
                    }else{//lectura proc a
			if(tagL1dato.equals(aux1)){//el dato está en L1a
                            if(M.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=7;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="M";
                                    if(nada.equals(cacheL1bestado[indexL1decima])){
                                        cacheL1bestado[indexL1decima]="";
                                        cacheL1btag[indexL1decima]="";//por si acaso
                                    }else{
                                        cacheL1bestado[indexL1decima]="I";
                                    }
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Lectura, L1a si, M");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
                                    //no hago nada
				}
                            }
                            if(E.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=8;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="E";
                                    cacheL2estado[indexL2decima]="E";
                                    cacheL1bestado[indexL1decima]="";
                                    cacheL1btag[indexL1decima]="";//por si acaso
                                    //no hago nada
                                    //System.out.println("Lectura, L1a si, E");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
                            if(S.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=9;
                                    cacheL1aestado[indexL1decima]="S";
                                    cacheL1bestado[indexL1decima]="S";
                                    if(S.equals(cacheL2estado[indexL2decima])){
                                        cacheL2estado[indexL2decima]="S";
                                    }else{
                                        cacheL2estado[indexL2decima]="I";
                                    }
                                    //System.out.println("Lectura, L1a si, S");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
                                    //no hago nada
				}
                            }
                            if(I.equals(cacheL1aestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=10;
                                    //el dato está en L1b modificado
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL1aestado[indexL1decima]="S";
                                    cacheL1bestado[indexL1decima]="S";
                                    cacheL2estado[indexL2decima]="I";
                                    //el dato ya es inválido en L2
                                    //System.out.println("Lectura, L1a si, I");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }							
			}else{//el dato puede estar en L1b
                            //puede ser M o E
                            if(tagL1dato.equals(cacheL1btag[indexL1decima])){
                                if(contaccion==0){
                                    contaccion=11;
                                    if(E.equals(cacheL1bestado[indexL1decima])){
                                        cacheL1btag[indexL1decima]=tagL1dato;//por si acaso
					cacheL1atag[indexL1decima]=tagL1dato;
					cacheL1aestado[indexL1decima]="S";
					cacheL1bestado[indexL1decima]="S";
					cacheL2estado[indexL2decima]="S";
                                    }				
                                    if(M.equals(cacheL1bestado[indexL1decima])){//el dato está M en L1b  
                                        cacheL1btag[indexL1decima]=tagL1dato;//por si acaso
					cacheL1atag[indexL1decima]=tagL1dato;
					cacheL1aestado[indexL1decima]="S";
					cacheL1bestado[indexL1decima]="S";
                                        cacheL2estado[indexL2decima]="I";
					//ya está inválido en L2
                                    }
                                    //System.out.println("Lectura, L1a no, L1b si, M");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }else{//el dato está en discos
				if(contaccion==0){
                                    contaccion=12;
                                    cacheL1atag[indexL1decima]=tagL1dato;
                                    cacheL2tag[indexL2decima]=tagL2dato;
                                    cacheL1aestado[indexL1decima]="E";
                                    cacheL2estado[indexL2decima]="E";
                                    cacheL1bestado[indexL1decima]="";
                                    cacheL1btag[indexL1decima]="";//por si acaso
                                    //System.out.println("Lectura, L1a, discos");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
			}
                    }
                }else{ //El contador es impar, trabaja el procesador b
                    contaux2 = contaux2+1;
                    //contadorp1++;
                    if(accion.equals("S")){//escritura proc b
			if(tagL1dato.equals(aux2)){//está en mi L1b, reviso estado
                            if(M.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    contaccion=13;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL2estado[indexL2decima]="I";
                                    if(nada.equals(cacheL1aestado[indexL1decima])){
                                        cacheL1aestado[indexL1decima]="";
                                        cacheL1atag[indexL1decima]="";//por si acaso
                                    }else{
                                        cacheL1aestado[indexL1decima]="I";
                                    }
                                    //System.out.println("Escritura, L1b si, M");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
                                    //todo estado sigue igual
				}
                            }
                            if(E.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=14;
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL2estado[indexL2decima]="I";
                                    cacheL1aestado[indexL1decima]="";
                                    cacheL1atag[indexL1decima]="";//por si acaso
                                    //System.out.println("Escritura, L1b si, E");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
                            if(S.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=15;
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL1aestado[indexL1decima]="I";
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1b si, S");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
                            if(I.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=16;
                                    //por fuerza está en L1b, E o M
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL1aestado[indexL1decima]="I";
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1b si, I");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
			}else{//no está en mi L1b, pruebo L1a
                            if(tagL1dato.equals(cacheL1atag[indexL1decima])){
                                //puede estar E o M
				if(contaccion==0){
                                    contaccion=17;
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL1aestado[indexL1decima]="I";
                                    cacheL2estado[indexL2decima]="I";
                                    //System.out.println("Escritura, L1b no, L1a si");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }else{//el dato está en discos
				if(contaccion==0){
                                    contaccion=18;
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL2tag[indexL2decima]=tagL2dato;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL2estado[indexL2decima]="I";
                                    cacheL1aestado[indexL1decima]="";
                                    cacheL1atag[indexL1decima]="";//por si acaso
                                    //System.out.println("Escritura, L1b, discos");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
			}
                    }else{//lectura proc b
			if(tagL1dato.equals(aux2)){//el dato está en L1b
                            if(M.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=19;
                                    cacheL1bestado[indexL1decima]="M";
                                    cacheL2estado[indexL2decima]="I";
                                    if(nada.equals(cacheL1aestado[indexL1decima])){
                                        cacheL1aestado[indexL1decima]="";
                                        cacheL1atag[indexL1decima]="";//por si acaso
                                    }else{
                                        cacheL1aestado[indexL1decima]="I";
                                    }
                                    //System.out.println("Lectura, L1b si, M");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
                                    //no hago nada
				}
                            }
                            if(E.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=20;
                                    //no hago nada
                                    cacheL1bestado[indexL1decima]="E";
                                    cacheL1aestado[indexL1decima]="";
                                    cacheL1atag[indexL1decima]="";//por si acaso
                                    cacheL2estado[indexL2decima]="E";
                                    //System.out.println("Lectura, L1b si, E");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
                            if(S.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=21;
                                    cacheL1bestado[indexL1decima]="S";
                                    cacheL1aestado[indexL1decima]="S";
                                    if(I.equals(cacheL2estado[indexL2decima])){
                                        cacheL2estado[indexL2decima]="I";
                                    }else{
                                        cacheL2estado[indexL2decima]="S";
                                    }
                                    //System.out.println("Lectura, L1b si, S");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
                                    //no hago nada
				}
                            }
                            if(I.equals(cacheL1bestado[indexL1decima])){
				if(contaccion==0){
                                    contaccion=22;
                                    //el dato está en L1b modificado
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL1bestado[indexL1decima]="S";
                                    cacheL1aestado[indexL1decima]="S";
                                    cacheL2estado[indexL2decima]="I";
                                    //el dato ya es inválido en L2
                                    //System.out.println("Lectura, L1b si, I");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }
							
			}else{//el dato puede estar en L1a
                            //puede ser M o E
                            if(tagL1dato.equals(cacheL1atag[indexL1decima])){
                                if(contaccion==0){
                                    contaccion=23;
                                    if(E.equals(cacheL1aestado[indexL1decima])){                                   
					cacheL1btag[indexL1decima]=tagL1dato;
					cacheL1bestado[indexL1decima]="S";
					cacheL1aestado[indexL1decima]="S";
					cacheL2estado[indexL2decima]="S";
                                    }				
                                    if(M.equals(cacheL1aestado[indexL1decima])){//el dato está M en L1b                                    
					cacheL1btag[indexL1decima]=cacheL1atag[indexL1decima];
					cacheL1bestado[indexL1decima]="S";
					cacheL1aestado[indexL1decima]="S";
                                        cacheL2estado[indexL2decima]="I";
					//ya está inválido en L2
                                    }
                                    //System.out.println("Lectura, L1b no, L1a si");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }else{//el dato está en discos
				if(contaccion==0){
                                    contaccion=24;
                                    cacheL1btag[indexL1decima]=tagL1dato;
                                    cacheL2tag[indexL2decima]=tagL2dato;
                                    cacheL1bestado[indexL1decima]="E";
                                    cacheL2estado[indexL2decima]="E";
                                    cacheL1aestado[indexL1decima]="";
                                    cacheL1atag[indexL1decima]="";//por si acaso
                                    //System.out.println("Lectura, L1b, discos");
                                    //System.out.println("EstL1a " + cacheL1aestado[indexL1decima]);
                                    //System.out.println("EstL1b " + cacheL1bestado[indexL1decima]);
                                    //System.out.println("EstL2 " + cacheL2estado[indexL2decima]);
                                    //System.out.println("El contador seleccionador de procesador: " + contadorp1);
				}
                            }	
			}
                    }
		}
                //k=contadorp1;
                //System.out.println("el contador es: " + k);
                //if(contadorp1%2==0){
                //    System.out.println("trabaja Pa");
                //}else{
                //    System.out.println("trabaja Pb");
                //}
                //System.out.println("La acción es: " + accion);
                //System.out.println("el tag en el nivel 1 es: " + tagL1dato);
                //System.out.println("el tag en el nivel 2 es: " + tagL2dato);
                //System.out.println("el index en el nivel 1 es: " + indexL1decima);
                //System.out.println("el index en el nivel 2 es: " + indexL2decima);
                //System.out.println("el tag en el $1a es: " + aux1);
                //System.out.println("el tag en el $1b es: " + aux2);
                //System.out.println("el estado en el $1a es: " + cacheL1aestado[indexL1decima]);
                //System.out.println("el estado en el $1b es: " + cacheL1bestado[indexL1decima]);
                //System.out.println("el estado en el $2 es: " + cacheL2estado[indexL2decima] + "\n");
                
                if((contadorp1)>=(datos1+datos2-25)){
                    System.out.println("\n\n\n El contaccion es: " + contaccion);
                    System.out.println("La direccion en binario del dato es: " + direcbin);
                    System.out.println("La accion es: " + accion);
                    //if(contadorp1%2==0){
                //    System.out.println("trabaja Pa");
                        System.out.println("El contadorp1 es: " + contadorp1);
                    if(contadorp1%2==0){
                        System.out.println("El procesador con el control es el procesador CPU1"); 
                        System.out.println("El estado nuevo del dato en mi cash es: " + cacheL1aestado[indexL1decima]);
                        System.out.println("El estado nuevo del dato en el otro cash es: " + cacheL1bestado[indexL1decima]);
                        System.out.println("El estado nuevo del dato en el cash principal es: " + cacheL2estado[indexL2decima]);
                        System.out.println("El tag nuevo del dato en el cash L1a es: " + cacheL1atag[indexL1decima]);
                        System.out.println("El tag nuevo del dato en el cash L1b es: " + cacheL1btag[indexL1decima]);
                        System.out.println("El tag nuevo del dato en el cash principal es: " + cacheL2tag[indexL2decima]);
                    }else{
                        System.out.println("El procesador con el control es el procesador CPU0");
                        System.out.println("El estado nuevo del dato en mi cash es: " + cacheL1bestado[indexL1decima]);
                        System.out.println("El estado nuevo del dato en el otro cash es: " + cacheL1aestado[indexL1decima]);                        
                        System.out.println("El estado nuevo del dato en el cash principal es: " + cacheL2estado[indexL2decima]);
                        System.out.println("El tag nuevo del dato en el cash L1a es: " + cacheL1atag[indexL1decima]);
                        System.out.println("El tag nuevo del dato en el cash L1b es: " + cacheL1btag[indexL1decima]);
                        System.out.println("El tag nuevo del dato en el cash principal es: " + cacheL2tag[indexL2decima]);
                    }
                }
                contaccion=0;
            }   
        }
    }    
}