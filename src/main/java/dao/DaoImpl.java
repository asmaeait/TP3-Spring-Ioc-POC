package dao;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("dao")
 @Profile("prod")
public class DaoImpl implements IDao {

    @Override
    public double getValue(){
        return 100.0;
    }
}
