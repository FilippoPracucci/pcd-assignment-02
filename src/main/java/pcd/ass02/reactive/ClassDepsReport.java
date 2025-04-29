package pcd.ass02.reactive;

import java.util.Set;

public interface ClassDepsReport {

    void addType(String type);

    Set<String> getReport();

    String getPath();

}
