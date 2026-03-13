// src/main/java/presentation/Presentation2.java
package presentation;

import metier.IMetier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


// Dit a spring que cette classe contient de la configuration
// cad : des @beans, des scans ..
@Configuration

// Dit "va chercher tous les @Component, @Service .. dans ces packages"
@ComponentScan(basePackages = {"dao", "metier", "config"})
public class Presentation2 {

    public static void main(String[] args) {

        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext();

        // ── Choix 1 : activer UN seul profil (décommenter une ligne) ──
        // ctx.getEnvironment().setActiveProfiles("prod");  // DaoImpl  100 → 200
        // ctx.getEnvironment().setActiveProfiles("dev");   // DaoImpl2 150 → 300
        // ctx.getEnvironment().setActiveProfiles("file");  // DaoFile  180 → 360
        // ctx.getEnvironment().setActiveProfiles("api");   // DaoApi   220 → 440

        // ── Choix 2 : laisser commenté → PropertyDrivenConfig prend le relais

        ctx.register(Presentation2.class);
        ctx.refresh();  // Spring construit tout le contexte ici

        /* apres setActiveProfiles() ? le profile doit etre active avant que spring
              construise le contexte.
           refresh() declanche la construct  */

        IMetier metier = ctx.getBean(IMetier.class);
        System.out.println("Résultat = " + metier.calcul());

        ctx.close();
    }
}