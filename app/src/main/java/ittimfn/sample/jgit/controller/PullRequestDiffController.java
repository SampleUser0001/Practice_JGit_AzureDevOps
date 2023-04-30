package ittimfn.sample.jgit.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;

public class PullRequestDiffController {
    
    private Repository repository;

    public PullRequestDiffController(Repository repository) {
        this.repository = repository;
    }

    public List<String> diff(String targetBranch, String sourceBranch) throws IOException, InterruptedException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException {
        CreateBranchController.createBranch(this.repository, targetBranch);
        CreateBranchController.createBranch(this.repository, sourceBranch);

        File gitBash = this.createGitDiffShell(targetBranch, sourceBranch);
        try {
            return this.bashExec(gitBash);
        } finally {
            gitBash.deleteOnExit();
        }
    }

    private File createGitDiffShell(String targetBranch, String sourceBranch) throws IOException {
        File gitMergeBase = File.createTempFile("jgit", ".sh");
        try(BufferedWriter writer = Files.newBufferedWriter(gitMergeBase.toPath(), Charset.forName("UTF-8"), StandardOpenOption.APPEND)) {
            writer.write("#!/bin/bash\n");
            writer.write("cd " + this.repository.getDirectory() + "\n");
            writer.write(String.format("git diff --name-status %s...%s\n", targetBranch, sourceBranch));
        }
        return gitMergeBase;
    }

    private List<String> bashExec(File gitBash) throws InterruptedException, IOException {
        List<String> diffList = new ArrayList<String>();

        ProcessBuilder pb = new ProcessBuilder("bash", gitBash.toString());
        Process process = pb.start();
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            diffList = buffer.lines()
                             .toList();
                  
        }
        process.waitFor();
        return diffList;
    }

}
