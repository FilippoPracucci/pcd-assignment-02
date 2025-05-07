package pcd.ass02.async;

import java.util.Map;
import java.util.Set;

public interface ProjectDepsReport {

    void putPackageDeps(String packageName, PackageDepsReport packageDeps);

    Map<String, PackageDepsReport> getAllReports();

    Set<String> getProjectReport();

}
