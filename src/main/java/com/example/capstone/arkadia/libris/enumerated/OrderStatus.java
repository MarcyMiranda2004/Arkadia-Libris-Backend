package com.example.capstone.arkadia.libris.enumerated;

public enum OrderStatus {
    PENDING_PAYMENT,   // creato ma non ancora pagato
    IN_PREPARAZIONE,   // pagamento avvenuto, in lavorazione
    IN_SPEDIZIONE,     // consegnato al corriere
    DELIVERED,         // consegnato all’utente
    COMPLETATO,        // chiuso dal sistema / utente
    CANCELED           // annullato prima della spedizione
}
