package se.effectivecode.tools.deploymentserver.io.repository;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.configuration.ProjectHolder;
import se.effectivecode.tools.deploymentserver.io.maven.MavenHandler;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class GitRepositoryHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(GitRepositoryHandler.class);
    public static final String DOT = ".";
    public static final String DASH = "/";
    private static final CharSequence ORIGIN = "refs/remotes/origin/";
    public static final String MASTER = "master";
    String username = "";
    String password = "";
    private String REPO_PATH = "repo";

    public Project downloadRepository(URL repositoryUrl) {
        String repoName = extractRepoName(repositoryUrl);
        Project project = null;
        try {
            String pathName = REPO_PATH + File.separator + repoName;
            File gitFile = new File(pathName);
            if (!gitFile.exists()) {
                Git.cloneRepository().setURI(String.valueOf(repositoryUrl.toURI()))
                        .setDirectory(gitFile).setCredentialsProvider(getCredential())
                        .call().close();
            }
            project = Project.builder()
                    .gitURL(repositoryUrl)
                    .name(repoName)
                    .gitAbsolutePath(gitFile.getAbsolutePath())
                    .build();
            readProjectInformationFromRepository(project);
            ProjectHolder.getInstance().addProject(project);
        } catch (GitAPIException gite) {
            LOGGER.debug("GitAPIException ... ", gite);
        } catch (URISyntaxException urie) {
            LOGGER.debug("Wrong format on URI", urie);
        }
        return project;
    }


    public void getBranches(Project project) {
        List<Ref> refList = null;
        try {
            ListBranchCommand listBranchCommand = Git.open(project.getGitDirectory()).branchList().setListMode(ListBranchCommand.ListMode.ALL);
            refList = listBranchCommand.call();
            LOGGER.debug("RefList size: " + refList.size());
            for (Ref ref : refList) {
                LOGGER.debug("Ref [" + ToStringBuilder.reflectionToString(ref) + "]");
                LOGGER.debug("Branch name [" + ref.getName() + "]");
                if (ref.getName().contains(ORIGIN)) {
                    String name = ref.getName();
                    String branchName = name.substring(ORIGIN.length());
                    project.addBranch(branchName);
                    LOGGER.debug("Added branch to project [" + branchName + "]");
                }
            }
            project.setCheckedOutBranch(MASTER);
        } catch (GitAPIException gae) {
            LOGGER.debug("Problem to access the git local or server", gae);
        } catch (IOException ioe) {
            LOGGER.error("IOException, should not be happend here", ioe);
        }
    }

    public boolean checkoutBranch(Project project, String branch) {
        boolean checkoutStatus = false;
        try {
            Ref ref = Git.open(getGitDir(project))
                    .checkout()
                    .setCreateBranch(true)
                    .setName(branch)
                    .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                    .setStartPoint("origin/" + branch).call();

            LOGGER.debug(ToStringBuilder.reflectionToString(ref));
            checkoutStatus = true;
        } catch (GitAPIException gae) {
            LOGGER.debug("Problem to access the git local or server", gae);
        } catch (IOException ioe) {
            LOGGER.error("IOException, should not be happend here", ioe);
        }
        return checkoutStatus;
    }

    public boolean updateProject(Project project) {
        boolean result = false;
        Git open = null;
        try {
            open = Git.open(getGitDir(project));
            PullResult call = open.pull().setCredentialsProvider(getCredential()).call();
            result = call.isSuccessful();
            open.close();
        } catch (IOException ioe) {
            LOGGER.debug("Problem with the I/O functionality", ioe);
        } catch (InvalidRefNameException irne) {
            LOGGER.error("There exist no such repo " + project.toString(), irne);
        } catch (CheckoutConflictException cce) {
            LOGGER.error("There is a conflict. ", cce);
        } catch (RefAlreadyExistsException raee) {
            LOGGER.debug("This should not happend ", raee);
        } catch (RefNotFoundException rnfe) {
            LOGGER.error("Reference Git Repo can not be located", rnfe);
        } catch (GitAPIException gapie) {
            LOGGER.debug("Some problem med JGIT library", gapie);
        } finally {
            if (open != null) {
                open.close();
            }
        }
        return result;
    }

    private File getGitDir(Project project) {
        return project.getGitDirectory();
    }

    String extractRepoName(URL repositoryUrl) {
        String externalForm = repositoryUrl.toExternalForm();
        int lastIndexOfDash = externalForm.lastIndexOf(DASH);
        String lastPartWithDot = externalForm.substring(lastIndexOfDash + 1, externalForm.length());
        int dot = lastPartWithDot.indexOf(DOT);
        return lastPartWithDot.substring(0, dot);
    }

    /**
     * Read project information from the repository. Will read the first pom.xml file.
     * From that file, extract information about the ping, dbPing, Version and artifactname.
     *
     * @param project the project that should be updated with some maven information.
     */
    public void readProjectInformationFromRepository(Project project) {
        File gitFilePath = project.getGitDirectory();
        LOGGER.debug("gitFilePath " + gitFilePath.getAbsolutePath());
        MavenHandler mavenHandler = new MavenHandler();
        mavenHandler.updateProjectWithMavenInformation(project);
    }

    CredentialsProvider getCredential() {
		if(username.equals("") || password.equals("")){
			throw new IllegalArgumentException("Username of password have not been compiled into the project.\nThis needs to be set before using the system.");
		} // Bad temporary solution. Should be config in a file for the startup, or as properties in the startup. But will work for the present time.
        return new UsernamePasswordCredentialsProvider(username, password);
    }
}