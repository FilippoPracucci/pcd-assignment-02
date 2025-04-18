package pcd.ass02.async;

import java.util.Map;

public interface PackageDepsReport {

    void putClassDeps(String className, ClassDepsReport classDeps);

    Map<String, ClassDepsReport> getReport();

}
