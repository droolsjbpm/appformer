package org.dashbuilder.client.screens.view;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.Response;
import org.dashbuilder.client.RuntimeCommunication;
import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.client.screens.UploadDashboardsScreen;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;

@Templated
@Dependent
public class UploadDashboardsScreenView implements UploadDashboardsScreen.View {

    private static final AppConstants i18n = AppConstants.INSTANCE;

    @Inject
    @DataField
    HTMLDivElement emptyImport;

    @Inject
    @DataField
    HTMLButtonElement btnImport;

    @Inject
    @DataField
    HTMLFormElement uploadForm;

    @Inject
    @DataField
    HTMLInputElement inputFile;

    @Inject
    BusyIndicatorView loading;

    @Inject
    RuntimeCommunication runtimeCommunication;

    private UploadDashboardsScreen presenter;

    @Override
    public HTMLElement getElement() {
        return emptyImport;
    }

    @Override
    public void init(UploadDashboardsScreen presenter) {
        this.presenter = presenter;

    }

    @EventHandler("inputFile")
    public void handleInputFileChange(ChangeEvent e) {
        presenter.submit(uploadForm);
    }

    @EventHandler("btnImport")
    public void handleImport(ClickEvent e) {
        inputFile.click();
    }

    @Override
    public void loading() {
        loading.showBusyIndicator(i18n.uploadingDashboards());
    }

    @Override
    public void stopLoading() {
        loading.hideBusyIndicator();
    }

    @Override
    public void badResponseUploading(Response response) {
        runtimeCommunication.showError(i18n.errorUploadingDashboards(), response);
    }

    @Override
    public void errorDuringUpload(Object error) {
        runtimeCommunication.showError(i18n.errorUploadingDashboards(), error);
    }
}