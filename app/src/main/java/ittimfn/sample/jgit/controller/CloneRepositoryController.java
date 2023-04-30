package ittimfn.sample.jgit.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.core.util.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import ittimfn.sample.jgit.enums.AuthPropertiesEnum;
import lombok.Getter;

@Getter
public class CloneRepositoryController {

    private String repoUrl;
    private String user;
    private String token;

    private Path repoDir;
    private Repository repository;
    private Git git;

    public CloneRepositoryController(String url, String user, String token) throws IOException {
        this.user = user;
        this.token = token;
        this.repoDir = Files.createTempDirectory(null);
        this.repoUrl = url;
    }

    public void gitClone() throws IOException, InvalidRemoteException, TransportException, GitAPIException {

        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(this.user, this.token);
        this.git = Git.cloneRepository()
                      .setURI(this.repoUrl)
                      .setDirectory(this.repoDir.toFile())
                      .setCredentialsProvider(cp)
                      .setCloneAllBranches(true)
                      .call();

        this.repository = this.git.getRepository();
    }

    public void rmdir() throws IOException {
        this.repoDir.toFile().deleteOnExit();
    }

}
