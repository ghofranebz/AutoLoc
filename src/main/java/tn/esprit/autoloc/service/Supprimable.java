package tn.esprit.autoloc.service;

/** ISP : contrat minimal de suppression, à implémenter uniquement par les services qui supportent delete. */
public interface Supprimable<ID> {

    void delete(ID id);
}
