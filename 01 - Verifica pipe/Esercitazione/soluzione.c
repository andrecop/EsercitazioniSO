/*
 * Soluzione di Crypto per il corso di Sistemi Operativi MOD2 - 05/03/2020
 * Prima verifica: pipe
 *
 * NOTA: Eseguire in questo modo, così crypto avrà già creato le pipe
 * sulle quali andrà a lavorare il programma di soluzione, i programmi
 * devono essere necessariamente nella stessa cartella, altrimenti và
 * specificata.
 *
 * ./crypto -i /tmp/pipeIn -o /tmp/pipeOut & (sleep 1; ./soluzione )
 *
 * (in alternativa lanciare i due programmi su due terminali diversi)
 *
 * Autore: Andrea Coppetta
 * Matricola: 873849
 *
 * La soluzione consiste in un ciclo while(1) (loop potenzialmente "infinito")
 * che svolge le seguenti operazioni:
 *
 * 1. Legge il primo carattere dalla pipeOut, e ne calcola la chiave di
 *    decriptazione (nel nostro caso compresa fra 1 e 9);
 * 2. Avvia un ciclo do-while (per non perdere la lettera letta
 *    precedentemente), e in seguito decripta carattere per carattere, così
 *    da avere maggiore controllo sulla quantità di byte letti, e non dover
 *    inoltre bufferizzare nulla e lo invia nella pipeIn del programma crypto,
 *    fino al raggiungimento della fine della frase, (ovvero il carattere '#');
 * 3. Appena rileva il carattere il fine frase, il ciclo viene interrotto e
 *    il programma si prepara a ricevere la prossima frase in ingresso,
 *    ricominciando dal punto 1.
 */

#include <fcntl.h>
#include <sys/types.h>
#include <sys/uio.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#define pipeIn  "/tmp/pipeIn"
#define pipeOut "/tmp/pipeOut"


int main() {

  int fd0,fd1;
  int i;
  int c=0;
  char in, out;

  /*
   * Apre pipeOut in lettura e pipeIn in scrittura.
   * se l'apertura di una delle due pipe fallisce, allora stampa il messaggio
   * di errore e termina l'esecuzione, altrimenti prosegue.
   */
  if ((fd0=open(pipeOut,O_RDONLY))<0 || (fd1=open(pipeIn,O_WRONLY))<0){
    perror("errore apertura pipe"); // messaggio di errore
    exit(EXIT_FAILURE);             // termina l'esecuzione
  }


  while(1) {
    /*
     * Il comando successivo, permette di leggere il primo carattere da
     * pipeOut, e nel caso siano terminate le domande o pipeOut vienga
     * chiusa, andrà a terminare il programma per evitare di occupare
     * inutilmente memoria, e per essere sicuri che il programma
     * termini (inoltre è possibile evitare overflow).
     */
    if ((read(fd0,&in,1))==0) exit(EXIT_SUCCESS);
    c = in - 'A';                   // calcola la chiave

   /*
    * NOTA: per calcolare la chiave di decriptazione sarà sufficiente
    * sottrarre dal carattere letto (ovvero 'A' criptata), il carattere 'A',
    * siccome i char possono essere trattati come interi, si troverà
    * facilmente la "distanza" fra il carattere cifrato e non, così da poter
    * decifrare l'intera frase.
    */

   /*
    * Il seguente ciclo invece, permette di leggere da pipeOut, decifrare, e
    * inviare la soluzione, carattere per carattere alla pipeIn, fino al
    * raggiungimento della fine della frase (ovvero '#').
    */
    do {
      out = in - c;                 // decifro il carattere appena letto
      write(fd1,&out,1);            // invio il carattere in pipeOut
      if(out == '#') break;         // se la frase finisce, interrompo il ciclo
    } while(read(fd0,&in,1));       // leggo il successivo carattere
  }
}
