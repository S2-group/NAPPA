package nl.vu.cs.s2group.nappa.plugin;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class Module extends ModuleBuilder {
    @Override
    public void setupRootModel(ModifiableRootModel modifiableRootModel) throws ConfigurationException {


    }

    @Override
    public ModuleType getModuleType() {
        return ModuleType.EMPTY;
    }
}
