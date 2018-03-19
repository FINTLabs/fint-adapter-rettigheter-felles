package no.fint.provider.tilganger.service;

import no.fint.model.felles.kompleksedatatyper.Identifikator;
import no.fint.model.ressurser.tilganger.Rettighet;

public enum RettighetsFactory {
    ;

    public static Rettighet createRettighet(String id, String name, String code, String description) {

        Identifikator identifikator = new Identifikator();
        identifikator.setIdentifikatorverdi(id);
        Rettighet rettighet = new Rettighet();
        rettighet.setSystemId(identifikator);
        rettighet.setNavn(name);
        rettighet.setKode(code);
        rettighet.setBeskrivelse(description);

        return rettighet;
    }

    public static Rettighet createRettighet(String id, String name, String description) {

        return createRettighet(id, name, id, description);
    }
}
