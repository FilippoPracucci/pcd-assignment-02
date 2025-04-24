package pcd.ass02.async;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DependencyAnalyserLib {

    final Verticle verticle;
    final JavaParser parser;

    public DependencyAnalyserLib(final Verticle verticle) {
        this.verticle = verticle;
        this.parser = new JavaParser();
    }

    public Future<ClassDepsReport> getClassDependencies(final String classSrcFile) {
        final Promise<ClassDepsReport> promise = Promise.promise();
        final FileSystem fileSystem = this.verticle.getVertx().fileSystem();
        final Future<Buffer> fileFuture = fileSystem.readFile(classSrcFile);
        fileFuture.onSuccess((final Buffer res) -> {
            ParseResult<CompilationUnit> parseResult = this.parser.parse(res.toString());
            if (!parseResult.isSuccessful()) {
                promise.fail("Parsing error.");
                return;
            }
            final ClassDepsReport report = new ClassDepsReportImpl();
            final Optional<CompilationUnit> compilationUnit = parseResult.getResult();
            if (compilationUnit.isPresent()) {
                final List<ClassOrInterfaceDeclaration> children = compilationUnit.get()
                        .findAll(ClassOrInterfaceDeclaration.class);
                children.forEach(c -> {
                    Stream.concat(c.getFields().stream().map(f -> (Node) f), c.getMethods().stream().map(m -> (Node) m))
                            .flatMap(e -> e.findAll(ClassOrInterfaceType.class).stream())
                            .distinct()
                            .forEach(t -> report.addType(t.toString()));
                });
                promise.complete(report);
            } else {
                promise.fail("Parsing error.");
            }
        });
        fileFuture.onFailure(promise::fail);
        return promise.future();
    }

    public Future<PackageDepsReport> getPackageDependencies(final String packageSrcFolder) {
        final Promise<PackageDepsReport> promise = Promise.promise();
        final FileSystem fileSystem = this.verticle.getVertx().fileSystem();
        final Future<List<String>> futureFiles = fileSystem.readDir(packageSrcFolder);
        futureFiles.onSuccess((final List<String> paths) -> {
            final PackageDepsReport packageDepsReport = new PackageDepsReportImpl();
            final List<Future<ClassDepsReport>> futures = new ArrayList<>();
            paths.forEach(p -> {
                if (p.contains(".")) {
                    final String fileName = Paths.get(p).getFileName().toString();
                    final String className = fileName.substring(0, fileName.lastIndexOf("."));
                    final Future<ClassDepsReport> futureClassDeps = getClassDependencies(p);
                    futureClassDeps.onSuccess((ClassDepsReport c) -> {
                        packageDepsReport.putClassDeps(className, c);
                    });
                    futures.add(futureClassDeps);
                }
            });
            Future.all(futures).onSuccess((CompositeFuture cf) -> {
                promise.complete(packageDepsReport);
            });
            Future.all(futures).onFailure(promise::fail);
        });
        futureFiles.onFailure(promise::fail);
        return promise.future();
    }

    private Future<Void> findAllPackages(final FileSystem fileSystem, final String packagePath,
                                         final List<String> packages) {
        final Promise<Void> promise = Promise.promise();
        final Future<CompositeFuture> cfFuture = fileSystem.readDir(packagePath).compose(paths -> {
            List<Future<Void>> futures = new ArrayList<>();
            paths.forEach(path -> {
                Future<Void> future = fileSystem.props(path).compose(props -> {
                    if (props.isDirectory()) {
                        packages.add(path);
                        return findAllPackages(fileSystem, path, packages);
                    } else {
                        return Future.succeededFuture();
                    }
                });
                futures.add(future);
            });
            return Future.all(futures);
        });
        cfFuture.onSuccess(cf -> promise.complete());
        cfFuture.onFailure(promise::fail);
        return promise.future();
    }

    public Future<ProjectDepsReport> getProjectDependencies(final String projectSrcFolder) {
        final Promise<ProjectDepsReport> promise = Promise.promise();
        final FileSystem fileSystem = this.verticle.getVertx().fileSystem();
        final List<String> packages = new ArrayList<>();
        final Future<Void> futurePackages = findAllPackages(fileSystem, projectSrcFolder, packages);
        futurePackages.onSuccess(v -> {
            final List<Future<PackageDepsReport>> futures = new ArrayList<>();
            final ProjectDepsReport projectDeps = new ProjectDepsReportImpl();
            packages.forEach(p -> {
                Future<PackageDepsReport> futurePackage = getPackageDependencies(p);
                futurePackage.onSuccess(packageDeps -> {
                    final String packageName = Paths.get(p).getFileName().toString();
                    projectDeps.putPackageDeps(packageName, packageDeps);
                });
                futures.add(futurePackage);
            });
            Future.all(futures).onSuccess((CompositeFuture cf) -> {
                promise.complete(projectDeps);
            });
            Future.all(futures).onFailure(promise::fail);
        });
        futurePackages.onFailure(promise::fail);
        return promise.future();
    }

}
