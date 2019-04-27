package org.parachutesmethod.framework.extraction.languages.java.visitors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class ImportDeclarationCollector extends VoidVisitorAdapter<List<ImportDeclaration>> {

    @Override
    public void visit(ImportDeclaration importDeclaration, List<ImportDeclaration> collector) {
        super.visit(importDeclaration, collector);
        collector.add(importDeclaration);
    }
}

