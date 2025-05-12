package pcd.ass02.reactive;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

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
        this.source = this.createSource();
    }

    private Observable<RxClassDepsReport> createSource() {
        return Observable.defer(() ->
            Observable
                    .fromStream(Files.walk(Paths.get(this.rootPath)))
                    .subscribeOn(Schedulers.io())
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains(".java"))
                    .flatMap(this::getClassDependencies)
        );
    }

    private ObservableSource<RxClassDepsReport> getClassDependencies(final Path filePath) {
        final RxClassDepsReport report = new RxClassDepsReportImpl(
                filePath.toString().substring(this.rootPath.length())
        );
        return Observable
                .just(filePath)
                .map(path -> this.parser.parse(String.join("\n", Files.readAllLines(path))))
                .filter(ParseResult::isSuccessful)
                .map(ParseResult::getResult)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMapIterable(unit -> unit.findAll(ClassOrInterfaceDeclaration.class))
                .flatMap(declaration -> Observable.merge(
                        Observable.fromIterable(declaration.getFields()).map(field -> (Node) field),
                        Observable.fromIterable(declaration.getMethods()).map(method -> (Node) method)
                ))
                .flatMapIterable(node -> node.findAll(ClassOrInterfaceType.class))
                .distinct()
                .map(ClassOrInterfaceType::toString)
                .filter(type -> !this.exclusions.contains(type))
                .doOnNext(report::addType)
                .ignoreElements()
                .andThen(Observable.just(report));
    }

    public void setRootPath(final String rootPath) {
        this.rootPath = rootPath;
    }

    public Observable<RxClassDepsReport> getSource() {
        return this.source;
    }
}
