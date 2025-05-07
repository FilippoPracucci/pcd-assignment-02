package pcd.ass02.util;

import java.util.Set;

public interface ClassDepsReport {

    void addType(String type);

    Set<String> getReport();

}
