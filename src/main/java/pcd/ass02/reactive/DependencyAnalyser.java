package pcd.ass02.reactive;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.reactivex.rxjava3.core.Observable;
import pcd.ass02.async.ClassDepsReport;
import pcd.ass02.async.ClassDepsReportImpl;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class DependencyAnalyser {

    private static final String EXCLUSIONS_PATH = "src/main/java/pcd/ass02/exclusions.txt";

    private final Observable<String> source;
    private final JavaParser parser;
    private final Set<String> exclusions;
    private String rootPath;

    public DependencyAnalyser() {
        this.parser = new JavaParser();
        try {
            this.exclusions = Set.copyOf(Files.readAllLines(Paths.get(EXCLUSIONS_PATH)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Observable.create not accessible from parser
        this.source = Observable.create(emitter -> {
            new Thread(() -> {
                try (final Stream<Path> paths = Files.walk(Paths.get( this.rootPath))) {
                    paths.filter(Files::isRegularFile).forEach(f -> {
                        if (!f.getFileName().toString().contains(".java")) {
                            System.out.println(f.getFileName() + "is not a java file");
                            return;
                        }
                        ParseResult<CompilationUnit> parseResult = null;
                        try {
                            parseResult = this.parser.parse(String.join("\n", Files.readAllLines(f)));
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            throw new RuntimeException(e);
                        }
                        if (!parseResult.isSuccessful()) {
                            System.out.println("Result not successful");
                            return;
                        }
                        final ClassDepsReport report = new ClassDepsReportImpl();
                        final Optional<CompilationUnit> compilationUnit = parseResult.getResult();
                        if (compilationUnit.isPresent()) {
                            final List<ClassOrInterfaceDeclaration> children = compilationUnit.get()
                                    .findAll(ClassOrInterfaceDeclaration.class);
                            children.forEach(c -> {
                                Stream.concat(c.getFields().stream().map(field -> (Node) field), c.getMethods().stream().map(m -> (Node) m))
                                        .flatMap(e -> e.findAll(ClassOrInterfaceType.class).stream())
                                        .distinct()
                                        .forEach(t -> {
                                            if (!this.exclusions.contains(t.toString())) {
                                                emitter.onNext(t.toString());
                                            }
                                        });
                            });
                        } else {
                            System.out.println("ComputationalUnit not present");
                        }
                    });
                } catch (final IOException e) {
                    e.getMessage();
                }
                emitter.onComplete();
            }).start();
        });
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public Observable<String> getSource() {
        return this.source;
    }
}
