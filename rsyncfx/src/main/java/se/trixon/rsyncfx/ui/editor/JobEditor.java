/*
 * Copyright 2023 Patrik Karlström <patrik@trixon.se>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.trixon.rsyncfx.ui.editor;

import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.fx.control.FileChooserPane;
import se.trixon.almond.util.fx.control.FileChooserPane.ObjectMode;
import se.trixon.rsyncfx.core.JobManager;
import se.trixon.rsyncfx.core.job.Job;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class JobEditor extends BaseEditor<Job> {

    private Job mItem;
    private final JobManager mManager = JobManager.getInstance();
    private FileChooserPane mRunAfterFailFileChooser;
    private FileChooserPane mRunAfterFileChooser;
    private FileChooserPane mRunAfterOkFileChooser;
    private CheckBox mRunBeforeCheckBox;
    private FileChooserPane mRunBeforeFileChooser;

    public JobEditor() {
        createUI();
    }

    @Override
    public void load(Job item) {
        if (item == null) {
            item = new Job();
        }

        var executeSection = item.getExecuteSection();

        loadRun(mRunBeforeFileChooser, executeSection.isBefore(), executeSection.getBeforeCommand());
        loadRun(mRunAfterFailFileChooser, executeSection.isAfterFailure(), executeSection.getAfterFailureCommand());
        loadRun(mRunAfterOkFileChooser, executeSection.isAfterSuccess(), executeSection.getAfterSuccessCommand());
        loadRun(mRunAfterFileChooser, executeSection.isAfter(), executeSection.getAfterCommand());
        mRunBeforeCheckBox.setSelected(executeSection.isBeforeHaltOnError());

        super.load(item);
        mItem = item;
    }

    @Override
    public Job save() {
        var map = mManager.getIdToItem();
        map.putIfAbsent(mItem.getId(), mItem);

        var executeSection = mItem.getExecuteSection();
        executeSection.setBefore(mRunBeforeFileChooser.getCheckBox().isSelected());
        executeSection.setBeforeCommand(mRunBeforeFileChooser.getPathAsString());

        executeSection.setAfterFailure(mRunAfterFailFileChooser.getCheckBox().isSelected());
        executeSection.setAfterFailureCommand(mRunAfterFailFileChooser.getPathAsString());

        executeSection.setAfterSuccess(mRunAfterOkFileChooser.getCheckBox().isSelected());
        executeSection.setAfterSuccessCommand(mRunAfterOkFileChooser.getPathAsString());

        executeSection.setAfter(mRunAfterFileChooser.getCheckBox().isSelected());
        executeSection.setAfterCommand(mRunAfterFileChooser.getPathAsString());

        executeSection.setBeforeHaltOnError(mRunBeforeCheckBox.isSelected());

        return super.save();
    }

    private Tab createLogTab() {
        var root = new VBox();
        var tab = new Tab(Dict.LOGGING.toString(), root);

        return tab;
    }

    private Tab createRunTab() {

        var dialogTitle = mBundle.getString("JobEditor.selectFileToRun");
        var selectionMode = SelectionMode.SINGLE;
        var objectMode = ObjectMode.FILE;

        mRunBeforeFileChooser = new FileChooserPane(dialogTitle, objectMode, selectionMode, mBundle.getString("JobEditor.runBefore"));
        mRunBeforeCheckBox = new CheckBox(Dict.STOP_ON_ERROR.toString());
        mRunBeforeCheckBox.disableProperty().bind(mRunBeforeFileChooser.getCheckBox().selectedProperty().not());
        mRunAfterFailFileChooser = new FileChooserPane(dialogTitle, objectMode, selectionMode, mBundle.getString("JobEditor.runAfterFail"));
        mRunAfterOkFileChooser = new FileChooserPane(dialogTitle, objectMode, selectionMode, mBundle.getString("JobEditor.runAfterOk"));
        mRunAfterFileChooser = new FileChooserPane(dialogTitle, objectMode, selectionMode, mBundle.getString("JobEditor.runAfter"));

        var root = new VBox(FxHelper.getUIScaled(12),
                mRunBeforeFileChooser,
                mRunBeforeCheckBox,
                mRunAfterFailFileChooser,
                mRunAfterOkFileChooser,
                mRunAfterFileChooser
        );

        FxHelper.setPadding(FxHelper.getUIScaledInsets(8, 0, 0, 0), mRunBeforeFileChooser, mRunAfterFailFileChooser);

        var tab = new Tab(Dict.RUN.toString(), root);

        return tab;
    }

    private void createUI() {
        getTabPane().getTabs().addAll(createRunTab(), createLogTab());
        getTabPane().getSelectionModel().select(1);
    }

    private void loadRun(FileChooserPane fcp, boolean selected, String command) {
        fcp.getCheckBox().setSelected(selected);
        fcp.setPath(command);
    }
}
