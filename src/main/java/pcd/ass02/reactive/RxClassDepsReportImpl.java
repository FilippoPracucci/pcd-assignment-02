package pcd.ass02.reactive;

import pcd.ass02.util.ClassDepsReportImpl;

public class RxClassDepsReportImpl extends ClassDepsReportImpl implements RxClassDepsReport  {

    private final String path;

    public RxClassDepsReportImpl(final String path) {
        super();
        this.path = path;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
