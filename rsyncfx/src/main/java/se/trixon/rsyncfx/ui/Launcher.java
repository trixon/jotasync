/*
 * Copyright 2022 Patrik Karlström <patrik@trixon.se>.
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
package se.trixon.rsyncfx.ui;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import se.trixon.rsyncfx.Options;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class Launcher extends BorderPane {

    private LauncherGridView mLauncherGridView;
    private LauncherListView mLauncherListView;
    private final Options mOptions = Options.getInstance();

    public Launcher() {
        createUI();

        mOptions.getPreferences().addPreferenceChangeListener(pce -> {
            if (pce.getKey().equalsIgnoreCase(Options.KEY_LAUNCHER_MODE)) {
                Platform.runLater(() -> updateLauncherMode());
            }
        });

        updateLauncherMode();
    }

    private void createUI() {
        mLauncherGridView = new LauncherGridView();
        mLauncherListView = new LauncherListView();
    }

    private void updateLauncherMode() {
        if (mOptions.getInt(Options.KEY_LAUNCHER_MODE, Options.DEFAULT_LAUNCHER_MODE) == 0) {
            setCenter(mLauncherGridView.getNode());
        } else {
            setCenter(mLauncherListView.getNode());
        }
    }
}
