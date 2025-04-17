package pcd.ass02.async;

import java.util.Set;

public interface ClassDepsReport {

    void addType(String type);

    Set<String> getReport();

}
