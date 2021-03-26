package pl.mjedynak.idea.plugins.builder.factory;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiPackage;
import pl.mjedynak.idea.plugins.builder.gui.CreateBuilderDialog;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

public class CreateBuilderDialogFactory {

    private static final String BUILDER_SUFFIX = "Builder";
    private static final String METHOD_PREFIX = "with";
    private static final String DIALOG_NAME = "CreateBuilder";

    private PsiHelper psiHelper;
    private ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory;
    private GuiHelper guiHelper;

    public CreateBuilderDialogFactory(PsiHelper psiHelper, ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory, GuiHelper guiHelper) {
        this.psiHelper = psiHelper;
        this.referenceEditorComboWithBrowseButtonFactory = referenceEditorComboWithBrowseButtonFactory;
        this.guiHelper = guiHelper;
    }

    public CreateBuilderDialog createBuilderDialog(PsiClass sourceClass, Project project, PsiPackage srcPackage, PsiClass existingBuilder) {
        return new CreateBuilderDialog.Builder()
                .withProject(project)
                .withTitle(DIALOG_NAME)
                .withSourceClass(sourceClass)
                .withBuilderSuffix(BUILDER_SUFFIX)
                .withMethodPrefix(METHOD_PREFIX)
                .withTargetPackage(srcPackage)
                .withPsiHelper(psiHelper)
                .withGuiHelper(guiHelper)
                .withReferenceEditorComboWithBrowseButtonFactory(referenceEditorComboWithBrowseButtonFactory)
                .withExistingBuilder(existingBuilder)
                .build();
    }
}