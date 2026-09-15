package exam_project.artigiani_webapp.services;

import exam_project.artigiani_webapp.entities.ShippingAddressEntity;
import exam_project.artigiani_webapp.entities.UserEntity;
import exam_project.artigiani_webapp.repositories.ShippingAddressRepository;
import exam_project.artigiani_webapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShippingAddressService {

    private final ShippingAddressRepository addressRepository;
    private final UserRepository            userRepository;

    @Autowired
    public ShippingAddressService(ShippingAddressRepository addressRepository,
                                  UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository    = userRepository;
    }

    // ----------------------------------------------------------------
    // LETTURA
    // ----------------------------------------------------------------

    public List<ShippingAddressEntity> findAllByEmail(String email) {
        UserEntity user = requireUser(email);
        return addressRepository.findByUser(user);
    }

    // ----------------------------------------------------------------
    // AGGIUNTA
    // ----------------------------------------------------------------

    @Transactional
    public void addAddress(String email,
                           String fullName, String country,
                           String street,   String streetExtra,
                           String postalCode, String city, String province,
                           boolean setAsDefault) {

        UserEntity user = requireUser(email);

        // Se è il primo indirizzo, lo impostiamo sempre come predefinito
        List<ShippingAddressEntity> existing = addressRepository.findByUser(user);
        boolean forceDefault = existing.isEmpty();

        if (setAsDefault || forceDefault) {
            // Rimuovi il flag da tutti gli altri
            existing.forEach(a -> a.setDefault(false));
            addressRepository.saveAll(existing);
        }

        ShippingAddressEntity addr = new ShippingAddressEntity();
        addr.setUser(user);
        addr.setFullName(fullName);
        addr.setCountry(country == null || country.isBlank() ? "Italia" : country);
        addr.setStreet(street);
        addr.setStreetExtra(streetExtra);
        addr.setPostalCode(postalCode);
        addr.setCity(city);
        addr.setProvince(province);
        addr.setDefault(setAsDefault || forceDefault);

        addressRepository.save(addr);
    }

    // ----------------------------------------------------------------
    // MODIFICA
    // ----------------------------------------------------------------

    @Transactional
    public boolean updateAddress(String email, Long addressId,
                                 String fullName, String country,
                                 String street,   String streetExtra,
                                 String postalCode, String city, String province,
                                 boolean setAsDefault) {

        UserEntity user = requireUser(email);
        ShippingAddressEntity addr = addressRepository.findByIdAndUser(addressId, user)
                .orElse(null);
        if (addr == null) return false;

        if (setAsDefault) {
            addressRepository.findByUser(user).forEach(a -> a.setDefault(false));
            addressRepository.saveAll(addressRepository.findByUser(user));
        }

        addr.setFullName(fullName);
        addr.setCountry(country == null || country.isBlank() ? "Italia" : country);
        addr.setStreet(street);
        addr.setStreetExtra(streetExtra);
        addr.setPostalCode(postalCode);
        addr.setCity(city);
        addr.setProvince(province);
        if (setAsDefault) addr.setDefault(true);

        addressRepository.save(addr);
        return true;
    }

    // ----------------------------------------------------------------
    // IMPOSTA PREDEFINITO
    // ----------------------------------------------------------------

    @Transactional
    public boolean setDefault(String email, Long addressId) {
        UserEntity user = requireUser(email);
        ShippingAddressEntity target = addressRepository.findByIdAndUser(addressId, user)
                .orElse(null);
        if (target == null) return false;

        // Rimuove predefinito da tutti
        addressRepository.findByUser(user).forEach(a -> {
            a.setDefault(false);
            addressRepository.save(a);
        });

        target.setDefault(true);
        addressRepository.save(target);
        return true;
    }

    // ----------------------------------------------------------------
    // ELIMINAZIONE SINGOLO INDIRIZZO
    // ----------------------------------------------------------------

    @Transactional
    public boolean deleteAddress(String email, Long addressId) {
        UserEntity user = requireUser(email);
        ShippingAddressEntity addr = addressRepository.findByIdAndUser(addressId, user)
                .orElse(null);
        if (addr == null) return false;

        boolean wasDefault = addr.isDefault();
        addressRepository.delete(addr);

        // Se era il predefinito, promuovi il primo rimasto
        if (wasDefault) {
            List<ShippingAddressEntity> remaining = addressRepository.findByUser(user);
            if (!remaining.isEmpty()) {
                remaining.get(0).setDefault(true);
                addressRepository.save(remaining.get(0));
            }
        }
        return true;
    }

    // ----------------------------------------------------------------
    // ELIMINAZIONE DI TUTTI GLI INDIRIZZI DI UN UTENTE
    // (usato dal deleteAccount in UserService)
    // ----------------------------------------------------------------

    @Transactional
    public void deleteAllByUser(UserEntity user) {
        addressRepository.deleteByUser(user);
    }

    // ----------------------------------------------------------------
    // HELPER
    // ----------------------------------------------------------------

    private UserEntity requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + email));
    }
}
