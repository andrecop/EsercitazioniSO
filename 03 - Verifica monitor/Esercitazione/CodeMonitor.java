/*
 * Materia:         Sistemi operativi Mod. 2 [CT0125]
 * Verifica n°3:    "Verifica monitor: coda prioritaria"
 * Studente:        Andrea Coppetta
 * Matricola:       873849
 * Data:            23-04-2020
 *
 *
 * COMMENTO GENERALE:
 *
 *
 * - Come si è arrivati a scegliere le strutture dati utilizzate per la sincronizzazione
 *
 * Per la sincronizzazione utilizziamo:
 *
 * 1. Una variabile booleana (sportello) per gestire la mutua esclusione sullo sportello.
 *    La variabile è inizializzata a false per indicare che lo sportello è inizialmente
 *    libero;
 *
 * 2. Una variabile intera (codaPrioritaria) che indica il numero di persone in coda
 *    prioritaria. Questa variabile serve per far attendere le persone nella coda normale,
 *    che tutte le persone nella coda prioritaria siano passate, così da rispettare la
 *    precedenza.
 *
 *
 *
 * - Come funziona, intuitivamente, la sincronizzazione
 *
 *   La funzione 'attendiSportello' è bloccante se lo sportello (al quale si sta tentando
 *   di accedere) è occupato, ovvero la viariabile booleana 'sportello' è settata a true.
 *   Nel caso della coda normale, è presente un ulteriore blocco, ovvero la variabile
 *   intera 'codaPrioritaria', che conta il numero di persone che hanno precedenza rispetto
 *   alla coda normale, quando tutte le persone saranno passate (o non sono presenti) si
 *   può accedere allo sportello.
 *   (inoltre se nell'attesa si presentano altre persone nella coda prioritaria, la
 *   variabile viene incrementata e dunque l'attesa aumenta, per lasciare precedenza
 *   all'altra coda)
 *
 *
 *
 * - Come sono state utilizzate le wait e le notify / notifyAll
 *
 *   Le 'wait' vengono utilizzate in 'attendiSportello' nel caso siano presenti persone
 *   allo sportello, questo su entrambe le code, per non permettere alle persone in coda
 *   di accedervi prima che si sia liberato.
 *   Nel caso invece della coda normale, la "caduta" nella 'wait' viene influenzata anche
 *   dalla presenza o no di persone nell'altra coda.
 *   Viene utilizzata una 'notifyAll' in 'liberaSportello', e serve a sincronizzare e
 *   liberare gli altri thread, ovvero notificarli che la persona allo sportello ha finito
 *   ed è libero.
 *
 *   La funzione 'attendiSportello' suddivide le due code grazie ad un 'if-then-else', che
 *   separa la coda prioritaria (la quale andra ad incrementare la variabile
 *   'codaPrioritaria') da quella normale (la quale verificherà la presenza di persone
 *   nell'altra coda).
 *
 */

public class CodeMonitor {    
    // Dichiarare qui le variabili del monitor
    boolean sportello=false;
    int codaPrioritaria=0;


    // La persona è la prossima ad essere servita e attende che si liberi 
    // lo sportello. Se la persona è sulla coda prioritaria (priority è
    // true) appena lo sportello si libera la persona può procedere. Se
    // la persona è sulla coda normale (priority è false) la persona
    // attende che non ci siano altre persone in coda prioritaria già
    // in attesa e che lo sportello si liberi. In altre parole, dà 
    // la precedenza a persone in attesa in coda prioritaria.
    synchronized void attendiSportello(boolean priority) throws InterruptedException {
        if (priority) {
            // gestione coda prioritaria
            codaPrioritaria++;
            while(sportello == true){
                wait();
            }
            sportello=true;
            codaPrioritaria--;
        } else {
            // gestione coda non prioritaria
            while(sportello == true || codaPrioritaria > 0){
                wait();
            }
            sportello=true;
        }
    }

    // La persona ha raggiunto lo sportello, ha fruito del servizio e ora 
    // lo libera
    public synchronized void liberaSportello() {
        sportello = false;
        notifyAll();
    } 
}
