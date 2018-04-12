package keyfinder;


import de.uni_leipzig.simba.keydiscovery.model.CandidateNode;
import de.uni_leipzig.simba.keydiscovery.rockerone.Rocker;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

public class RockerImplementation implements Keyfinder {
    @Override
    public void find(String[] args) throws IOException, InterruptedException {
        Rocker r = null;
        String name = args[0];
        String dataPpath = args[1];
        String owlPath = args[2];
        try {
            r = new Rocker(name, dataPpath, owlPath, false, true, 1.0);
            r.run();
            Set<CandidateNode> results = r.getKeys();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}


/**
public  class RockerImplementation{



}
*/