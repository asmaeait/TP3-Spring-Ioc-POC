package metier;

import dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component("metier") // Spring detecte automatique cette classe et l'enregistre comme bean nomme "metier"
public class MetierImpl implements IMetier {

    @Autowired
    private IDao dao; // pas de @Qualifier ici, jamais
    // prq? on ne veut pas toucher a cette classe pour changer d'implementation
    // cest le principe OCP

    @Override
    public double calcul(){
        return dao.getValue() *2;
    }

    @PostConstruct
    private void init(){
        System.out.println("[TRACE] DAO injecte = " + dao.getClass().getSimpleName());

    }

}
