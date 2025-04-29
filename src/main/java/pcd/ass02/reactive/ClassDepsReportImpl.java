package pcd.ass02.reactive;

import java.util.HashSet;
import java.util.Set;

public class ClassDepsReportImpl implements ClassDepsReport {

    private final String path;
    private final Set<String> types = new HashSet<>();

    public ClassDepsReportImpl(String path) {
        this.path = path;
    }

    @Override
    public void addType(String type) {
        this.types.add(type);
    }

    @Override
    public Set<String> getReport() {
        return Set.copyOf(this.types);
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String toString() {
        return this.types.toString();
    }
}
