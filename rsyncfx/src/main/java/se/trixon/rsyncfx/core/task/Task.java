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
package se.trixon.rsyncfx.core.task;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.openide.util.NbBundle;
import se.trixon.almond.util.Dict;
import se.trixon.rsyncfx.core.BaseItem;

/**
 *
 * @author Patrik Karlström
 */
public class Task extends BaseItem {

    private final transient List<String> mCommand = new ArrayList<>();
    @SerializedName("destination")
    private String mDestination;
    @SerializedName("environment")
    private String mEnvironment = "";
    @SerializedName("excludeSection")
    private final ExcludeSection mExcludeSection;
    @SerializedName("executeSection")
    private final TaskExecuteSection mExecuteSection;
    @SerializedName("noAdditionalDir")
    private boolean mNoAdditionalDir;
    @SerializedName("optionSection")
    private final OptionSection mOptionSection;
    @SerializedName("source")
    private String mSource;
    private transient StringBuilder mSummaryBuilder;

    public Task() {
        mExecuteSection = new TaskExecuteSection();
        mExcludeSection = new ExcludeSection();
        mOptionSection = new OptionSection();
    }

    public List<String> getCommand() {
        mCommand.clear();

        if (!StringUtils.isBlank(StringUtils.join(mOptionSection.getCommand(), ""))) {
            mCommand.addAll(mOptionSection.getCommand());
        }

        if (!StringUtils.isBlank(StringUtils.join(mExcludeSection.getCommand(), ""))) {
            mCommand.addAll(mExcludeSection.getCommand());
        }

        String source;
        String destination;

        if (SystemUtils.IS_OS_WINDOWS) {
            source = "/cygdrive/" + mSource.replace(":", "").replace("\\", "/");
            destination = "/cygdrive/" + mDestination.replace(":", "").replace("\\", "/");
        } else {
            source = mSource;
            destination = mDestination;
        }

        add(source);
        add(destination);

        return mCommand;
    }

    public String getCommandAsString() {
        return StringUtils.join(getCommand(), " ");
    }

    public String getDestination() {
        return mDestination;
    }

    public String getEnvironment() {
        return mEnvironment;
    }

    public ExcludeSection getExcludeSection() {
        return mExcludeSection;
    }

    public TaskExecuteSection getExecuteSection() {
        return mExecuteSection;
    }

    public OptionSection getOptionSection() {
        return mOptionSection;
    }

    public String getSource() {
        return mSource;
    }

    @Override
    public String getSummaryAsHtml() {
        mSummaryBuilder = new StringBuilder("<h2>").append(getName()).append("</h2>");

        addOptionalToSummary(true, getSource(), Dict.SOURCE.toString());
        addOptionalToSummary(true, getDestination(), Dict.DESTINATION.toString());

        var bundle = NbBundle.getBundle(Task.class);

        addOptionalToSummary(mExecuteSection.getBefore().isEnabled(), mExecuteSection.getBefore().getCommand(), bundle.getString("TaskExecutePanel.beforePanel.header"));
        if (mExecuteSection.getBefore().isEnabled() && mExecuteSection.getBefore().isHaltOnError()) {
            mSummaryBuilder.append(Dict.STOP_ON_ERROR.toString());
        }

        addOptionalToSummary(mExecuteSection.getAfterFail().isEnabled(), mExecuteSection.getAfterFail().getCommand(), bundle.getString("TaskExecutePanel.afterFailurePanel.header"));
        if (mExecuteSection.getAfterFail().isEnabled() && mExecuteSection.getAfterFail().isHaltOnError()) {
            mSummaryBuilder.append(Dict.STOP_ON_ERROR.toString());
        }

        addOptionalToSummary(mExecuteSection.getAfterOk().isEnabled(), mExecuteSection.getAfterOk().getCommand(), bundle.getString("TaskExecutePanel.afterSuccessPanel.header"));
        if (mExecuteSection.getAfterOk().isEnabled() && mExecuteSection.getAfterOk().isHaltOnError()) {
            mSummaryBuilder.append(Dict.STOP_ON_ERROR.toString());
        }

        addOptionalToSummary(mExecuteSection.getAfter().isEnabled(), mExecuteSection.getAfter().getCommand(), bundle.getString("TaskExecutePanel.afterPanel.header"));
        if (mExecuteSection.getAfter().isEnabled() && mExecuteSection.getAfter().isHaltOnError()) {
            mSummaryBuilder.append(Dict.STOP_ON_ERROR.toString());
        }

        if (mExecuteSection.isJobHaltOnError()) {
            mSummaryBuilder.append("<p>").append(bundle.getString("TaskExecutePanel.jobHaltOnErrorCheckBox.text")).append("</p>");
        }

        mSummaryBuilder.append("<h2>rsync</h2>").append(getCommandAsString());

        return mSummaryBuilder.toString();
    }

    public boolean isDryRun() {
        return mOptionSection.getCommand().contains("--dry-run");
    }

    public boolean isNoAdditionalDir() {
        return mNoAdditionalDir;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }

    public void setEnvironment(String environment) {
        mEnvironment = environment;
    }

    public void setNoAdditionalDir(boolean value) {
        mNoAdditionalDir = value;
    }

    public void setSource(String source) {
        mSource = source;
    }

    @Override
    public String toString() {
        return getName();
    }

    private void add(String command) {
        if (!mCommand.contains(command)) {
            mCommand.add(command);
        }
    }

    private void addOptionalToSummary(boolean active, String command, String header) {
        if (active) {
            mSummaryBuilder.append(String.format("<p><b>%s</b><br /><i>%s</i></p>", header, command));
        }
    }
}
