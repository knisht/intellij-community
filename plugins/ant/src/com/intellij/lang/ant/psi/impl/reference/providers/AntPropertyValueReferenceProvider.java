package com.intellij.lang.ant.psi.impl.reference.providers;

import com.intellij.lang.ant.psi.AntElement;
import com.intellij.lang.ant.psi.AntProperty;
import com.intellij.lang.ant.psi.impl.reference.AntPropertyReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.impl.source.resolve.reference.ReferenceType;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.GenericReferenceProvider;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AntPropertyValueReferenceProvider extends GenericReferenceProvider {

  @NotNull
  public PsiReference[] getReferencesByElement(PsiElement element) {
    AntElement antElement = (AntElement)element;
    final XmlAttribute[] attributes = antElement.getAttributes();
    if (attributes.length > 0) {
      List<PsiReference> refs = new ArrayList<PsiReference>();
      for (XmlAttribute attr : attributes) {
        getAttributeReferences(antElement, attr, refs);
      }
      if (refs.size() > 0) {
        return refs.toArray(new PsiReference[refs.size()]);
      }
    }
    return PsiReference.EMPTY_ARRAY;
  }

  private void getAttributeReferences(final AntElement element, final XmlAttribute attr, final List<PsiReference> refs) {
    final String value = attr.getValue();
    final int offsetInPosition = attr.getValueElement().getTextRange().getStartOffset() - element.getTextRange().getStartOffset() + 1;
    int startIndex;
    int endIndex = -1;
    while ((startIndex = value.indexOf("${", endIndex + 1)) > endIndex) {
      startIndex += 2;
      endIndex = value.indexOf('}', startIndex);
      if (endIndex < 0) break;
      if (endIndex > startIndex) {
        final String propName = value.substring(startIndex, endIndex);
        AntElement temp = element;
        AntProperty result = null;
        do {
          temp = temp.getAntParent();
          if (temp == null) break;
        }
        while ((result = temp.getProperty(propName)) == null);
        if (result != null) {
          refs.add(new AntPropertyReference(this, element, propName,
                                            new TextRange(offsetInPosition + startIndex, offsetInPosition + endIndex), attr));
        }
      }
    }
  }

  @NotNull
  public PsiReference[] getReferencesByElement(PsiElement element, ReferenceType type) {
    return getReferencesByElement(element);
  }

  @NotNull
  public PsiReference[] getReferencesByString(String str, PsiElement position, ReferenceType type, int offsetInPosition) {
    return getReferencesByElement(position);
  }

  public void handleEmptyContext(PsiScopeProcessor processor, PsiElement position) {
    handleElementRecursive(processor, ((AntElement)position).getAntProject());
  }

  private static boolean handleElementRecursive(PsiScopeProcessor processor, AntElement element) {
    if (element instanceof AntProperty) {
      AntProperty property = (AntProperty)element;
      if (!processor.execute(property, PsiSubstitutor.EMPTY)) {
        return false;
      }
    }
    for (PsiElement child : element.getChildren()) {
      if (!handleElementRecursive(processor, (AntElement)child)) {
        return false;
      }
    }
    return true;
  }
}
