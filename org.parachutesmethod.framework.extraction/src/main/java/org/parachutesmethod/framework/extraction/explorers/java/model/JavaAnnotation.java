package org.parachutesmethod.framework.extraction.explorers.java.model;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.HashMap;
import java.util.Map;

public class JavaAnnotation {
    private String name;
    private AnnotationExpr annotationExpression;
    private boolean isMarkerAnnotation;
    private Map<String, String> parameters;

    JavaAnnotation(AnnotationExpr annotationExpression) {
        this.annotationExpression = annotationExpression;
        this.name = annotationExpression.getNameAsString();

        if (annotationExpression.getClass().equals(MarkerAnnotationExpr.class)) {
            isMarkerAnnotation = true;
        }
        if (annotationExpression.getClass().equals(NormalAnnotationExpr.class)) {
            isMarkerAnnotation = false;
            parameters = new HashMap<>();
            for (MemberValuePair pair : ((NormalAnnotationExpr) annotationExpression).getPairs()) {
                parameters.put(pair.getNameAsString(), pair.getValue().toString());
            }
        }
    }

    public String getName() {
        return name;
    }

    public AnnotationExpr getAnnotationExpression() {
        return annotationExpression;
    }

    public boolean isMarkerAnnotation() {
        return isMarkerAnnotation;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
