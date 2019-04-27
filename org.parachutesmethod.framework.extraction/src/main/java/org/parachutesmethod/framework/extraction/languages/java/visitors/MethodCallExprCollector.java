package org.parachutesmethod.framework.extraction.languages.java.visitors;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

public class MethodCallExprCollector extends VoidVisitorAdapter<List<MethodCallExpr>> {

    @Override
    public void visit(MethodCallExpr mce, List<MethodCallExpr> collector) {
        super.visit(mce, collector);
        collector.add(mce);
    }
}