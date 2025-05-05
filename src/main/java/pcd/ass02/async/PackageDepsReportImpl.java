package pcd.ass02.async;

import pcd.ass02.util.ClassDepsReport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageDepsReportImpl implements PackageDepsReport {

    private final Map<String, ClassDepsReport> packageDeps = new HashMap<>();

    @Override
    public void putClassDeps(final String className, final ClassDepsReport classDeps) {
        this.packageDeps.put(className, classDeps);
    }

    @Override
    public Map<String, ClassDepsReport> getAllReports() {
        return Map.copyOf(this.packageDeps);
    }

    @Override
    public Set<String> getPackageReport() {
        return this.packageDeps.values().stream()
                .flatMap(c -> c.getReport().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return this.packageDeps.toString();
    }
}
