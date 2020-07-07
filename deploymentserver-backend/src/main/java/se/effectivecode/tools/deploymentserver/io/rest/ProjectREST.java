package se.effectivecode.tools.deploymentserver.io.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.effectivecode.tools.deploymentserver.configuration.ProjectHolder;
import se.effectivecode.tools.deploymentserver.io.docker.DockerController;
import se.effectivecode.tools.deploymentserver.io.repository.GitRepositoryHandler;
import se.effectivecode.tools.deploymentserver.io.rest.helper.RESTHelper;
import se.effectivecode.tools.deploymentserver.io.util.FileUtil;
import se.effectivecode.tools.deploymentserver.model.Project;
import se.effectivecode.tools.deploymentserver.util.StringUtil;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.net.URLDecoder;

@Controller
@RequestMapping("/project")
@RestController
public class ProjectREST {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectREST.class);
    private StringUtil util = new StringUtil();
    private RESTHelper restHelper = new RESTHelper();
    private GitRepositoryHandler gitHandler = new GitRepositoryHandler();
    private DockerController controller = new DockerController();

    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Return all projects", response = Project.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of projects", response = Project.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Something have gone wrong")
    })
    public String getProjects() {
        return fetchProject();
    }

    public String fetchProject() {
        LOGGER.debug("Inside FetchProjects");
        ProjectHolder instance = ProjectHolder.getInstance();
        LOGGER.debug("Amount of projects in the holder. [" + instance.getProjects().size() + "]");
        Response response = Response.ok(restHelper.toJSON(instance.getProjects())).build();
        LOGGER.debug("Response with all projects [" + new ToStringBuilder(response).toString() + "]");
        String returnString = restHelper.toJSON(instance.getProjects());
        LOGGER.debug("Project as JSON " + returnString);
        return returnString;
    }

    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "Return all projects", response = Project.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of projects", response = Project.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Something have gone wrong")
    })
    public String registerProject(@RequestParam("giturl") String gitUrl) {
        LOGGER.debug("RegisterProject [" + gitUrl + "]");
        String response = "";
        try {
            if (util.isNotEmpty(gitUrl)) {
                String decodeURL = URLDecoder.decode(gitUrl, "UTF-8");
                URL repositoryUrl = new URL(decodeURL);
                Project project = gitHandler.downloadRepository(repositoryUrl);
                if (project != null) {
                    LOGGER.debug("time to readProjectInformationFromRepository");
                    gitHandler.getBranches(project);
                    gitHandler.readProjectInformationFromRepository(project);
                    ProjectHolder instance = ProjectHolder.getInstance();
                    LOGGER.debug("Project to be added [" + project + "]");
                    instance.addProject(project);
                    LOGGER.debug("Amount of projects in the holder. [" + instance.getProjects().size() + "]");
                    response = fetchProject();
                }
            }
        } catch (Exception e) {
            LOGGER.debug("What happends here", e);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build().toString();
        }
        return response;
    }

    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "pong")
    })
    public String ping() {
        return "pong";
    }


    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/deploy", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "set giturl")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List with project"),
            @ApiResponse(code = 500, message = "Something have gone wrong")
    })
    public String deployProject(@RequestParam("giturl") String gitUrl, @RequestParam("branch") String branch) {
        String response = Response.status(Response.Status.NO_CONTENT).build().toString();
        LOGGER.debug("Incoming QueryParam [" + gitUrl + "]");
        try {
            if (util.isNotEmpty(gitUrl)) {
                String decodeURL = URLDecoder.decode(gitUrl, "UTF-8");
                Project project = ProjectHolder.getInstance().getProject(decodeURL);
                LOGGER.debug("Found project", project);
                gitHandler.updateProject(project);
                if (util.isNotEmpty(branch)) {
                    gitHandler.checkoutBranch(project, branch);
                }
                Boolean dockerResult = controller.startDocker(project);
                response = restHelper.toJSON(dockerResult);
            }
        } catch (Exception e) {
            LOGGER.debug("What happends here", e);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build().toString();
        }
        return response;
    }

    @Produces({MediaType.APPLICATION_JSON})
    @RequestMapping(value = "/remove", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "set giturl")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Project have been removed"),
            @ApiResponse(code = 500, message = "Something have gone wrong")
    })
    public String removeProject(@RequestParam("gitUrl") String gitUrl) {
        String response = Response.status(Response.Status.NO_CONTENT).build().toString();
        LOGGER.debug("Incoming QueryParam [" + gitUrl + "]");
        try {
            if (util.isNotEmpty(gitUrl)) {
                String decodeURL = URLDecoder.decode(gitUrl, "UTF-8");
                Project project = ProjectHolder.getInstance().getProject(decodeURL);
                LOGGER.debug("Found project that should be removed", project);
                boolean removeDockerStatus = controller.removeDocker(project);
                boolean hasGitProjectBeenRemoved = false;
                if (removeDockerStatus) {
                    hasGitProjectBeenRemoved = FileUtil.removeDirectory(project.getGitDirectory());
                }
                boolean removeStatus = false;
                if (removeDockerStatus && hasGitProjectBeenRemoved) {
                    ProjectHolder.getInstance().removeProject(project);
                    removeStatus = true;
                }
                response = restHelper.toJSON(removeStatus);
            }
        } catch (Exception e) {
            LOGGER.debug("What happends here", e);
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build().toString();
        }
        return response;
    }
}