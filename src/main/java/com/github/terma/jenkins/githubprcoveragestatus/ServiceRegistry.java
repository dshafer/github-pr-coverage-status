package com.github.terma.jenkins.githubprcoveragestatus;

import hudson.model.TaskListener;

public class ServiceRegistry {

    private static MasterCoverageRepository masterCoverageRepository;
    private static CoverageRepository coverageRepository;
    private static SettingsRepository settingsRepository;
    private static PullRequestRepository pullRequestRepository;

    public static MasterCoverageRepository getMasterCoverageRepository() {
        return masterCoverageRepository != null ? masterCoverageRepository : Configuration.DESCRIPTOR;
    }

    public static void setMasterCoverageRepository(MasterCoverageRepository masterCoverageRepository) {
        ServiceRegistry.masterCoverageRepository = masterCoverageRepository;
    }

    public static CoverageRepository getCoverageRepository(TaskListener listener) {
        return coverageRepository != null ? coverageRepository : new GetCoverageCallable(listener);
    }

    public static void setCoverageRepository(CoverageRepository coverageRepository) {
        ServiceRegistry.coverageRepository = coverageRepository;
    }

    public static SettingsRepository getSettingsRepository() {
        return settingsRepository != null ? settingsRepository : Configuration.DESCRIPTOR;
    }

    public static void setSettingsRepository(SettingsRepository settingsRepository) {
        ServiceRegistry.settingsRepository = settingsRepository;
    }

    public static PullRequestRepository getPullRequestRepository() {
        return pullRequestRepository != null ? pullRequestRepository : new GitHubPullRequestRepository();
    }

    public static void setPullRequestRepository(PullRequestRepository pullRequestRepository) {
        ServiceRegistry.pullRequestRepository = pullRequestRepository;
    }
}
