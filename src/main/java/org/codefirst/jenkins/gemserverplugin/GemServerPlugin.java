package org.codefirst.jenkins.gemserverplugin;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.RootAction;
import hudson.model.Hudson;
import hudson.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

@Extension
public class GemServerPlugin extends Plugin implements RootAction {

    private static String GEM_DIR_PATH = "/tmp/gems";

    public String getIconFileName() {
        return "/plugin/gem-server-plugin/icons/gems-32x32.png";
    }

    public String getDisplayName() {
        return "Gem Server";
    }

    public String getUrlName() {
        return "gems";
    }

    @Override
    public void doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        String path = req.getRestOfPath();
        if (path.equals("/") || path.startsWith("/icons/")) {
            super.doDynamic(req, rsp);
            return;
        }

        InputStream is = getGemsDirectory().getInputStream(path);
        ServletOutputStream os = rsp.getOutputStream();
        IOUtils.copy(is, os);
        os.flush();
        is.close();
    }

    public GemDirectory getGemsDirectory() {
        GemServerNotifier.DescriptorImpl desc = Hudson.getInstance().getDescriptorByType(GemServerNotifier.DescriptorImpl.class);
        String gemsDirecotryPath = desc.getGemsDirectoryPath();
        return new GemDirectory(new File(gemsDirecotryPath));
    }

}
