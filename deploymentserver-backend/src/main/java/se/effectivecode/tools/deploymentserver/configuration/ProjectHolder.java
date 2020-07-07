package se.effectivecode.tools.deploymentserver.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.effectivecode.tools.deploymentserver.util.StringUtil;
import se.effectivecode.tools.deploymentserver.io.projectfile.DeploymentProject;
import se.effectivecode.tools.deploymentserver.model.Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectHolder {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectHolder.class);
    private final static List<Project> currentProject = new ArrayList<>();
    private static ProjectHolder instance = null;
    private final static DeploymentProject dProject = new DeploymentProject();
    private final static StringUtil util = new StringUtil();

    private ProjectHolder() {
        readProjectList();
    }

    public static ProjectHolder getInstance() {
        if (instance == null) {
            instance = new ProjectHolder();
        }
        return instance;
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(currentProject);
    }

    public void removeProject(Project project) {
        currentProject.remove(project);
        saveProjectList();
    }

    public void addProject(Project project) {
        if (util.isNotEmpty(project.getGitURL().toExternalForm())) {
            if (getProject(project.getGitURL().toExternalForm()) == null) {
                LOGGER.debug("Time to save project to the list ", project);
                currentProject.add(project);
            }
        }
        saveProjectList();
    }

    private void saveProjectList() {
        LOGGER.debug("Save Project to File");
        dProject.writeToXML(currentProject);
    }

    private void readProjectList() {
        List<Project> projects = dProject.readProjectsFromFile();
        clear();
        currentProject.addAll(projects);
    }

    void clear() {
        currentProject.clear();
    }

    public void updateProject(Project project) {
        Project projectToRemove = getProject(project.getGitURL().toExternalForm());
        if (projectToRemove != null) {
            currentProject.remove(projectToRemove);
            currentProject.add(project);
            saveProjectList();
        }
    }

    public Project getProject(String projectGitUrl) {
        Project project = null;
        if (util.isNotEmpty(projectGitUrl)) {
            for (Project p : currentProject) {
                if (p.getGitURL().toExternalForm().equals(projectGitUrl)) {
                    project = p;
                }
            }
        }
        return project;
    }
}