package pl.mjedynak.idea.plugins.builder.gui;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.RecentsManager;
import com.intellij.ui.ReferenceEditorComboWithBrowseButton;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import pl.mjedynak.idea.plugins.builder.factory.PackageChooserDialogFactory;
import pl.mjedynak.idea.plugins.builder.factory.ReferenceEditorComboWithBrowseButtonFactory;
import pl.mjedynak.idea.plugins.builder.gui.helper.GuiHelper;
import pl.mjedynak.idea.plugins.builder.psi.PsiHelper;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class CreateBuilderDialog extends DialogWrapper {

    static final String RECENTS_KEY = "CreateBuilderDialog.RecentsKey";
    private static final int WIDTH = 40;

    private PsiHelper psiHelper;
    private GuiHelper guiHelper;
    private Project project;
    private PsiDirectory targetDirectory;
    private PsiClass sourceClass;
    private JTextField targetClassNameField;
    private JTextField targetMethodPrefix;
    private JCheckBox innerBuilder;
    private JCheckBox butMethod;
    private JCheckBox useSingleField;
    private JCheckBox hasPrivateConstructor;
    private ReferenceEditorComboWithBrowseButton targetPackageField;
    private PsiClass existingBuilder;
    private String builderSuffix;

    public CreateBuilderDialog(Project project,
                               String title,
                               PsiClass sourceClass,
                               String builderSuffix,
                               String methodPrefix,
                               PsiPackage targetPackage,
                               PsiHelper psiHelper,
                               GuiHelper guiHelper,
                               ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory,
                               PsiClass existingBuilder) {
        super(project, true);
        this.psiHelper = psiHelper;
        this.guiHelper = guiHelper;
        this.project = project;
        this.sourceClass = sourceClass;
        this.existingBuilder = existingBuilder;
        this.builderSuffix = builderSuffix;
        targetClassNameField = new JTextField(builderSuffix);
        targetMethodPrefix = new JTextField(methodPrefix);
        setPreferredSize(targetClassNameField);
        setPreferredSize(targetMethodPrefix);

        String targetPackageName = (targetPackage != null) ? targetPackage.getQualifiedName() : "";
        targetPackageField = referenceEditorComboWithBrowseButtonFactory.getReferenceEditorComboWithBrowseButton(project, targetPackageName, RECENTS_KEY);
        targetPackageField.addActionListener(new ChooserDisplayerActionListener(targetPackageField, new PackageChooserDialogFactory(), project));
        setTitle(title);
    }

    @Override
    public void show() {
        super.init();
        super.show();
    }

    private void setPreferredSize(JTextField field) {
        Dimension size = field.getPreferredSize();
        FontMetrics fontMetrics = field.getFontMetrics(field.getFont());
        size.width = fontMetrics.charWidth('a') * WIDTH;
        field.setPreferredSize(size);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction(), getCancelAction(), getHelpAction()};
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbConstraints = new GridBagConstraints();

        panel.setBorder(IdeBorderFactory.createBorder());

        // Class name
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Class name"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(targetClassNameField, gbConstraints);
        targetClassNameField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                getOKAction().setEnabled(JavaPsiFacade.getInstance(project).getNameHelper().isIdentifier(getClassName()));
            }
        });
        // Class name

        // Method prefix
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 2;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Method prefix"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(targetMethodPrefix, gbConstraints);
        // Method prefix

        // Destination package
        gbConstraints.gridx = 0;
        gbConstraints.gridy = 3;
        gbConstraints.weightx = 0;
        gbConstraints.gridwidth = 1;
        panel.add(new JLabel(CodeInsightBundle.message("dialog.create.class.destination.package.label")), gbConstraints);

        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;

        targetPackageField.setEnabled(false);

        AnAction clickAction = new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                targetPackageField.getButton().doClick();
            }
        };
        clickAction.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK)),
                targetPackageField.getChildComponent());

        addInnerPanelForDestinationPackageField(panel, gbConstraints);
        // Destination package

        // Inner builder
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridy = 4;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Inner builder"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;

        innerBuilder = new JCheckBox();
        innerBuilder.setSelected(true);
        innerBuilder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetPackageField.setEnabled(!innerBuilder.isSelected());
                targetClassNameField.setText(innerBuilder.isSelected() ? getBuilderSuffix() : sourceClass.getName() + getBuilderSuffix());
            }
        });
        panel.add(innerBuilder, gbConstraints);
        // Inner builder


        // but method
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridy = 5;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("'but' method"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        butMethod = new JCheckBox();
        panel.add(butMethod, gbConstraints);
        // but method


        // useSingleField
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridy = 6;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Use single field"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        useSingleField = new JCheckBox();
        panel.add(useSingleField, gbConstraints);
        // useSingleField


        // hasPrivateConstructor
        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 0;
        gbConstraints.weightx = 0;
        gbConstraints.gridy = 7;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Private constructor"), gbConstraints);

        gbConstraints.insets = new Insets(4, 8, 4, 8);
        gbConstraints.gridx = 1;
        gbConstraints.weightx = 1;
        gbConstraints.gridwidth = 1;
        gbConstraints.fill = GridBagConstraints.HORIZONTAL;
        gbConstraints.anchor = GridBagConstraints.WEST;
        hasPrivateConstructor = new JCheckBox();
        panel.add(hasPrivateConstructor, gbConstraints);
        // hasPrivateConstructor

        return panel;
    }

    private void addInnerPanelForDestinationPackageField(JPanel panel, GridBagConstraints gbConstraints) {
        JPanel innerPanel = createInnerPanelForDestinationPackageField();
        panel.add(innerPanel, gbConstraints);
    }

    private JPanel createInnerPanelForDestinationPackageField() {
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(targetPackageField, BorderLayout.CENTER);
        return innerPanel;
    }

    @Override
    protected void doOKAction() {
        registerEntry(RECENTS_KEY, targetPackageField.getText());
        Module module = psiHelper.findModuleForPsiClass(sourceClass, project);
        if (module == null) {
            throw new IllegalStateException("Cannot find module for class " + sourceClass.getName());
        }
        try {
            checkIfSourceClassHasZeroArgsConstructorWhenUsingSingleField();
            checkIfClassCanBeCreated(module);
            callSuper();
        } catch (IncorrectOperationException e) {
            guiHelper.showMessageDialog(project, e.getMessage(), CommonBundle.getErrorTitle(), Messages.getErrorIcon());
        }
    }

    void checkIfSourceClassHasZeroArgsConstructorWhenUsingSingleField() {
        if (useSingleField()) {
            PsiMethod[] constructors = sourceClass.getConstructors();
            if(constructors.length == 0){
                return;
            }
            for (PsiMethod constructor : constructors) {
                if (constructor.getParameterList().getParametersCount() == 0) {
                    return;
                }
            }
            throw new IncorrectOperationException(String.format("%s must define a default constructor", sourceClass.getName()));
        }
    }

    void checkIfClassCanBeCreated(Module module) {
        if (!isInnerBuilder()) {
            SelectDirectory selectDirectory = new SelectDirectory(this, psiHelper, module, getPackageName(), getClassName(), existingBuilder);
            executeCommand(selectDirectory);
        }
    }

    void registerEntry(String key, String entry) {
        RecentsManager.getInstance(project).registerRecentEntry(key, entry);
    }

    void callSuper() {
        super.doOKAction();
    }

    void executeCommand(SelectDirectory selectDirectory) {
        CommandProcessor.getInstance().executeCommand(project, selectDirectory, CodeInsightBundle.message("create.directory.command"), null);
    }

    private String getPackageName() {
        String name = targetPackageField.getText();
        return (name != null) ? name.trim() : "";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return targetClassNameField;
    }

    public String getClassName() {
        return targetClassNameField.getText();
    }

    public String getBuilderSuffix() { return builderSuffix; }

    public String getMethodPrefix() {
        return targetMethodPrefix.getText();
    }

    public boolean isInnerBuilder() {
        return innerBuilder.isSelected();
    }

    public boolean hasButMethod() {
        return butMethod.isSelected();
    }

    public boolean useSingleField() { return useSingleField.isSelected(); }

    public boolean hasPrivateConstructor() { return hasPrivateConstructor.isSelected(); }

    public PsiDirectory getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(PsiDirectory targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public static final class Builder {
        private Project project;
        private String title;
        private PsiClass sourceClass;
        private String builderSuffix;
        private String methodPrefix;
        private PsiPackage targetPackage;
        private PsiHelper psiHelper;
        private GuiHelper guiHelper;
        private ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory;
        private PsiClass existingBuilder;

        public Builder withProject(Project project) {
            this.project = project;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withSourceClass(PsiClass sourceClass) {
            this.sourceClass = sourceClass;
            return this;
        }

        public Builder withBuilderSuffix(String builderSuffix) {
            this.builderSuffix = builderSuffix;
            return this;
        }

        public Builder withMethodPrefix(String methodPrefix) {
            this.methodPrefix = methodPrefix;
            return this;
        }

        public Builder withTargetPackage(PsiPackage targetPackage) {
            this.targetPackage = targetPackage;
            return this;
        }

        public Builder withPsiHelper(PsiHelper psiHelper) {
            this.psiHelper = psiHelper;
            return this;
        }

        public Builder withGuiHelper(GuiHelper guiHelper) {
            this.guiHelper = guiHelper;
            return this;
        }

        public Builder withReferenceEditorComboWithBrowseButtonFactory(ReferenceEditorComboWithBrowseButtonFactory referenceEditorComboWithBrowseButtonFactory) {
            this.referenceEditorComboWithBrowseButtonFactory = referenceEditorComboWithBrowseButtonFactory;
            return this;
        }

        public Builder withExistingBuilder(PsiClass existingBuilder) {
            this.existingBuilder = existingBuilder;
            return this;
        }

        public CreateBuilderDialog build() {
            return new CreateBuilderDialog(project, title, sourceClass, builderSuffix, methodPrefix, targetPackage, psiHelper, guiHelper, referenceEditorComboWithBrowseButtonFactory, existingBuilder);
        }
    }
}