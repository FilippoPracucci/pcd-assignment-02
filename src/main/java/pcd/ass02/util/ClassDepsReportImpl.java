package pcd.ass02.util;

import java.util.HashSet;
import java.util.Set;

public class ClassDepsReportImpl implements ClassDepsReport {

    private final Set<String> types;

    public ClassDepsReportImpl() {
        this.types = new HashSet<>();
    }

    @Override
    public void addType(final String type) {
        this.types.add(type);
    }

    @Override
    public Set<String> getReport() {
        return Set.copyOf(this.types);
    }

    @Override
    public String toString() {
        return this.types.toString();
    }
}
