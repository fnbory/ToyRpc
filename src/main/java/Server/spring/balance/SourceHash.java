package Server.spring.balance;

import Server.Provider;

import java.util.List;

/**
 * @Author: fnbory
 * @Date: 2019/8/19 19:36
 */
public class SourceHash implements  LoadBalance{

    @Override
    public Provider getProvider(List<Provider> providers) throws Exception {
        return null;
    }
}
