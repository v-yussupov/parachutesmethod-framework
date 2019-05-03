package org.parachutesmethod.framework.models.java.projectmodel;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.resolution.declarations.ResolvedAnnotationDeclaration;
import org.parachutesmethod.framework.models.java.JavaConfiguration;

@JsonIgnoreProperties( {"annotationExpression"})
public class JavaAnnotation {
    private String name;
    private String annotationExpressionString;
    private String className;
    private AnnotationExpr annotationExpression;
    private boolean markerAnnotation;
    private boolean parachuteAnnotation;
    private boolean pathAnnotation;
    private Map<String, String> parameters;

    public JavaAnnotation() {
    }

    public JavaAnnotation(AnnotationExpr annotationExpression) {
        this.annotationExpressionString = annotationExpression.toString();
        this.annotationExpression = annotationExpression;
        this.name = annotationExpression.getNameAsString();

        if (annotationExpression.getClass().equals(MarkerAnnotationExpr.class)) {
            markerAnnotation = true;
        }
        if (annotationExpression.getClass().equals(NormalAnnotationExpr.class)) {
            markerAnnotation = false;
            parameters = new HashMap<>();
            for (MemberValuePair pair : ((NormalAnnotationExpr) annotationExpression).getPairs()) {
                parameters.put(pair.getNameAsString(), pair.getValue().toString());
            }
        }
        if (JavaConfiguration.PARACHUTE_METHOD_ANNOTATION.value().equals(annotationExpression.getNameAsString())) {
            parachuteAnnotation = true;
        } else {
            // do not resolve parachute annotations
            ResolvedAnnotationDeclaration ra = annotationExpression.resolve();
            this.className = ra.getQualifiedName();
        }

        if (this.getName().equals(JavaConfiguration.PATH_ANNOTATION.value())) {
            pathAnnotation = true;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnotationExpressionString() {
        return annotationExpressionString;
    }

    public void setAnnotationExpressionString(String annotationExpressionString) {
        this.annotationExpressionString = annotationExpressionString;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public AnnotationExpr getAnnotationExpression() {
        return annotationExpression;
    }

    public void setAnnotationExpression(AnnotationExpr annotationExpression) {
        this.annotationExpression = annotationExpression;
    }

    public boolean isMarkerAnnotation() {
        return markerAnnotation;
    }

    public void setMarkerAnnotation(boolean markerAnnotation) {
        this.markerAnnotation = markerAnnotation;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public boolean containsParameter(String name) {
        return parameters.containsKey(name);
    }

    public String getParameterByName(String name) {
        return parameters.get(name);
    }

    public boolean isParachuteAnnotation() {
        return parachuteAnnotation;
    }

    public void setParachuteAnnotation(boolean parachuteAnnotation) {
        this.parachuteAnnotation = parachuteAnnotation;
    }

    public boolean isPathAnnotation() {
        return pathAnnotation;
    }

    public void setPathAnnotation(boolean pathAnnotation) {
        this.pathAnnotation = pathAnnotation;
    }
}
