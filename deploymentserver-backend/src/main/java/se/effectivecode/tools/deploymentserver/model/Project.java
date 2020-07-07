package se.effectivecode.tools.deploymentserver.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@ApiModel(value = "Project", description = "Information about a project")
public class Project {
    /*
     * Primarykey for each project.
     */
    @ApiModelProperty(notes = "The url to the git project", required = true)
    private URL gitURL;
    private String version;
    private String name;
    private String repoName;
    private String gitAbsolutePath;
    private String pingStatus;
    private String pingDbStatus;
    private URL ping;
    private URL dbPing;
    private List<String> branches = new ArrayList<>();
    private String checkedOutBranch;

    public static ProjectBuilder builder() {
        return new ProjectBuilder();
    }

    public File getGitDirectory() {
        return new File(gitAbsolutePath);
    }

    public void addBranch(String branchName) {
        if (branches == null) {
            branches = new ArrayList<>();
        }
        branches.add(branchName);
    }
}