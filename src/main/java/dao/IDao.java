package dao;

public interface IDao {
    double getValue();

    /* Pourquoi ? C'est le contrat : toute classe qui veut être
     une source de données doit implémenter cette méthode.
     Spring va chercher un bean qui respecte ce contrat pour l'injecter dans MetierImpl.
     */
}
