package ittimfn.sample.jgit.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;

import lombok.Data;

@Data
public class MergeBaseSha1Controller {
    
    private Repository repository;

    public MergeBaseSha1Controller(Repository repository) {
        this.repository = repository;
    }

    public String getMergeBaseSha1(String targetBranch, String sourceBranch) throws IOException, RefAlreadyExistsException, RefNotFoundException, InvalidRefNameException, GitAPIException, InterruptedException {
        String sha1 = null;
        
        CreateBranchController.createBranch(this.repository, targetBranch);
        CreateBranchController.createBranch(this.repository, sourceBranch);
        
        File gitBash = createGitMergeBaseShell(targetBranch, sourceBranch);
        try {
            sha1 = this.bashExec(gitBash);
        } finally {
            gitBash.deleteOnExit();
        }

        return sha1;
    }

    private File createGitMergeBaseShell(String targetBranch, String sourceBranch) throws IOException {
        File gitMergeBase = File.createTempFile("jgit", ".sh");
        try(BufferedWriter writer = Files.newBufferedWriter(gitMergeBase.toPath(), Charset.forName("UTF-8"), StandardOpenOption.APPEND)) {
            writer.write("#!/bin/bash\n");
            writer.write("cd " + repository.getDirectory() + "\n");
            writer.write(String.format("git merge-base %s %s\n", targetBranch, sourceBranch));
        }
        return gitMergeBase;
    }

    private String bashExec(File gitBash) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder("bash", gitBash.toString());
        Process process = pb.start();
        String sha1 = null;
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            // 最大1行の想定。
            sha1 = buffer.readLine();
        }
        process.waitFor();
        return sha1;
    }

}
