/**
 * cart.js – Artigiani Bottega
 * Piccole utility per il carrello lato client
 */

document.addEventListener('DOMContentLoaded', () => {

    // Conferma rimozione prodotto dal carrello
    document.querySelectorAll('[data-confirm-remove]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            if (!confirm('Vuoi rimuovere questo prodotto dal carrello?')) {
                e.preventDefault();
            }
        });
    });

    // Aggiorna il badge del carrello (se implementato in futuro)
    // Placeholder per eventuali aggiornamenti AJAX del contatore carrello

});
