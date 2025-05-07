package pcd.ass02.async;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectDepsReportImpl implements ProjectDepsReport {

    private final Map<String, PackageDepsReport> projectDeps = new HashMap<>();

    @Override
    public void putPackageDeps(final String packageName, final PackageDepsReport packageDeps) {
        this.projectDeps.put(packageName, packageDeps);
    }

    @Override
    public Map<String, PackageDepsReport> getAllReports() {
        return Map.copyOf(this.projectDeps);
    }

    @Override
    public Set<String> getProjectReport() {
        return this.projectDeps.values().stream()
                .flatMap(c -> c.getPackageReport().stream())
                .collect(Collectors.toSet());
    }
}
