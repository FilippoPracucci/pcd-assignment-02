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
            }
            promise.complete(report);
        });

        return promise.future();
    }

}
