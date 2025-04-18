package pcd.ass02.async;

import java.util.HashMap;
import java.util.Map;

public class PackageDepsReportImpl implements PackageDepsReport {

    private final Map<String, ClassDepsReport> packageDeps = new HashMap<>();

    @Override
    public void putClassDeps(final String className, final ClassDepsReport classDeps) {
        this.packageDeps.put(className, classDeps);
    }

    @Override
    public Map<String, ClassDepsReport> getReport() {
        return Map.copyOf(this.packageDeps);
    }
}
