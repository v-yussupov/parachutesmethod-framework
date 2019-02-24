package org.parachutesmethod.framework.extraction.explorers.java.visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class ClassOrInterfaceDeclarationCollector extends VoidVisitorAdapter<List<ClassOrInterfaceDeclaration>> {
    @Override
    public void visit(ClassOrInterfaceDeclaration coid, List<ClassOrInterfaceDeclaration> collector) {
        super.visit(coid, collector);
        collector.add(coid);
    }
}
