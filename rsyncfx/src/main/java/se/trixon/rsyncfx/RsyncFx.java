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
package se.trixon.rsyncfx;

import com.dlsc.workbenchfx.Workbench;
import javafx.stage.Stage;
import se.trixon.almond.util.ExecutionFlow;
import se.trixon.almond.util.fx.FxHelper;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public class RsyncFx {

    public static final int ICON_SIZE_TOOLBAR = 32;

    private final ExecutionFlow mExecutionFlow = new ExecutionFlow();
    private Stage mStage;
    private Workbench mWorkbench;

    public static int getIconSizeToolBar() {
        return FxHelper.getUIScaled(ICON_SIZE_TOOLBAR);
    }

    public static int getIconSizeToolBarInt() {
        return (int) (getIconSizeToolBar() / 1.3);
    }

    public static RsyncFx getInstance() {
        return Holder.INSTANCE;
    }

    private RsyncFx() {
    }

    public ExecutionFlow getExecutionFlow() {
        return mExecutionFlow;
    }

    public Stage getStage() {
        return mStage;
    }

    public Workbench getWorkbench() {
        return mWorkbench;
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    public void setWorkbench(Workbench workbench) {
        mWorkbench = workbench;
    }

    private static class Holder {

        private static final RsyncFx INSTANCE = new RsyncFx();
    }
}