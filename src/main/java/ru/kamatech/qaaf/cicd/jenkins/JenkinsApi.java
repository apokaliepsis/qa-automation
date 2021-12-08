package ru.kamatech.qaaf.cicd.jenkins;

import com.offbytwo.jenkins.JenkinsServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class JenkinsApi {
    private JenkinsServer jenkins;
    public void executeBuild(/*String url, String loginUser, String token, */String jobName, Map<String,String> parameterJob) {
        //jenkins = authorizeJenkins(url, loginUser, token);
        try {
            System.out.println(jenkins.getJob(jobName).build(parameterJob));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void authorizeJenkins(String url, String loginUser, String token) {

        try {
            jenkins = new JenkinsServer(new URI(url),
                    loginUser, token);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    public int getIdleExecutors(/*String url, String loginUser, String token, */String nodeName){
        //jenkins = authorizeJenkins(url, loginUser, token);
        int countExecutors = 0;
        try {
            countExecutors = jenkins.getLabel(nodeName).getIdleExecutors();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return countExecutors;
    }
}
