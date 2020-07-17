module org.jusecase.jte {
    requires jdk.unsupported;
    requires java.compiler;
    requires jdk.httpserver;

    requires transitive org.jusecase.jte.runtime;

    exports org.jusecase.jte.resolve;
    exports org.jusecase.jte.compile to org.jusecase.jte.runtime;
}