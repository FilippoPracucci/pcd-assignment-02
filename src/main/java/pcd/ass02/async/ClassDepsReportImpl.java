package pcd.ass02.async;

import java.util.HashSet;
import java.util.Set;

public class ClassDepsReportImpl implements ClassDepsReport {

    private final Set<String> types = new HashSet<>();

    @Override
    public void addType(String type) {
        this.types.add(type);
    }

    @Override
    public Set<String> getReport() {
        return Set.copyOf(this.types);
    }
}
