/*

    Copyright 2015-2016 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/
package com.github.terma.jenkins.githubprcoveragestatus;

import hudson.FilePath;
import hudson.Util;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;
import jenkins.MasterToSlaveFileCallable;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
final class GetCoverageCallable extends MasterToSlaveFileCallable<Float> implements CoverageRepository {

    private TaskListener listener;
    public GetCoverageCallable(TaskListener listener) {
        super();
        this.listener = listener;
    }

    private static List<SingleFileCoverageData> getCoverage(File ws, String path, CoverageReportParser parser) {
        FileSet fs = Util.createFileSet(ws, path);
        DirectoryScanner ds = fs.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        List<SingleFileCoverageData> cov = new ArrayList<SingleFileCoverageData>();
        for (String file : files) cov.add(parser.get(new File(ds.getBasedir(), file).getAbsolutePath()));
        return cov;
    }

    @Override
    public float get(final FilePath workspace) throws IOException, InterruptedException {
        if (workspace == null) throw new IllegalArgumentException("Workspace should not be null!");
        return workspace.act(new GetCoverageCallable(listener));
    }

    @Override
    public Float invoke(final File ws, final VirtualChannel channel) throws IOException {
        final List<SingleFileCoverageData> cov = new ArrayList<SingleFileCoverageData>();
        cov.addAll(getCoverage(ws, "**/cobertura.xml", new CoberturaParser()));
        cov.addAll(getCoverage(ws, "**/cobertura-coverage.xml", new CoberturaParser()));
        cov.addAll(getCoverage(ws, "**/jacoco.xml", new JacocoParser()));
        cov.addAll(getCoverage(ws, "**/jacocoTestReport.xml", new JacocoParser())); //default for gradle
        cov.addAll(getCoverage(ws, "**/clover.xml", new CloverParser()));
        final PrintStream buildLog = listener.getLogger();

        float missed = 0;
        float covered = 0;
        for (SingleFileCoverageData c : cov){
            missed += c.missedLines;
            covered += c.coveredLines;
            buildLog.println(c.getSummary());
        }

        if (cov.isEmpty() || (covered + missed == 0)) {
            return 0f;
        } else {
            return covered / (covered + missed);
        }
    }

}
