package pcd.ass02.reactive;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class DependencyAnalyser {

    private static final String EXCLUSIONS_PATH = "src/main/java/pcd/ass02/exclusions.txt";

    private final Observable<RxClassDepsReport> source;
    private final JavaParser parser;
    private final Set<String> exclusions;
    private String rootPath;

    public DependencyAnalyser() {
        this.parser = new JavaParser();
        try {
            this.exclusions = Set.copyOf(Files.readAllLines(Paths.get(EXCLUSIONS_PATH)));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        this.source = this.analyse();
    }

    private Observable<RxClassDepsReport> analyse() {
        // Observable.create not accessible from parser
        return Observable.create(emitter -> {
            new Thread(() -> {
                try (final Stream<Path> paths = Files.walk(Paths.get(this.rootPath))) {
                    paths.filter(Files::isRegularFile).forEach(f -> getClassDependencies(emitter, f));
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.onComplete();
            }).start();
        });
    }

    private void getClassDependencies(final ObservableEmitter<RxClassDepsReport> emitter, final Path filePath) {
        if (!filePath.getFileName().toString().contains(".java")) {
            return;
        }
        final RxClassDepsReport report = new RxClassDepsReportImpl(
                filePath.toString().substring(this.rootPath.length())
        );
        tryParse(filePath).ifPresentOrElse(children -> children.forEach(c -> {
            Stream.concat(c.getFields().stream().map(field -> (Node) field), c.getMethods().stream().map(m -> (Node) m))
                    .flatMap(e -> e.findAll(ClassOrInterfaceType.class).stream())
                    .distinct()
                    .forEach(t -> {
                        if (!this.exclusions.contains(t.toString())) {
                            report.addType(t.toString());
                        }
                    });
        }), () -> {
            throw new RuntimeException();
        });
        emitter.onNext(report);
    }

    private Optional<List<ClassOrInterfaceDeclaration>> tryParse(final Path filePath) {
        ParseResult<CompilationUnit> parseResult;
        try {
            parseResult = this.parser.parse(String.join("\n", Files.readAllLines(filePath)));
        } catch (final IOException e) {
            return Optional.empty();
        }
        if (!parseResult.isSuccessful()) {
            return Optional.empty();
        }
        return parseResult.getResult().map(unit -> unit.findAll(ClassOrInterfaceDeclaration.class));
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public Observable<RxClassDepsReport> getSource() {
        return this.source;
    }
}
