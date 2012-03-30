package org.codefirst.jenkins.gemserverplugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.DirScanner;
import hudson.util.DirScanner.Glob;
import hudson.util.FileVisitor;

import java.io.File;
import java.io.IOException;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class GemServerNotifier extends Notifier {
    @DataBoundConstructor
    public GemServerNotifier() {
        super();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        final GemDirectory gemsDirectory = new GemDirectory(new File(getDescriptor().getGemsDirectoryPath()));
        gemsDirectory.mkdirs();

        // copy *.gem files
        File artifactsDir = build.getArtifactsDir();
        Glob glob = new DirScanner.Glob("**/*.gem", null);
        glob.scan(artifactsDir, new FileVisitor() {
            @Override
            public void visit(File f, String relativePath) throws IOException {
                FileUtils.copyFile(f, new File(gemsDirectory.getGemsSubDirectory(), f.getName()));
            }
        });

        // re-index
        gemsDirectory.generateIndex(getDescriptor().getGemPath());
        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    // This indicates to Jenkins that this is an implementation of an extension
    // point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private String gemPath = "gem";
        private String gemsDirectoryPath = "/tmp/gems";

        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project
            // types
            return true;
        }

        public String getDisplayName() {
            return "Copy artifact gems to Gem Server";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            gemsDirectoryPath = formData.getString("gemsDirectoryPath");
            gemPath = formData.getString("gemPath");
            save();
            return super.configure(req, formData);
        }

        /**
         * Get the gemPath.
         * @return the gemPath
         */
        public String getGemPath() {
            return gemPath;
        }

        /**
         * Get the gemsDirectoryPath.
         * @return the gemsDirectoryPath
         */
        public String getGemsDirectoryPath() {
            return gemsDirectoryPath;
        }

        /**
         * Set the gemPath.
         * @param gemPath the gemPath to set
         */
        public void setGemPath(String gemPath) {
            this.gemPath = gemPath;
        }

        /**
         * Set the gemsDirectoryPath.
         * @param gemsDirectoryPath the gemsDirectoryPath to set
         */
        public void setGemsDirectoryPath(String gemsDirectoryPath) {
            this.gemsDirectoryPath = gemsDirectoryPath;
        }

    }

}
