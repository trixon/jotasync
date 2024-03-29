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
package se.trixon.jotasync.ui.editor;

import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.ListActionView;
import org.controlsfx.control.ListSelectionView;
import org.openide.DialogDescriptor;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.almond.util.icons.material.MaterialIcon;
import se.trixon.jotasync.core.JobManager;
import se.trixon.jotasync.core.TaskManager;
import se.trixon.jotasync.core.job.Job;
import se.trixon.jotasync.core.task.Task;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class JobEditor extends BaseEditor<Job> {

    private Job mItem;
    private ListSelectionView<Task> mListSelectionView;
    private RunSectionPane mRunAfterFailSection;
    private RunSectionPane mRunAfterOkSection;
    private RunSectionPane mRunAfterSection;
    private RunSectionPane mRunBeforeSection;

    public JobEditor() {
        super(JobManager.getInstance());
        createUI();
    }

    @Override
    public void load(Job item, DialogDescriptor dialogDescriptor) {
        if (item == null) {
            item = new Job();
        }

        var execute = item.getExecuteSection();
        mRunBeforeSection.load(execute.getBefore());
        mRunAfterFailSection.load(execute.getAfterFail());
        mRunAfterOkSection.load(execute.getAfterOk());
        mRunAfterSection.load(execute.getAfter());

        var tasks = item.getTasks();
        mListSelectionView.getSourceItems().removeAll(tasks);
        mListSelectionView.getTargetItems().setAll(tasks);

        super.load(item, dialogDescriptor);
        mItem = item;
    }

    @Override
    public Job save() {
        var map = mManager.getIdToItem();
        map.putIfAbsent(mItem.getId(), mItem);

        var execute = mItem.getExecuteSection();
        save(execute.getBefore(), mRunBeforeSection);
        save(execute.getAfterFail(), mRunAfterFailSection);
        save(execute.getAfterOk(), mRunAfterOkSection);
        save(execute.getAfter(), mRunAfterSection);

        var taskIds = mListSelectionView.getTargetItems().stream()
                .map(task -> task.getId())
                .toList();
        mItem.setTaskIds(new ArrayList<>(taskIds));

        return super.save();
    }

    private ListActionView.ListAction[] createTaskTargetActions() {
        int imageSize = FxHelper.getUIScaled(16);

        return new ListActionView.ListAction[]{
            new ListActionView.ListAction<Task>(MaterialIcon._Navigation.EXPAND_LESS.getImageView(imageSize)) {
                @Override
                public void initialize(ListView<Task> listView) {
                    setEventHandler(event -> moveSelectedTasksUp(listView));
                }
            },
            new ListActionView.ListAction<Task>(MaterialIcon._Navigation.EXPAND_MORE.getImageView(imageSize)) {
                @Override
                public void initialize(ListView<Task> listView) {
                    setEventHandler(event -> moveSelectedTasksDown(listView));
                }
            }
        };
    }

    private void createUI() {
        mRunBeforeSection = new RunSectionPane(mBundle.getString("JobEditor.runBefore"), true, false);
        mRunAfterFailSection = new RunSectionPane(mBundle.getString("JobEditor.runAfterFail"), false, false);
        mRunAfterOkSection = new RunSectionPane(mBundle.getString("JobEditor.runAfterOk"), false, false);
        mRunAfterSection = new RunSectionPane(mBundle.getString("JobEditor.runAfter"), false, false);

        mListSelectionView = new ListSelectionView();
        mListSelectionView.setSourceHeader(new Label("%s %s".formatted(Dict.AVAILABLE.toString(), Dict.TASKS.toLower())));
        mListSelectionView.setTargetHeader(new Label("%s %s".formatted(Dict.SELECTED.toString(), Dict.TASKS.toLower())));
        mListSelectionView.getSourceItems().addAll(TaskManager.getInstance().getItems());
        mListSelectionView.getTargetActions().addAll(createTaskTargetActions());

        int row = 0;
        var gp = new GridPane(FxHelper.getUIScaled(8), FxHelper.getUIScaled(8));
        gp.add(mRunBeforeSection, 0, row++, GridPane.REMAINING, 1);
        gp.add(mListSelectionView, 0, row++, GridPane.REMAINING, 1);
        gp.addRow(row++, mRunAfterFailSection, mRunAfterOkSection);
        gp.add(mRunAfterSection, 0, row++, GridPane.REMAINING, 1);
        FxHelper.autoSizeColumn(gp, 2);
        GridPane.setVgrow(mListSelectionView, Priority.ALWAYS);
        FxHelper.setPadding(FxHelper.getUIScaledInsets(8, 0, 8, 0), gp);
        FxHelper.setPadding(FxHelper.getUIScaledInsets(8, 0, 0, 0),
                mRunBeforeSection,
                mListSelectionView,
                mRunAfterSection
        );

        setCenter(gp);
    }

    private void moveSelectedTasksDown(ListView<Task> listView) {
        var items = listView.getItems();
        var selectionModel = listView.getSelectionModel();
        var selectedIndices = selectionModel.getSelectedIndices();
        int lastIndex = items.size() - 1;

        for (int index = selectedIndices.size() - 1; index >= 0; index--) {
            var selectedIndex = selectedIndices.get(index);
            if (selectedIndex < lastIndex) {
                if (selectedIndices.contains(selectedIndex + 1)) {
                    continue;
                }
                var item = items.get(selectedIndex);
                var itemToBeReplaced = items.get(selectedIndex + 1);
                items.set(selectedIndex + 1, item);
                items.set(selectedIndex, itemToBeReplaced);
                selectionModel.clearSelection(selectedIndex);
                selectionModel.select(selectedIndex + 1);
            }
        }
    }

    private void moveSelectedTasksUp(ListView<Task> listView) {
        var items = listView.getItems();
        var selectionModel = listView.getSelectionModel();
        var selectedIndices = selectionModel.getSelectedIndices();

        for (var selectedIndex : selectedIndices) {
            if (selectedIndex > 0) {
                if (selectedIndices.contains(selectedIndex - 1)) {
                    continue;
                }
                var item = items.get(selectedIndex);
                var itemToBeReplaced = items.get(selectedIndex - 1);
                items.set(selectedIndex - 1, item);
                items.set(selectedIndex, itemToBeReplaced);
                selectionModel.clearSelection(selectedIndex);
                selectionModel.select(selectedIndex - 1);
            }
        }
    }
}
