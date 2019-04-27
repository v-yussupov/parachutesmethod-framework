package org.parachutesmethod.framework.models.java.projectmodel;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import org.parachutesmethod.framework.models.java.JavaConfiguration;

import java.util.HashMap;
import java.util.Map;

public class JavaAnnotation {
    private String name;
    private AnnotationExpr annotationExpression;
    private boolean isMarkerAnnotation;
    private boolean isParachuteAnnotation;
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
        if (JavaConfiguration.PARACHUTE_METHOD_ANNOTATION.value().equals(annotationExpression.getNameAsString())) {
            isParachuteAnnotation = true;
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

    public boolean containsParameter(String name) {
        return parameters.containsKey(name);
    }

    public String getParameterByName(String name) {
        return parameters.get(name);
    }

    public boolean isParachuteAnnotation() {
        return isParachuteAnnotation;
    }

    public boolean isPathAnnotation() {
        return this.getName().equals(JavaConfiguration.PATH_ANNOTATION.value());
    }
}
