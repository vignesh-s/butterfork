package butterfork.compiler;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import butterfork.BindArray;
import butterfork.BindBitmap;
import butterfork.BindBool;
import butterfork.BindColor;
import butterfork.BindDimen;
import butterfork.BindDrawable;
import butterfork.BindInt;
import butterfork.BindString;
import butterfork.BindView;
import butterfork.OnCheckedChanged;
import butterfork.OnClick;
import butterfork.OnEditorAction;
import butterfork.OnFocusChange;
import butterfork.OnItemClick;
import butterfork.OnItemLongClick;
import butterfork.OnLongClick;
import butterfork.OnPageChange;
import butterfork.OnTextChanged;
import butterfork.OnTouch;
import butterfork.internal.ListenerClass;
import butterfork.internal.ListenerMethod;
import butterfork.BindViews;
import butterfork.OnItemSelected;
import butterfork.Optional;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public final class ButterKnifeProcessor extends AbstractProcessor {
  static final String VIEW_TYPE = "android.view.View";
  private static final String BINDING_CLASS_SUFFIX = "$$ViewBinder";
  private static final String COLOR_STATE_LIST_TYPE = "android.content.res.ColorStateList";
  private static final String BITMAP_TYPE = "android.graphics.Bitmap";
  private static final String DRAWABLE_TYPE = "android.graphics.drawable.Drawable";
  private static final String TYPED_ARRAY_TYPE = "android.content.res.TypedArray";
  private static final String NULLABLE_ANNOTATION_NAME = "Nullable";
  private static final String STRING_TYPE = "java.lang.String";
  private static final String LIST_TYPE = List.class.getCanonicalName();
  private static final List<Class<? extends Annotation>> LISTENERS = Arrays.asList(//
      OnCheckedChanged.class, //
      OnClick.class, //
      OnEditorAction.class, //
      OnFocusChange.class, //
      OnItemClick.class, //
      OnItemLongClick.class, //
      OnItemSelected.class, //
      OnLongClick.class, //
      OnPageChange.class, //
      OnTextChanged.class, //
      OnTouch.class //
  );

  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;

  @Override public synchronized void init(ProcessingEnvironment env) {
    super.init(env);

    elementUtils = env.getElementUtils();
    typeUtils = env.getTypeUtils();
    filer = env.getFiler();
  }

  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> types = new LinkedHashSet<>();

    types.add(BindArray.class.getCanonicalName());
    types.add(BindBitmap.class.getCanonicalName());
    types.add(BindBool.class.getCanonicalName());
    types.add(BindColor.class.getCanonicalName());
    types.add(BindDimen.class.getCanonicalName());
    types.add(BindDrawable.class.getCanonicalName());
    types.add(BindInt.class.getCanonicalName());
    types.add(BindString.class.getCanonicalName());
    types.add(BindView.class.getCanonicalName());
    types.add(BindViews.class.getCanonicalName());

    for (Class<? extends Annotation> listener : LISTENERS) {
      types.add(listener.getCanonicalName());
    }

    return types;
  }

  @Override public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
    Map<TypeElement, BindingClass> targetClassMap = findAndParseTargets(env);

    for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
      TypeElement typeElement = entry.getKey();
      BindingClass bindingClass = entry.getValue();

      try {
        bindingClass.brewJava().writeTo(filer);
      } catch (IOException e) {
        error(typeElement, "Unable to write view binder for type %s: %s", typeElement,
            e.getMessage());
      }
    }

    return true;
  }

  private Map<TypeElement, BindingClass> findAndParseTargets(RoundEnvironment env) {
    Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();
    Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

    // Process each @BindArray element.
    for (Element element : env.getElementsAnnotatedWith(BindArray.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceArray(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindArray.class, e);
      }
    }

    // Process each @BindBitmap element.
    for (Element element : env.getElementsAnnotatedWith(BindBitmap.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceBitmap(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindBitmap.class, e);
      }
    }

    // Process each @BindBool element.
    for (Element element : env.getElementsAnnotatedWith(BindBool.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceBool(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindBool.class, e);
      }
    }

    // Process each @BindColor element.
    for (Element element : env.getElementsAnnotatedWith(BindColor.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceColor(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindColor.class, e);
      }
    }

    // Process each @BindDimen element.
    for (Element element : env.getElementsAnnotatedWith(BindDimen.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceDimen(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindDimen.class, e);
      }
    }

    // Process each @BindDrawable element.
    for (Element element : env.getElementsAnnotatedWith(BindDrawable.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceDrawable(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindDrawable.class, e);
      }
    }

    // Process each @BindInt element.
    for (Element element : env.getElementsAnnotatedWith(BindInt.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceInt(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindInt.class, e);
      }
    }

    // Process each @BindString element.
    for (Element element : env.getElementsAnnotatedWith(BindString.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseResourceString(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindString.class, e);
      }
    }

    // Process each @BindView element.
    for (Element element : env.getElementsAnnotatedWith(BindView.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseBindView(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindView.class, e);
      }
    }

    // Process each @BindViews element.
    for (Element element : env.getElementsAnnotatedWith(BindViews.class)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseBindViews(element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        logParsingError(element, BindViews.class, e);
      }
    }

    // Process each annotation that corresponds to a listener.
    for (Class<? extends Annotation> listener : LISTENERS) {
      findAndParseListener(env, listener, targetClassMap, erasedTargetNames);
    }

    // Try to find a parent binder for each.
    for (Map.Entry<TypeElement, BindingClass> entry : targetClassMap.entrySet()) {
      TypeElement parentType = findParentType(entry.getKey(), erasedTargetNames);
      if (parentType != null) {
        BindingClass bindingClass = entry.getValue();
        BindingClass parentBindingClass = targetClassMap.get(parentType);
        bindingClass.setParent(parentBindingClass);
      }
    }

    return targetClassMap;
  }

  private void logParsingError(Element element, Class<? extends Annotation> annotation,
      Exception e) {
    StringWriter stackTrace = new StringWriter();
    e.printStackTrace(new PrintWriter(stackTrace));
    error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
  }

  private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass,
      String targetThing, Element element) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify method modifiers.
    Set<Modifier> modifiers = element.getModifiers();
    if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
      error(element, "@%s %s must not be private or static. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing type.
    if (enclosingElement.getKind() != CLASS) {
      error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify containing class visibility is not private.
    if (enclosingElement.getModifiers().contains(PRIVATE)) {
      error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
          annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    return hasError;
  }

  private boolean isBindingInWrongPackage(Class<? extends Annotation> annotationClass,
      Element element) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
    String qualifiedName = enclosingElement.getQualifiedName().toString();

    if (qualifiedName.startsWith("android.")) {
      error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
          annotationClass.getSimpleName(), qualifiedName);
      return true;
    }
    if (qualifiedName.startsWith("java.")) {
      error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
          annotationClass.getSimpleName(), qualifiedName);
      return true;
    }

    return false;
  }

  private void parseBindView(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Start by verifying common generated code restrictions.
    boolean hasError = isInaccessibleViaGeneratedCode(BindView.class, "fields", element)
        || isBindingInWrongPackage(BindView.class, element);

    // Verify that the target type extends from View.
    TypeMirror elementType = element.asType();
    if (elementType.getKind() == TypeKind.TYPEVAR) {
      TypeVariable typeVariable = (TypeVariable) elementType;
      elementType = typeVariable.getUpperBound();
    }
    if (!isSubtypeOfType(elementType, VIEW_TYPE) && !isInterface(elementType)) {
      error(element, "@%s fields must extend from View or be an interface. (%s.%s)",
          BindView.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String id = element.getAnnotation(BindView.class).value();

    BindingClass bindingClass = targetClassMap.get(enclosingElement);
    if (bindingClass != null) {
      ViewBindings viewBindings = bindingClass.getViewBinding(id);
      if (viewBindings != null && viewBindings.getFieldBinding() != null) {
        FieldViewBinding existingBinding = viewBindings.getFieldBinding();
        error(element, "Attempt to use @%s for an already bound ID <%s> on '%s'. (%s.%s)",
            BindView.class.getSimpleName(), id, existingBinding.getName(),
            enclosingElement.getQualifiedName(), element.getSimpleName());
        return;
      }
    } else {
      bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    }

    String name = element.getSimpleName().toString();
    TypeName type = TypeName.get(elementType);
    boolean required = isFieldRequired(element);

    FieldViewBinding binding = new FieldViewBinding(name, type, required);
    bindingClass.addField(id, binding);

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
  }

  private void parseBindViews(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Start by verifying common generated code restrictions.
    boolean hasError = isInaccessibleViaGeneratedCode(BindViews.class, "fields", element)
        || isBindingInWrongPackage(BindViews.class, element);

    // Verify that the type is a List or an array.
    TypeMirror elementType = element.asType();
    String erasedType = doubleErasure(elementType);
    TypeMirror viewType = null;
    FieldCollectionViewBinding.Kind kind = null;
    if (elementType.getKind() == TypeKind.ARRAY) {
      ArrayType arrayType = (ArrayType) elementType;
      viewType = arrayType.getComponentType();
      kind = FieldCollectionViewBinding.Kind.ARRAY;
    } else if (LIST_TYPE.equals(erasedType)) {
      DeclaredType declaredType = (DeclaredType) elementType;
      List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
      if (typeArguments.size() != 1) {
        error(element, "@%s List must have a generic component. (%s.%s)",
            BindViews.class.getSimpleName(), enclosingElement.getQualifiedName(),
            element.getSimpleName());
        hasError = true;
      } else {
        viewType = typeArguments.get(0);
      }
      kind = FieldCollectionViewBinding.Kind.LIST;
    } else {
      error(element, "@%s must be a List or array. (%s.%s)", BindViews.class.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }
    if (viewType != null && viewType.getKind() == TypeKind.TYPEVAR) {
      TypeVariable typeVariable = (TypeVariable) viewType;
      viewType = typeVariable.getUpperBound();
    }

    // Verify that the target type extends from View.
    if (viewType != null && !isSubtypeOfType(viewType, VIEW_TYPE) && !isInterface(viewType)) {
      error(element, "@%s List or array type must extend from View or be an interface. (%s.%s)",
          BindViews.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String[] ids = element.getAnnotation(BindViews.class).value();
    if (ids.length == 0) {
      error(element, "@%s must specify at least one ID. (%s.%s)", BindViews.class.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    String duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@%s annotation contains duplicate ID <%s>. (%s.%s)",
          BindViews.class.getSimpleName(), duplicateId, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    assert viewType != null; // Always false as hasError would have been true.
    TypeName type = TypeName.get(viewType);
    boolean required = isFieldRequired(element);

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldCollectionViewBinding binding = new FieldCollectionViewBinding(name, type, kind, required);
    bindingClass.addFieldCollection(ids, binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceBool(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is bool.
    if (element.asType().getKind() != TypeKind.BOOLEAN) {
      error(element, "@%s field type must be 'boolean'. (%s.%s)",
          BindBool.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindBool.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindBool.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindBool.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldResourceBinding binding = new FieldResourceBinding(id, name, "getBoolean", false);
    bindingClass.addResource(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceColor(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is int or ColorStateList.
    boolean isColorStateList = false;
    TypeMirror elementType = element.asType();
    if (COLOR_STATE_LIST_TYPE.equals(elementType.toString())) {
      isColorStateList = true;
    } else if (elementType.getKind() != TypeKind.INT) {
      error(element, "@%s field type must be 'int' or 'ColorStateList'. (%s.%s)",
          BindColor.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindColor.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindColor.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindColor.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldResourceBinding binding = new FieldResourceBinding(id, name,
        isColorStateList ? "getColorStateList" : "getColor", true);
    bindingClass.addResource(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceDimen(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is int or ColorStateList.
    boolean isInt = false;
    TypeMirror elementType = element.asType();
    if (elementType.getKind() == TypeKind.INT) {
      isInt = true;
    } else if (elementType.getKind() != TypeKind.FLOAT) {
      error(element, "@%s field type must be 'int' or 'float'. (%s.%s)",
          BindDimen.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindDimen.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindDimen.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindDimen.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldResourceBinding binding = new FieldResourceBinding(id, name,
        isInt ? "getDimensionPixelSize" : "getDimension", false);
    bindingClass.addResource(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceBitmap(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is Bitmap.
    if (!BITMAP_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'Bitmap'. (%s.%s)",
          BindBitmap.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindBitmap.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindBitmap.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindBitmap.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldBitmapBinding binding = new FieldBitmapBinding(id, name);
    bindingClass.addBitmap(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceDrawable(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is Drawable.
    if (!DRAWABLE_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'Drawable'. (%s.%s)",
          BindDrawable.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindDrawable.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindDrawable.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindDrawable.class).value();
    String tint = element.getAnnotation(BindDrawable.class).tint();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldDrawableBinding binding = new FieldDrawableBinding(id, name, tint);
    bindingClass.addDrawable(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceInt(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is int.
    if (element.asType().getKind() != TypeKind.INT) {
      error(element, "@%s field type must be 'int'. (%s.%s)", BindInt.class.getSimpleName(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindInt.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindInt.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindInt.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldResourceBinding binding = new FieldResourceBinding(id, name, "getInteger", false);
    bindingClass.addResource(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceString(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is String.
    if (!STRING_TYPE.equals(element.asType().toString())) {
      error(element, "@%s field type must be 'String'. (%s.%s)",
          BindString.class.getSimpleName(), enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindString.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindString.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindString.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldResourceBinding binding = new FieldResourceBinding(id, name, "getString", false);
    bindingClass.addResource(binding);

    erasedTargetNames.add(enclosingElement);
  }

  private void parseResourceArray(Element element, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    boolean hasError = false;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Verify that the target type is supported.
    String methodName = getArrayResourceMethodName(element);
    if (methodName == null) {
      error(element,
          "@%s field type must be one of: String[], int[], CharSequence[], %s. (%s.%s)",
          BindArray.class.getSimpleName(), TYPED_ARRAY_TYPE, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    // Verify common generated code restrictions.
    hasError |= isInaccessibleViaGeneratedCode(BindArray.class, "fields", element);
    hasError |= isBindingInWrongPackage(BindArray.class, element);

    if (hasError) {
      return;
    }

    // Assemble information on the field.
    String name = element.getSimpleName().toString();
    String id = element.getAnnotation(BindArray.class).value();

    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    FieldResourceBinding binding = new FieldResourceBinding(id, name, methodName, false);
    bindingClass.addResource(binding);

    erasedTargetNames.add(enclosingElement);
  }

  /**
   * Returns a method name from the {@link android.content.res.Resources} class for array resource
   * binding, null if the element type is not supported.
   */
  private static String getArrayResourceMethodName(Element element) {
    TypeMirror typeMirror = element.asType();
    if (TYPED_ARRAY_TYPE.equals(typeMirror.toString())) {
      return "obtainTypedArray";
    }
    if (TypeKind.ARRAY.equals(typeMirror.getKind())) {
      ArrayType arrayType = (ArrayType) typeMirror;
      String componentType = arrayType.getComponentType().toString();
      if (STRING_TYPE.equals(componentType)) {
        return "getStringArray";
      } else if ("int".equals(componentType)) {
        return "getIntArray";
      } else if ("java.lang.CharSequence".equals(componentType)) {
        return "getTextArray";
      }
    }
    return null;
  }

  /** Returns the first duplicate element inside an array, null if there are no duplicates. */
  private static String findDuplicate(String[] array) {
    Set<String> seenElements = new LinkedHashSet<>();

    for (String element : array) {
      if (!seenElements.add(element)) {
        return element;
      }
    }

    return null;
  }

  /** Uses both {@link Types#erasure} and string manipulation to strip any generic types. */
  private String doubleErasure(TypeMirror elementType) {
    String name = typeUtils.erasure(elementType).toString();
    int typeParamStart = name.indexOf('<');
    if (typeParamStart != -1) {
      name = name.substring(0, typeParamStart);
    }
    return name;
  }

  private void findAndParseListener(RoundEnvironment env,
      Class<? extends Annotation> annotationClass, Map<TypeElement, BindingClass> targetClassMap,
      Set<TypeElement> erasedTargetNames) {
    for (Element element : env.getElementsAnnotatedWith(annotationClass)) {
      if (!SuperficialValidation.validateElement(element)) continue;
      try {
        parseListenerAnnotation(annotationClass, element, targetClassMap, erasedTargetNames);
      } catch (Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));

        error(element, "Unable to generate view binder for @%s.\n\n%s",
            annotationClass.getSimpleName(), stackTrace.toString());
      }
    }
  }

  private void parseListenerAnnotation(Class<? extends Annotation> annotationClass, Element element,
      Map<TypeElement, BindingClass> targetClassMap, Set<TypeElement> erasedTargetNames)
      throws Exception {
    // This should be guarded by the annotation's @Target but it's worth a check for safe casting.
    if (!(element instanceof ExecutableElement) || element.getKind() != METHOD) {
      throw new IllegalStateException(
          String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
    }

    ExecutableElement executableElement = (ExecutableElement) element;
    TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

    // Assemble information on the method.
    Annotation annotation = element.getAnnotation(annotationClass);
    Method annotationValue = annotationClass.getDeclaredMethod("value");
    if (annotationValue.getReturnType() != String[].class) {
      throw new IllegalStateException(
          String.format("@%s annotation value() type not String[].", annotationClass));
    }

    String[] ids = (String[]) annotationValue.invoke(annotation);
    String name = executableElement.getSimpleName().toString();
    boolean required = isListenerRequired(executableElement);

    // Verify that the method and its containing class are accessible via generated code.
    boolean hasError = isInaccessibleViaGeneratedCode(annotationClass, "methods", element);
    hasError |= isBindingInWrongPackage(annotationClass, element);

    String duplicateId = findDuplicate(ids);
    if (duplicateId != null) {
      error(element, "@%s annotation for method contains duplicate ID <%s>. (%s.%s)",
          annotationClass.getSimpleName(), duplicateId, enclosingElement.getQualifiedName(),
          element.getSimpleName());
      hasError = true;
    }

    ListenerClass listener = annotationClass.getAnnotation(ListenerClass.class);
    if (listener == null) {
      throw new IllegalStateException(
          String.format("No @%s defined on @%s.", ListenerClass.class.getSimpleName(),
              annotationClass.getSimpleName()));
    }

    for (String id : ids) {
      if (id == null || id.isEmpty()) {
        if (ids.length == 1) {
          if (!required) {
            error(element, "ID-free binding must not be annotated with @Optional. (%s.%s)",
                enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
          }

          // Verify target type is valid for a binding without an id.
          String targetType = listener.targetType();
          if (!isSubtypeOfType(enclosingElement.asType(), targetType)
              && !isInterface(enclosingElement.asType())) {
            error(element, "@%s annotation without an ID may only be used with an object of type "
                    + "\"%s\" or an interface. (%s.%s)",
                annotationClass.getSimpleName(), targetType,
                enclosingElement.getQualifiedName(), element.getSimpleName());
            hasError = true;
          }
        } else {
          error(element, "@%s annotation contains invalid ID %s. (%s.%s)",
              annotationClass.getSimpleName(), id, enclosingElement.getQualifiedName(),
              element.getSimpleName());
          hasError = true;
        }
      }
    }

    ListenerMethod method;
    ListenerMethod[] methods = listener.method();
    if (methods.length > 1) {
      throw new IllegalStateException(String.format("Multiple listener methods specified on @%s.",
          annotationClass.getSimpleName()));
    } else if (methods.length == 1) {
      if (listener.callbacks() != ListenerClass.NONE.class) {
        throw new IllegalStateException(
            String.format("Both method() and callback() defined on @%s.",
                annotationClass.getSimpleName()));
      }
      method = methods[0];
    } else {
      Method annotationCallback = annotationClass.getDeclaredMethod("callback");
      Enum<?> callback = (Enum<?>) annotationCallback.invoke(annotation);
      Field callbackField = callback.getDeclaringClass().getField(callback.name());
      method = callbackField.getAnnotation(ListenerMethod.class);
      if (method == null) {
        throw new IllegalStateException(
            String.format("No @%s defined on @%s's %s.%s.", ListenerMethod.class.getSimpleName(),
                annotationClass.getSimpleName(), callback.getDeclaringClass().getSimpleName(),
                callback.name()));
      }
    }

    // Verify that the method has equal to or less than the number of parameters as the listener.
    List<? extends VariableElement> methodParameters = executableElement.getParameters();
    if (methodParameters.size() > method.parameters().length) {
      error(element, "@%s methods can have at most %s parameter(s). (%s.%s)",
          annotationClass.getSimpleName(), method.parameters().length,
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    // Verify method return type matches the listener.
    TypeMirror returnType = executableElement.getReturnType();
    if (returnType instanceof TypeVariable) {
      TypeVariable typeVariable = (TypeVariable) returnType;
      returnType = typeVariable.getUpperBound();
    }
    if (!returnType.toString().equals(method.returnType())) {
      error(element, "@%s methods must have a '%s' return type. (%s.%s)",
          annotationClass.getSimpleName(), method.returnType(),
          enclosingElement.getQualifiedName(), element.getSimpleName());
      hasError = true;
    }

    if (hasError) {
      return;
    }

    Parameter[] parameters = Parameter.NONE;
    if (!methodParameters.isEmpty()) {
      parameters = new Parameter[methodParameters.size()];
      BitSet methodParameterUsed = new BitSet(methodParameters.size());
      String[] parameterTypes = method.parameters();
      for (int i = 0; i < methodParameters.size(); i++) {
        VariableElement methodParameter = methodParameters.get(i);
        TypeMirror methodParameterType = methodParameter.asType();
        if (methodParameterType instanceof TypeVariable) {
          TypeVariable typeVariable = (TypeVariable) methodParameterType;
          methodParameterType = typeVariable.getUpperBound();
        }

        for (int j = 0; j < parameterTypes.length; j++) {
          if (methodParameterUsed.get(j)) {
            continue;
          }
          if (isSubtypeOfType(methodParameterType, parameterTypes[j])
              || isInterface(methodParameterType)) {
            parameters[i] = new Parameter(j, TypeName.get(methodParameterType));
            methodParameterUsed.set(j);
            break;
          }
        }
        if (parameters[i] == null) {
          StringBuilder builder = new StringBuilder();
          builder.append("Unable to match @")
              .append(annotationClass.getSimpleName())
              .append(" method arguments. (")
              .append(enclosingElement.getQualifiedName())
              .append('.')
              .append(element.getSimpleName())
              .append(')');
          for (int j = 0; j < parameters.length; j++) {
            Parameter parameter = parameters[j];
            builder.append("\n\n  Parameter #")
                .append(j + 1)
                .append(": ")
                .append(methodParameters.get(j).asType().toString())
                .append("\n    ");
            if (parameter == null) {
              builder.append("did not match any listener parameters");
            } else {
              builder.append("matched listener parameter #")
                  .append(parameter.getListenerPosition() + 1)
                  .append(": ")
                  .append(parameter.getType());
            }
          }
          builder.append("\n\nMethods may have up to ")
              .append(method.parameters().length)
              .append(" parameter(s):\n");
          for (String parameterType : method.parameters()) {
            builder.append("\n  ").append(parameterType);
          }
          builder.append(
              "\n\nThese may be listed in any order but will be searched for from top to bottom.");
          error(executableElement, builder.toString());
          return;
        }
      }
    }

    MethodViewBinding binding = new MethodViewBinding(name, Arrays.asList(parameters), required);
    BindingClass bindingClass = getOrCreateTargetClass(targetClassMap, enclosingElement);
    for (String id : ids) {
      if (!bindingClass.addMethod(id, listener, method, binding)) {
        error(element, "Multiple listener methods with return value specified for ID <%s>. (%s.%s)",
            id, enclosingElement.getQualifiedName(), element.getSimpleName());
        return;
      }
    }

    // Add the type-erased version to the valid binding targets set.
    erasedTargetNames.add(enclosingElement);
  }

  private boolean isInterface(TypeMirror typeMirror) {
    return typeMirror instanceof DeclaredType
        && ((DeclaredType) typeMirror).asElement().getKind() == INTERFACE;
  }

  private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
    if (otherType.equals(typeMirror.toString())) {
      return true;
    }
    if (typeMirror.getKind() != TypeKind.DECLARED) {
      return false;
    }
    DeclaredType declaredType = (DeclaredType) typeMirror;
    List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
    if (typeArguments.size() > 0) {
      StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
      typeString.append('<');
      for (int i = 0; i < typeArguments.size(); i++) {
        if (i > 0) {
          typeString.append(',');
        }
        typeString.append('?');
      }
      typeString.append('>');
      if (typeString.toString().equals(otherType)) {
        return true;
      }
    }
    Element element = declaredType.asElement();
    if (!(element instanceof TypeElement)) {
      return false;
    }
    TypeElement typeElement = (TypeElement) element;
    TypeMirror superType = typeElement.getSuperclass();
    if (isSubtypeOfType(superType, otherType)) {
      return true;
    }
    for (TypeMirror interfaceType : typeElement.getInterfaces()) {
      if (isSubtypeOfType(interfaceType, otherType)) {
        return true;
      }
    }
    return false;
  }

  private BindingClass getOrCreateTargetClass(Map<TypeElement, BindingClass> targetClassMap,
      TypeElement enclosingElement) {
    BindingClass bindingClass = targetClassMap.get(enclosingElement);
    if (bindingClass == null) {
      TypeName targetType = TypeName.get(enclosingElement.asType());
      if (targetType instanceof ParameterizedTypeName) {
        targetType = ((ParameterizedTypeName) targetType).rawType;
      }

      String packageName = getPackageName(enclosingElement);
      ClassName classFqcn = ClassName.get(packageName,
          getClassName(enclosingElement, packageName) + BINDING_CLASS_SUFFIX);

      boolean isFinal = enclosingElement.getModifiers().contains(Modifier.FINAL);

      bindingClass = new BindingClass(targetType, classFqcn, isFinal);
      targetClassMap.put(enclosingElement, bindingClass);
    }
    return bindingClass;
  }

  private static String getClassName(TypeElement type, String packageName) {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
  }

  /** Finds the parent binder type in the supplied set, if any. */
  private TypeElement findParentType(TypeElement typeElement, Set<TypeElement> parents) {
    TypeMirror type;
    while (true) {
      type = typeElement.getSuperclass();
      if (type.getKind() == TypeKind.NONE) {
        return null;
      }
      typeElement = (TypeElement) ((DeclaredType) type).asElement();
      if (parents.contains(typeElement)) {
        return typeElement;
      }
    }
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private void error(Element element, String message, Object... args) {
    if (args.length > 0) {
      message = String.format(message, args);
    }
    processingEnv.getMessager().printMessage(ERROR, message, element);
  }

  private String getPackageName(TypeElement type) {
    return elementUtils.getPackageOf(type).getQualifiedName().toString();
  }

  private static boolean hasAnnotationWithName(Element element, String simpleName) {
    for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
      String annotationName = mirror.getAnnotationType().asElement().getSimpleName().toString();
      if (simpleName.equals(annotationName)) {
        return true;
      }
    }
    return false;
  }

  private static boolean isFieldRequired(Element element) {
    return !hasAnnotationWithName(element, NULLABLE_ANNOTATION_NAME);
  }

  private static boolean isListenerRequired(ExecutableElement element) {
    return element.getAnnotation(Optional.class) == null;
  }
}
