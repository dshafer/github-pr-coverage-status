package com.github.terma.jenkins.githubprcoveragestatus;

/**
 * Created by ashafer on 1/13/17.
 */
class SingleFileCoverageData {
    public String fileName;
    public int coveredLines;
    public int missedLines;

    public SingleFileCoverageData() {
        coveredLines = 0;
        missedLines = 0;
    }

    public String getSummary() {
        return String.format("Covered=%d,Missed=%d file:%s", coveredLines, missedLines, fileName);
    }
}