package org.emonic.base.buildmechanism;

import java.util.ArrayList;

// Build mechanisms which implement this interface deliver a build command which emonic uses 
// to build the project
public interface IBuildCommandRunner {
    ArrayList<String> getFullBuildCommand();
    //String getTargetBuildCommand(String affectedTargets);
    ArrayList<String> getTargetBuildCommand(String[] affectedTargets);
}
