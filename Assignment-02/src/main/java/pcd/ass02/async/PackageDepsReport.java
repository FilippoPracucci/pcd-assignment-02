package pcd.ass02.async;

import pcd.ass02.util.ClassDepsReport;

import java.util.Map;
import java.util.Set;

public interface PackageDepsReport {

    void putClassDeps(String className, ClassDepsReport classDeps);

    Map<String, ClassDepsReport> getAllReports();

    Set<String> getPackageReport();

}
